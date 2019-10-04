# Money Transfer App rest api implemented in Javalin to showcase transfer of amount between two acounts

### Technologies
- Javalin API
- H2 in memory database
- Log4j
- Apache HTTP Client
- Google Guice for dependency injection

## Technology stack
- Java 8
- [Maven](https://maven.apache.org/)
- [Google Guice](https://github.com/google/guice)
- [Maven](https://maven.apache.org/)
- [Javalin](https://javalin.io/) (with embedded Jetty Server)
- **Hand-written in-memory data storage using concurrency utilities**
- [JUnit 4](https://junit.org/junit4/)
- [Apache HttpClient](https://hc.apache.org/index.html) (for unit testing)


### How to run
```sh
mvn exec:java
```

Application is running in inbuilt light weight javalin server on localhost port 7000. H2 is a in memory database which has been initialized with some sample user and account data to view

- http://localhost:7000/user/test1
- http://localhost:7000/user/test2
- http://localhost:7000/account/1
- http://localhost:7000/account/2

### Available rest api's

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
- 200 OK: The request has succeeded
- 400 Bad Request: The request could not be understood by the server 
- 404 Not Found: The requested resource cannot be found
- 500 Internal Server Error: The server encountered an unexpected condition 

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
   "balance":10.0000,
   "currencyCode":"GBP"
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
   "currencyCode":"EUR",
   "amount":100000.0000,
   "fromAccountId":1,
   "toAccountId":2
}
```
