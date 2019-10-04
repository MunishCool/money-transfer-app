package com.mybank.account;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.mybank.account.repositories.AccountRepositoryModule;
import com.mybank.account.services.AccountServiceModule;
import com.mybank.entrypoint.Routing;

/**
 * 
 * @author munish
 *
 */
public class AccountModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(AccountController.class);
		install(new AccountServiceModule());
		install(new AccountRepositoryModule());
		Multibinder.newSetBinder(binder(), Routing.class).addBinding().to(AccountRouting.class);
	}
}
