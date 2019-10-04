package com.mybank.account.repositories;

import com.google.inject.AbstractModule;

/**
 * 
 * @author munish
 *
 */
public class AccountRepositoryModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(AccountRepository.class).to(InMemoryAccountRepository.class);
	}
}
