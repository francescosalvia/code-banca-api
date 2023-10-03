package com.banca.project.rest;

import com.banca.project.dto.request.BonificoRequestDto;
import com.banca.project.dto.response.BonificoResponse;
import com.banca.project.dto.response.GenericRestEntityResponse;
import com.banca.project.dto.response.PayloadTransactionsResponse;
import com.banca.project.exception.*;
import com.banca.project.service.RestService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/v1/")
@CrossOrigin(origins = "${app.cross.origin}")
@Slf4j
public class BankController {

  private final RestService restService;

  @Autowired
  public BankController(RestService restService) {
    this.restService = restService;
  }

  @GetMapping(value = "balance")
  @ApiOperation(value = "get balance by account id")
  public ResponseEntity<GenericRestEntityResponse> getCashAccount(@RequestParam String accountId) {

    log.info("Request balance for accountId -> [{}]", accountId);

    try {

      restService.checkIdCustomer(accountId);

      double balance = restService.getBalance(accountId);

      return ResponseEntity.ok(GenericRestEntityResponse.ok(balance));

    } catch (CustomerIdNotCorrectlyException e) {

      return ResponseEntity.badRequest()
          .body(GenericRestEntityResponse.badRequest("account_id_not_valid"));
    } catch (GetBalanceNullException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("balance_error")));
    } catch (SandboxInternalErrorException e) {
      return ResponseEntity.internalServerError()
          .body((GenericRestEntityResponse.internalServerError("internal_error")));
    } catch (GetBalanceBadRequestException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("response_not_acceptable")));
    }
  }

  @GetMapping(value = "transactions")
  @ApiOperation(value = "Get transactions by account id")
  public ResponseEntity<GenericRestEntityResponse> getTransactionAccount(
      @RequestParam String accountId,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fromDate,
      @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate toDate) {

    log.info(
        "Request list transactions for accountId -> [{}], from date [{}] to date [{}]",
        accountId,
        fromDate,
        toDate);

    try {

      restService.checkIdCustomer(accountId);

      restService.verifyDate(fromDate, toDate);

      List<PayloadTransactionsResponse> transactions =
          restService.getTransactions(accountId, fromDate, toDate);

      return ResponseEntity.ok(GenericRestEntityResponse.ok(transactions));

    } catch (CustomerIdNotCorrectlyException e) {

      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("account_id_not_valid")));
    } catch (EndDateNotCorrectlyException e) {

      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("end_date_not_correctly")));
    } catch (GetBalanceNullException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("balance_error")));
    } catch (SandboxInternalErrorException e) {
      return ResponseEntity.internalServerError()
          .body((GenericRestEntityResponse.internalServerError("internal_error")));
    } catch (GetBalanceBadRequestException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("response_not_acceptable")));
    }
  }

  @PostMapping(value = "bonifico")
  @ApiOperation(value = "post bonifico by account id")
  public ResponseEntity<GenericRestEntityResponse> postBonifico(
      @RequestParam String accountId, @Valid @RequestBody BonificoRequestDto bonificoRequestDto) {

    log.info(
        "Request bonifico for accountId -> [{}] and bonificoRequest -> [{}]",
        accountId,
        bonificoRequestDto);

    try {

      restService.checkIdCustomer(accountId);

      restService.isValidDate(bonificoRequestDto.getExecutionDate());

      String iban = restService.getAccountInfo(accountId);

      BonificoResponse returnFromBonifico =
          restService.postBonifico(accountId, iban, bonificoRequestDto);

      return ResponseEntity.ok(GenericRestEntityResponse.ok(returnFromBonifico));
    } catch (CustomerIdNotCorrectlyException e) {

      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("account_id_not_valid")));
    } catch (GetInfoAccountNullException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("info_account_error")));
    } catch (SandboxInternalErrorException e) {
      return ResponseEntity.internalServerError()
          .body((GenericRestEntityResponse.internalServerError("internal_error")));
    } catch (GetInfoAccountBadRequestException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("response_not_acceptable")));
    } catch (DateNotCorrectlyException e) {
      return ResponseEntity.badRequest()
          .body((GenericRestEntityResponse.badRequest("execution_date_not_valid")));
    }
  }
}
