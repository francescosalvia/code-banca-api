package com.banca.project.service;

import com.banca.project.exception.CustomerIdNotCorrectlyException;
import com.banca.project.exception.EndDateNotCorrectlyException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("dev")
@Slf4j
class BankRestTest {

  @Autowired RestService restService;

  @Test
  void isNotValidAccountId() {

    assertThrows(
        CustomerIdNotCorrectlyException.class,
        () -> {
          restService.checkIdCustomer("");
        });

    assertThrows(
        CustomerIdNotCorrectlyException.class,
        () -> {
          restService.checkIdCustomer(null);
        });
  }

  @Test
  void isNotValidDate() {

    LocalDate startDate = LocalDate.parse("2023-06-01");
    LocalDate endDate = LocalDate.parse("2023-01-01");

    assertThrows(
        EndDateNotCorrectlyException.class,
        () -> {
          restService.verifyDate(startDate, endDate);
        });
  }

  @Test
  void accountIdNotPresent() {

    String code = "account_id_not_valid";

    CustomerIdNotCorrectlyException customerIdNotCorrectlyException =
        assertThrows(
            CustomerIdNotCorrectlyException.class,
            () -> {
              restService.getBalance("id_fake");
            });

    assertEquals(code, customerIdNotCorrectlyException.getCode());
  }
}
