--sample data

DROP TABLE IF EXISTS User;

CREATE TABLE User (UserId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
 UserName VARCHAR(30) NOT NULL,
 EmailAddress VARCHAR(30) NOT NULL);

CREATE UNIQUE INDEX idx_ue on User(UserName,EmailAddress);

INSERT INTO User (UserName, EmailAddress) VALUES ('munish','munish@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('testuser1','testuser1@gmail.com');
INSERT INTO User (UserName, EmailAddress) VALUES ('testuser2','testuser2@gmail.com');

DROP TABLE IF EXISTS Account;

						
CREATE TABLE Account (AccountId LONG PRIMARY KEY AUTO_INCREMENT NOT NULL,
customerName VARCHAR(30),
customerEmail VARCHAR(30),
customerAddress VARCHAR(30),
customerMobile VARCHAR(30),
customerIdProof VARCHAR(30),
customerPassword VARCHAR(30),
balance DECIMAL(19,4),
currencyCode VARCHAR(30)
);

CREATE UNIQUE INDEX idx_acc on Account(customerName,currencyCode);

INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,balance,currencyCode) VALUES ('munish','munish@test.com','India','+918901901200','ZVXYZA',100.0000,'USD');
INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,balance,currencyCode) VALUES ('test1','test1@test.com','America','+401901901200','XVOYPA',200.0000,'USD');
INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,balance,currencyCode) VALUES ('test2','test2@test.com','Europe','+628601901200','CVXYZA',500.0000,'EUR');
INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,balance,currencyCode) VALUES ('test3','test3@test.com','Europe','+128901911200','DVXEZN',500.0000,'EUR');
INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,balance,currencyCode) VALUES ('test4','test4@test.com','Germany','+448901901200','EVCYZM',500.0000,'GBP');
INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,balance,currencyCode) VALUES ('test5','test5@test.com','Germany','+448901901200','FVDYZX',500.0000,'GBP');

