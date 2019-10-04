package com.mybank.guice.module;

import com.google.inject.AbstractModule;
import com.mybank.account.AccountModule;
import com.mybank.user.UserModule;

/**
 * 
 * @author munish
 *
 */
public class MyBankModule extends AbstractModule {
	@Override
	protected void configure() {
		install(new UserModule());
		install(new AccountModule());
		// install(new DataSourceModule());
		install(WebModule.create());
	}
}
