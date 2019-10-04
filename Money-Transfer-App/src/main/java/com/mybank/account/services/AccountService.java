package com.mybank.account.services;

import java.math.BigDecimal;
import java.util.List;

import com.mybank.custom.exception.AccountException;
import com.mybank.model.Account;
import com.mybank.model.UserTransaction;
import com.mybank.response.JsonResponse;

/**
 * 
 * @author munish
 *
 */
public interface AccountService {

	public List<Account> getAllAccounts() throws AccountException;

	public JsonResponse getAccountById(long accountId) throws AccountException;

	public Account createAccount(Account account) throws AccountException;

	public JsonResponse deleteAccountById(long accountId) throws AccountException;

	public Account updateAccountBalance(long accountId, BigDecimal deltaAmount) throws AccountException, Exception;

	public JsonResponse transferAccountBalance(UserTransaction userTransaction) throws AccountException, Exception;

	Account withDramAmountFromAccount(long accountId, BigDecimal amount) throws Exception;

	JsonResponse getAccountBalanceById(long accountId) throws AccountException;

	public Account updateAccount(String field, String value, long accountId) throws AccountException;

}
