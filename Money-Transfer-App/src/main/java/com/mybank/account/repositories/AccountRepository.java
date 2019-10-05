package com.mybank.account.repositories;

import java.math.BigDecimal;
import java.util.List;

import com.mybank.custom.exception.AccountException;
import com.mybank.model.Account;
import com.mybank.model.UserTransaction;

/**
 * 
 * @author munish
 *
 */
public interface AccountRepository {

	List<Account> getAllAccounts() throws AccountException;

	Account getAccountById(long accountId) throws AccountException;

	long createAccount(Account account) throws AccountException;

	int deleteAccountById(long accountId) throws AccountException;

	int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws AccountException;

	int transferAccountBalance(UserTransaction userTransaction) throws AccountException;

	long updateAccount(String field, String value, long accountId) throws AccountException;

	int disableAccount(Long accountId, boolean status) throws AccountException;

}
