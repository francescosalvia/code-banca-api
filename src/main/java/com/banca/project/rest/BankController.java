package com.banca.project.rest;

import com.banca.project.dto.response.PayloadTransactionsResponse;
import com.banca.project.exception.*;
import com.banca.project.service.RestService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
  public ResponseEntity getCashAccount(@RequestParam String accountId) {

    log.info("Request balance for accountId -> [{}]", accountId);

    try {

      restService.checkIdCustomer(accountId);

      double balance = restService.getBalance(accountId);

      return ResponseEntity.ok().body(balance);

    } catch (CustomerIdNotCorrectlyException e) {

      return ResponseEntity.badRequest().body("account_id_not_valid");
    } catch (GetBalanceNullException e) {
      return ResponseEntity.badRequest().body("balance_error");
    } catch (SandboxInternalErrorException e) {
      return ResponseEntity.badRequest().body("internal_error");
    } catch (GetBalanceBadRequestException e) {
      return ResponseEntity.badRequest().body("response_not_acceptable");
    }
  }

  @GetMapping(value = "transactions")
  @ApiOperation(value = "Get transactions by account id")
  public ResponseEntity getTransactionAccount(
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

      return ResponseEntity.ok().body(transactions);

    } catch (CustomerIdNotCorrectlyException e) {

      return ResponseEntity.badRequest().body("account_id_not_valid");
    } catch (EndDateNotCorrectlyException e) {

      return ResponseEntity.badRequest().body("end_date_not_correctly");
    } catch (GetBalanceNullException e) {
      return ResponseEntity.badRequest().body("balance_error");
    } catch (SandboxInternalErrorException e) {
      return ResponseEntity.badRequest().body("internal_error");
    } catch (GetBalanceBadRequestException e) {
      return ResponseEntity.badRequest().body("response_not_acceptable");
    }
  }

  @GetMapping(value = "bonifico")
  @ApiOperation(value = "post bonifico by account id")
  public ResponseEntity postBonifico(@RequestParam String accountId) {

    log.info("Request bonifico for accountId -> [{}]", accountId);

    try {

      restService.checkIdCustomer(accountId);

      String iban = restService.getAccountInfo(accountId);

      return ResponseEntity.ok().body("");
    } catch (CustomerIdNotCorrectlyException e) {

      return ResponseEntity.badRequest().body("account_id_not_valid");
    } catch (GetInfoAccountNullException e) {
      return ResponseEntity.badRequest().body("info_account_error");
    } catch (SandboxInternalErrorException e) {
      return ResponseEntity.badRequest().body("internal_error");
    } catch (GetInfoAccountBadRequestException e) {
      return ResponseEntity.badRequest().body("response_not_acceptable");
    }
  }
}
