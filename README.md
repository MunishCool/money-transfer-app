# Rest API's for Money Transfer between two accounts, implemented in Javalin.

## Technology stack
- Java 8
- [Maven](https://maven.apache.org/)
- [Google Guice for dependency injection](https://github.com/google/guice)
- [H2 in memory database](https://www.h2database.com/html/main.html)
- [Maven](https://maven.apache.org/)
- [Javalin](https://javalin.io/) (with embedded Jetty Server)
- **Hand-written in-memory data storage using concurrency utilities**
- [JUnit 4](https://junit.org/junit4/)
- [Apache HttpClient](https://hc.apache.org/index.html) (for unit testing)

### Main Class

com.mybank.server.App

### API URL Routings

All the account related end points routine is done in the class as below path.
com.mybank.account.AccountRouting

User API end points routing is done in class com.mybank.user.UserRouting

### Guice Module

com.mybank.server.AppModule which is loading the main MyBankModule

### How to run
```sh
mvn exec:java
```

Application is running in inbuilt light weight javalin server on localhost port 7000. H2 is a in memory database which has been initialized with some sample user and account data to view

- http://localhost:7000/api/user/test1
- http://localhost:7000/api/user/test2
- http://localhost:7000/api/account/1
- http://localhost:7000/api/account/2

### Available Rest API's

| HTTP METHOD | PATH | USAGE |
| -----------| ------ | ------ |
| GET | /api/user/all | get all users | 
| POST | /api/user/create | create a new user | 
| PUT | /api/user/{userId} | update user | 
| GET | /api/user/{userId} | get user details by userId | 
| DELETE | /api/user/{userId} | remove user | 
| GET | /api/account/{accountId} | get account details by accountId | 
| GET | /api/account/all | get all accounts | 
| GET | /api/account/{accountId}/balance | get account balance details by accountId | 
| POST | /api/account/create | create a new account
| DELETE | /api/account/{accountId} | remove account by accountId | 
| PUT | /api/account/{accountId}/withdraw/{amount} | withdraw money from account | 
| PUT | /api/account/{accountId}/deposit/{amount} | deposit money to account | 
| POST | /api/transaction | perform transaction between 2 user accounts | 

### Http Status
- 200 OK: Request is ok and processed successfully.
- 400 Bad Request: Unknown parameter in a request which server doesn't know or accept.
- 404 Not Found: The requested resource is not found, or its not available in db.
- 500 Internal Server Error: The server encountered an unexpected condition or exception.

### Dummy JSON for User and Account
##### User : 
```sh
{  
  "userName":"Munish Bhardwaj",
  "emailAddress":"munish@gmail.com"
} 
```
##### User Account: : 

```sh
{  
   "customerName":"test1",
   "balance":1000.0000,
   "currencyCode":"USD"
} 
```

##### Account: : 

```sh
{  
  	"accountId": 1,
        "customerName": "munish",
        "customerEmail": "munish@test.com",
        "customerAddress": "India",
        "customerMobile": "+918901901200",
        "customerIdProof": "ZVXYZA",
        "customerPassword": XVZA
        "balance": 100.0000,
        "currencyCode": "USD"
} 
```

#### User Transaction:
```sh
{  
   "currencyCode":"INR",
   "amount":800000.0000,
   "fromAccountId":1,
   "toAccountId":2
}
```
