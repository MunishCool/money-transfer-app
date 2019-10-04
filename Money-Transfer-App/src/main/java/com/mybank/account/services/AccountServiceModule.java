package com.mybank.account.services;

import com.google.inject.AbstractModule;

/**
 * 
 * @author munish
 *
 */
public class AccountServiceModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AccountService.class).to(DefaultAccountService.class);
	}
}
