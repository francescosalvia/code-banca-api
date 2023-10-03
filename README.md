Project name: code-bank-api

Progetto per test tecnico.

Per eseguire le chiamate è possibile usare lo swagger -> http://localhost:8080/swagger-ui/index.html

Oppure Postman

Chiamata getSaldo:

curl --location 'http://127.0.0.1:8080/v1/balance?accountId=14537780'

Chiamata getTransazioni:

curl --location 'http://localhost:8080/v1/transactions?accountId=14537780&fromDate=2019-01-01&toDate=2019-12-01'

Chiamata getBonifico:

curl --location 'http://localhost:8080/v1/bonifico?accountId=14537780' \
--header 'Content-Type: application/json' \
--data '{
"creditor": {
"name": "John Doe",
"account": {
}
},
"executionDate": "2019-04-01",
"description": "Payment invoice 75/2017",
"amount": 800,
"currency": "EUR"
}'

per quest'ultima, se l'iban non viene specificato dentro l'oggetto account

public class Account {
String accountCode;
}

viene settato l'iban dell'accountId inserito in fase di chiamata.

Inoltre per la corretta esecuzione della chiamata ci aspettiamo code "API000"