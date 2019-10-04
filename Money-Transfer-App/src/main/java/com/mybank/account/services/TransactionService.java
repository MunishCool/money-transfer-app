package com.mybank.account.services;

import com.mybank.model.UserTransaction;

/**
 * 
 * @author munish
 *
 */
public interface TransactionService {

	int transferFund(UserTransaction transaction);

}
