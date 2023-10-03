-- liquibase formatted sql
-- changeset usernome.usercognome:date-01

CREATE TABLE IF NOT EXISTS `transaction` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `transaction_id` VARCHAR(255) NOT NULL,
  `operation_id` VARCHAR(255) NOT NULL,
  `accounting_date` date DEFAULT NULL,
  `value_date`  date DEFAULT NULL,
  `enumeration` VARCHAR(255) DEFAULT NULL,
  `value` VARCHAR(255) DEFAULT NULL,
  `amount` DECIMAL(20,2) NULL DEFAULT NULL,
  `currency`  VARCHAR(255) DEFAULT NULL,
  `description` VARCHAR(255) DEFAULT NULL,
  `dt_insert` datetime DEFAULT CURRENT_TIMESTAMP,
  `dt_update` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
