package com.mybank.account.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jetty.http.HttpStatus;

import com.mybank.account.repositories.AccountRepository;
import com.mybank.custom.exception.AccountException;
import com.mybank.model.Account;
import com.mybank.model.MoneyUtil;
import com.mybank.model.UserTransaction;
import com.mybank.response.JsonResponse;

/**
 * 
 * @author munish
 *
 */
@Singleton
class DefaultAccountService implements AccountService {
	private AccountRepository accountRepository;

	@Inject
	public DefaultAccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	/**
	 * Find all accounts
	 * 
	 * @return
	 * @throws AccountException
	 */
	@Override
	public List<Account> getAllAccounts() throws AccountException {
		List<Account> accounts = accountRepository.getAllAccounts();
		return accounts;
	}

	/**
	 * Find by account id
	 * 
	 * @param accountId
	 * @return
	 * @throws AccountException
	 */
	@Override
	public JsonResponse getAccountById(long accountId) throws AccountException {
		JsonResponse response = new JsonResponse();
		Account account = accountRepository.getAccountById(accountId);
		if (null != account) {
			response.setData(account);
			response.setMessage("Successfully fetched the account details");
		} else {
			response.setData(null);
			response.setStatus("NOT_FOUND_404");
		}

		return response;
	}

	/**
	 * Create Account
	 * 
	 * @param account object
	 * @return
	 * @throws AccountException
	 */
	@Override
	public Account createAccount(Account account) throws AccountException {
		final long accountId = accountRepository.createAccount(account);
		return accountRepository.getAccountById(accountId);
	}

	/**
	 * Deposit account by account Id
	 * 
	 * @param accountId
	 * @return
	 * @throws AccountException
	 */
	public JsonResponse deleteAccountById(long accountId) throws AccountException {
		JsonResponse response = new JsonResponse();
		int deleteCount = accountRepository.deleteAccountById(accountId);
		if (deleteCount == 1) {
			response.setMessage("Successfully deleted the account");
			response.setData(deleteCount);
			response.setResponseCode(HttpStatus.OK_200);
			response.setStatus(JsonResponse.getSuccessStatus());
			return response;
		}
		if (deleteCount == 0) {
			response.setMessage("No record found with accountId {} " + accountId);
			response.setData(null);
			response.setResponseCode(HttpStatus.NOT_FOUND_404);
			response.setStatus(JsonResponse.getSuccessStatus());
			return response;
		} else {
			response.setMessage("Something went wrong");
			response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setData(null);
			response.setStatus(JsonResponse.getStatusFailure());
			return response;
		}
	}

	@Override
	public Account updateAccountBalance(long accountId, BigDecimal amount) throws Exception {
		if (amount.compareTo(MoneyUtil.zeroAmount) <= 0) {
			throw new Exception("Invalid Deposit amount BAD_REQUEST");
		}

		accountRepository.updateAccountBalance(accountId, amount.setScale(4, RoundingMode.HALF_EVEN));
		return accountRepository.getAccountById(accountId);
	}

	/**
	 * transfer amount from one account Id to another
	 * 
	 * @param UserTransaction object
	 * @return
	 * @throws AccountException
	 */
	@Override
	public JsonResponse transferAccountBalance(UserTransaction transaction) throws Exception {
		JsonResponse response = new JsonResponse();

		try {
			// handling multiple request one by one
			synchronized (this) {
				String currency = transaction.getCurrencyCode();
				if (MoneyUtil.INSTANCE.validateCcyCode(currency)) {
					int updateCount = accountRepository.transferAccountBalance(transaction);

					if (updateCount == 2) {
						response.setMessage("Successfully transferred the amount from account {} "
								+ transaction.getFromAccountId() + " " + "to" + " " + transaction.getToAccountId());
						response.setData(transaction);
						response.setResponseCode(HttpStatus.OK_200);
						response.setStatus(JsonResponse.getSuccessStatus());
					} else {
						// transaction failed
						response.setData(transaction);
						response.setResponseCode(HttpStatus.BAD_REQUEST_400);
						response.setStatus(JsonResponse.getStatusFailure());
						return response;
					}

				} else {
					response.setData(null);
					response.setResponseCode(HttpStatus.BAD_GATEWAY_502);
					response.setStatus(JsonResponse.getStatusFailure());
					return response;
				}
			}
		} catch (Exception ex) {
			throw new Exception("Internal server error");
		}
		return response;

	}

	/**
	 * Withdraw amount by account Id
	 * 
	 * @param accountId
	 * @param amount
	 * @return
	 * @throws AccountException
	 */
	@Override
	public Account withDramAmountFromAccount(long accountId, BigDecimal amount) throws Exception {
		if (amount.compareTo(MoneyUtil.zeroAmount) <= 0) {
			throw new Exception("Invalid Deposit amount BAD_REQUEST");
		}
		BigDecimal delta = amount.negate();
		accountRepository.updateAccountBalance(accountId, delta.setScale(4, RoundingMode.HALF_EVEN));
		return accountRepository.getAccountById(accountId);
	}

	@Override
	public JsonResponse getAccountBalanceById(long accountId) throws AccountException {
		JsonResponse response = new JsonResponse();
		Account account = accountRepository.getAccountById(accountId);

		if (null != account) {
			response.setData(account.getBalance());
			response.setMessage("Successfully fetched the account details");
			response.setResponseCode(HttpStatus.OK_200);
			response.setStatus(JsonResponse.getSuccessStatus());
		} else {
			response.setMessage("No record found with accountId {} " + accountId);
			response.setData(null);
			response.setResponseCode(HttpStatus.NOT_FOUND_404);
			response.setStatus(JsonResponse.getSuccessStatus());
		}

		return response;
	}

	@Override
	public Account updateAccount(String field, String value, long accountId) throws AccountException {
		accountRepository.updateAccount(field, value, accountId);
		return accountRepository.getAccountById(accountId);
	}

	@Override
	public Account disableAccount(Long accountId, boolean status) throws AccountException {
		accountRepository.disableAccount(accountId, status);
		return accountRepository.getAccountById(accountId);
	}
}
