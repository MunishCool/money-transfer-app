package com.mybank.account;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mybank.account.services.AccountService;
import com.mybank.custom.exception.AccountException;
import com.mybank.model.Account;
import com.mybank.model.UserTransaction;

import io.javalin.Context;

/**
 * 
 * @author munish
 *
 */
@Singleton
class AccountController {
	private AccountService accountService;

	@Inject
	public AccountController(AccountService accountService) {
		this.accountService = accountService;
	}

	public void getAllAccounts(Context ctx) throws AccountException {
		ctx.json(accountService.getAllAccounts());
	}

	public void getAccountById(Context ctx, long accountId) throws AccountException {
		ctx.json(accountService.getAccountById(accountId));
	}

	public void getAccountBalanceById(Context ctx, long accountId) throws AccountException {
		ctx.json(accountService.getAccountBalanceById(accountId));
	}

	public void createAccount(Context ctx, Account account) throws AccountException {
		ctx.json(accountService.createAccount(account));
	}

	public void depositAmountToAccount(Context ctx, long accountId, BigDecimal amount) throws Exception {
		ctx.json(accountService.updateAccountBalance(accountId, amount));
	}

	public void withDramAmountFromAccount(Context ctx, long accountId, BigDecimal amount) throws Exception {
		ctx.json(accountService.withDramAmountFromAccount(accountId, amount));
	}

	public void deleteAccountById(Context ctx, long accountId) throws AccountException {
		ctx.json(accountService.deleteAccountById(accountId));
	}

	public void transferFunds(Context ctx, UserTransaction userTransaction) throws Exception {
		ctx.json(accountService.transferAccountBalance(userTransaction));
	}

	public void updateAccount(Context ctx, String field, String value, long accountId) throws AccountException {
		ctx.json(accountService.updateAccount(field, value, accountId));
	}

}
