package com.banca.project.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity(name = Transaction.TABLE_NAME)
@NoArgsConstructor
public class Transaction {

  public static final String TABLE_NAME = "transaction";

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @CreationTimestamp
  @Column(
      name = "dt_insert",
      nullable = false,
      updatable = false,
      insertable = false,
      columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
  private LocalDateTime dtInsert;

  @UpdateTimestamp
  @Column(
      name = "dt_update",
      nullable = false,
      insertable = false,
      columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
  private LocalDateTime dtUpdate;

  @Column(name = "transaction_id")
  private String transactionId;

  @Column(name = "operation_id")
  private String operationId;

  @Column(name = "accounting_date")
  private LocalDate accountingDate;

  @Column(name = "value_date")
  private LocalDate valueDate;

  @Column(name = "amount")
  private double amount;

  @Column(name = "currency")
  private String currency;

  @Column(name = "description")
  private String description;

  @Column(name = "enumeration")
  private String enumeration;

  @Column(name = "value")
  private String value;

  @Column(name = "account_id")
  private String accountId;
}
