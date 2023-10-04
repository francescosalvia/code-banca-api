package com.banca.project.service;

import com.banca.project.config.SandboxConfig;
import com.banca.project.dto.request.BonificoRequestDto;
import com.banca.project.dto.response.BonificoResponse;
import com.banca.project.dto.response.PayloadTransactionsResponse;
import com.banca.project.entity.Transaction;
import com.banca.project.exception.*;
import com.banca.project.mapper.TransactionMapper;
import com.banca.project.model.Error;
import com.banca.project.model.*;
import com.banca.project.repository.TransactionRepository;
import com.google.gson.Gson;
import liquibase.repackaged.org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RestService {

  public final String AUTH_SCHEMA = "Auth-Schema";

  public final String X_TIME_ZONE = "X-Time-Zone";
  public final String EUROPE_ROME = "Europe/Rome";

  public final String API_KEY = "apikey";

  private Gson gson = new Gson();
  public final String URL_BANKING = "/api/gbs/banking/v4.0/accounts/";

  public final String BALANCE = "/balance";

  public final String TRANSACTIONS = "/transactions";

  public final String BONIFICO = "/payments/money-transfers";

  public final String RESPONSE_KO = "KO";
  private final SandboxConfig config;

  private final TransactionMapper transactionMapper;

  private final TransactionRepository transactionRepository;

  @Autowired
  public RestService(
      SandboxConfig config,
      TransactionMapper transactionMapper,
      TransactionRepository transactionRepository) {
    this.config = config;
    this.transactionMapper = transactionMapper;
    this.transactionRepository = transactionRepository;
  }

  public void checkIdCustomer(String idCustomer) throws CustomerIdNotCorrectlyException {

    if (StringUtils.isBlank(idCustomer)) {
      log.error("account id [{}] is blank", idCustomer);
      throw new CustomerIdNotCorrectlyException(
          "account id is not correct", "account_id_not_valid");
    }
  }

  public void verifyDate(LocalDate fromDate, LocalDate toDate) throws EndDateNotCorrectlyException {

    if (toDate.isBefore(fromDate)) {
      throw new EndDateNotCorrectlyException("EndDate before StartDate", "end_date_not_correctly");
    }
  }

  public double getBalance(String accountId)
      throws GetBalanceNullException, GetBalanceBadRequestException, SandboxInternalErrorException,
          CustomerIdNotCorrectlyException {

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set(AUTH_SCHEMA, config.getAuthSchema());
    headers.set(API_KEY, config.getApiKey());

    final String url =
        UriComponentsBuilder.fromHttpUrl(config.getBasicUrl())
            .path(URL_BANKING)
            .path(accountId)
            .path(BALANCE)
            .encode()
            .toUriString();

    log.info("Calling banking balance service [{}]", url);

    try {
      ResponseEntity<String> response =
          restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

      String json = response.getBody();

      SaldoBankResponse saldoBankResponse = this.gson.fromJson(json, SaldoBankResponse.class);

      if (saldoBankResponse == null || StringUtils.isBlank(saldoBankResponse.getStatus())) {
        log.warn("Response for account id [{}] null", accountId);
        throw new GetBalanceNullException("Response is null", "response_null");
      }

      if (StringUtils.equalsIgnoreCase(saldoBankResponse.getStatus(), RESPONSE_KO)) {
        log.warn("Response for account id [{}] not valid", accountId);
        throw new GetBalanceBadRequestException("Response not valid", "response_ko");
      }

      log.info(
          "Balance for account id [{}] is [{}]",
          accountId,
          saldoBankResponse.getPayload().getBalance());

      return saldoBankResponse.getPayload().getBalance();

    } catch (HttpClientErrorException e) {

      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      if (StringUtils.equalsIgnoreCase(RESPONSE_KO, badRequestResponse.getStatus())
          && badRequestResponse.getErrors() != null) {
        for (int i = 0; i < badRequestResponse.getErrors().size(); i++) {
          Error error = badRequestResponse.getErrors().get(i);

          if (StringUtils.equalsIgnoreCase(error.getCode(), "REQ004")
              && StringUtils.equalsIgnoreCase(
                  error.getDescription(), "Invalid account identifier")) {
            log.error(error.getDescription());
            throw new CustomerIdNotCorrectlyException(
                "account id is not valid", "account_id_not_valid");
          }
        }
      }

      log.warn("Cannot banking account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    }
  }

  public List<PayloadTransactionsResponse> getTransactions(
      String accountId, LocalDate startDate, LocalDate endDate)
      throws GetBalanceNullException, GetBalanceBadRequestException, SandboxInternalErrorException,
          CustomerIdNotCorrectlyException {

    HttpHeaders headers = createHeader();

    final String url =
        UriComponentsBuilder.fromHttpUrl(config.getBasicUrl())
            .path(URL_BANKING)
            .path(accountId)
            .path(TRANSACTIONS)
            .queryParam("fromAccountingDate", startDate)
            .queryParam("toAccountingDate", endDate)
            .encode()
            .toUriString();

    log.info("Calling banking transactions service [{}]", url);

    try {
      String json = getMethod(url, headers);

      TransactionsBankResponse transactionsBankResponse =
          this.gson.fromJson(json, TransactionsBankResponse.class);

      if (transactionsBankResponse == null
          || StringUtils.isBlank(transactionsBankResponse.getStatus())) {
        log.warn("Response for account id [{}] null", accountId);
        throw new GetBalanceNullException("Response is null", "response_null");
      }

      if (StringUtils.equalsIgnoreCase(transactionsBankResponse.getStatus(), RESPONSE_KO)) {
        log.warn("Response for account id [{}] not valid", accountId);
        throw new GetBalanceBadRequestException("Response not valid", "response_ko");
      }

      log.info(
          "Number of transactions for the account [{}] is [{}]",
          accountId,
          transactionsBankResponse.getPayload().getList().size());

      saveHistoryTransaction(transactionsBankResponse.getPayload().getList(), accountId);

      return transactionsBankResponse.getPayload().getList();

    } catch (HttpClientErrorException e) {

      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      if (StringUtils.equalsIgnoreCase(RESPONSE_KO, badRequestResponse.getStatus())
          && badRequestResponse.getErrors() != null) {
        for (int i = 0; i < badRequestResponse.getErrors().size(); i++) {
          Error error = badRequestResponse.getErrors().get(i);

          if (StringUtils.equalsIgnoreCase(error.getCode(), "REQ004")
              && StringUtils.equalsIgnoreCase(
                  error.getDescription(), "Invalid account identifier")) {
            log.error(error.getDescription());
            throw new CustomerIdNotCorrectlyException(
                "account id is not correct", "account_id_not_valid");
          }
        }
      }

      log.warn("Cannot banking account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    } catch (HttpServerErrorException e) {
      log.warn("Generic error for account id [{}] data", accountId, e);
      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      throw new SandboxInternalErrorException(
          "sandbox internal error", badRequestResponse.getErrors().get(0).getDescription());
    } catch (Exception e) {
      log.warn("Generic error for account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    }
  }

  private void saveHistoryTransaction(
      List<PayloadTransactionsResponse> payloadTransactionsResponseList, String accountId) {

    List<Transaction> transactionList =
        transactionMapper.toTransactionList(payloadTransactionsResponseList);

    List<Transaction> newTransactionToSave = new ArrayList<>();

    for (Transaction transaction : transactionList) {
      transaction.setAccountId(accountId);
      Optional<Transaction> transactionOptional =
          transactionRepository.findByTransactionId(transaction.getTransactionId());

      if (transactionOptional.isPresent()) {
        transaction.setId(transactionOptional.get().getId());
        transactionRepository.save(transaction);
      } else {
        newTransactionToSave.add(transaction);
      }
    }

    transactionRepository.saveAll(newTransactionToSave);
  }

  public String getAccountInfo(String accountId)
      throws GetInfoAccountNullException, GetInfoAccountBadRequestException,
          CustomerIdNotCorrectlyException, SandboxInternalErrorException {

    HttpHeaders headers = createHeader();

    final String url =
        UriComponentsBuilder.fromHttpUrl(config.getBasicUrl())
            .path(URL_BANKING)
            .path(accountId)
            .encode()
            .toUriString();

    log.info("Calling banking info service [{}]", url);

    try {

      String json = getMethod(url, headers);

      AccountInfoResponse accountInfoResponse = this.gson.fromJson(json, AccountInfoResponse.class);

      if (accountInfoResponse == null || StringUtils.isBlank(accountInfoResponse.getStatus())) {
        log.warn("Response for account id [{}] null", accountId);
        throw new GetInfoAccountNullException("Response is null", "response_null");
      }

      if (StringUtils.equalsIgnoreCase(accountInfoResponse.getStatus(), RESPONSE_KO)) {
        log.warn("Response for account id [{}] not valid", accountId);
        throw new GetInfoAccountBadRequestException("Response not valid", "response_ko");
      }

      log.info(
          "Iban for account id [{}] is [{}]",
          accountId,
          accountInfoResponse.getPayload().getIban());

      return accountInfoResponse.getPayload().getIban();

    } catch (HttpClientErrorException e) {

      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      if (StringUtils.equalsIgnoreCase(RESPONSE_KO, badRequestResponse.getStatus())
          && badRequestResponse.getErrors() != null) {
        for (int i = 0; i < badRequestResponse.getErrors().size(); i++) {
          Error error = badRequestResponse.getErrors().get(i);

          if (StringUtils.equalsIgnoreCase(error.getCode(), "REQ004")
              && StringUtils.equalsIgnoreCase(
                  error.getDescription(), "Invalid account identifier")) {
            log.error(error.getDescription());
            throw new CustomerIdNotCorrectlyException(
                "account id is not valid", "account_id_not_valid");
          }
        }
      }

      log.warn("Cannot banking account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    } catch (HttpServerErrorException e) {
      log.warn("Generic error for account id [{}] data", accountId, e);
      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      throw new SandboxInternalErrorException(
          "sandbox internal error", badRequestResponse.getErrors().get(0).getDescription());
    } catch (Exception e) {
      log.warn("Generic error for account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    }
  }

  public BonificoResponse postBonifico(
      String accountId, String iban, BonificoRequestDto bonificoRequestDto)
      throws GetInfoAccountNullException, GetInfoAccountBadRequestException,
          CustomerIdNotCorrectlyException, SandboxInternalErrorException {

    BonificoResponse bonificoResponse = new BonificoResponse();

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = createHeader();

    if (bonificoRequestDto.getCreditor().getAccount() != null
        && StringUtils.isBlank(bonificoRequestDto.getCreditor().getAccount().getAccountCode())) {
      bonificoRequestDto.getCreditor().getAccount().setAccountCode(iban);
    }

    log.info("Json body call bonifico [{}]", bonificoRequestDto);

    final String url =
        UriComponentsBuilder.fromHttpUrl(config.getBasicUrl())
            .path(URL_BANKING)
            .path(accountId)
            .path(BONIFICO)
            .encode()
            .toUriString();

    log.info("Calling banking bonifico service [{}]", url);

    try {
      ResponseEntity<String> response =
          restTemplate.exchange(
              url, HttpMethod.POST, new HttpEntity<>(bonificoRequestDto, headers), String.class);

      String json = response.getBody();

      GenericResponse genericResponse = this.gson.fromJson(json, GenericResponse.class);

      if (genericResponse == null || StringUtils.isBlank(genericResponse.getStatus())) {
        log.warn("Response for account id [{}] null", accountId);
        throw new GetInfoAccountNullException("Response is null", "response_null");
      }

      if (StringUtils.equalsIgnoreCase(genericResponse.getStatus(), RESPONSE_KO)) {
        log.warn("Response for account id [{}] not valid", accountId);
        throw new GetInfoAccountBadRequestException("Response not valid", "response_ko");
      }

      bonificoResponse.setCode(genericResponse.getStatus());
      bonificoResponse.setDescription(
          "Nessuna informazioni dal cliente su come ottenere un risultato corretto");

      return bonificoResponse;

    } catch (HttpClientErrorException e) {

      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      if (StringUtils.equalsIgnoreCase(RESPONSE_KO, badRequestResponse.getStatus())
          && badRequestResponse.getErrors() != null) {
        for (int i = 0; i < badRequestResponse.getErrors().size(); i++) {
          Error error = badRequestResponse.getErrors().get(i);

          if (StringUtils.equalsIgnoreCase(error.getCode(), "REQ004")
              && StringUtils.equalsIgnoreCase(
                  error.getDescription(), "Invalid account identifier")) {
            log.error(error.getDescription());
            throw new CustomerIdNotCorrectlyException(
                "account id is not valid", "account_id_not_valid");
          }

          if (StringUtils.equalsIgnoreCase(error.getCode(), "API000")) {
            log.info("Falso errore per il test");
            bonificoResponse.setCode(error.getCode());
            bonificoResponse.setDescription(error.getDescription());
            return bonificoResponse;
          }
        }
      }

      log.warn("Cannot banking account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    } catch (HttpServerErrorException e) {
      log.warn("Generic error for account id [{}] data", accountId, e);
      BadRequestResponse badRequestResponse =
          this.gson.fromJson(e.getResponseBodyAsString(), BadRequestResponse.class);

      throw new SandboxInternalErrorException(
          "sandbox internal error", badRequestResponse.getErrors().get(0).getDescription());
    } catch (Exception e) {
      log.warn("Generic error for account id [{}] data", accountId, e);
      throw new SandboxInternalErrorException("sandbox internal error", "internal_error");
    }
  }

  public String getMethod(String url, HttpHeaders headers) {

    RestTemplate restTemplate = new RestTemplate();

    ResponseEntity<String> response =
        restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

    return response.getBody();
  }

  private HttpHeaders createHeader() {
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    headers.set(AUTH_SCHEMA, config.getAuthSchema());
    headers.set(API_KEY, config.getApiKey());
    headers.set(X_TIME_ZONE, EUROPE_ROME);

    return headers;
  }

  public void isValidDate(String dateString) throws DateNotCorrectlyException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    try {
      sdf.parse(dateString);
    } catch (ParseException e) {
      log.error("executionDate from bonifico request not valid [{}] data", dateString);
      throw new DateNotCorrectlyException("sandbox internal error", "internal_error");
    }
  }
}
