package com.mybank.guice.module;

import javax.inject.Named;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Scopes;
import com.mybank.account.repositories.InMemoryAccountRepository;
import com.mybank.util.Utils;

public class DataSourceModule extends AbstractModule {

	@SuppressWarnings("unused")
	private static final String h2_driver = Utils.getStringProperty("h2_driver");
	private static final String h2_connection_url = Utils.getStringProperty("h2_connection_url");
	private static final String h2_user = Utils.getStringProperty("h2_user");
	private static final String h2_password = Utils.getStringProperty("h2_password");

	@Override
	protected void configure() {

		bind(DataSource.class).toProvider(H2DataSourceProvider.class).in(Scopes.SINGLETON);
		bind(InMemoryAccountRepository.class);
	}

	static class H2DataSourceProvider implements Provider<DataSource> {

		private final String url;
		private final String username;
		private final String password;
		
		public H2DataSourceProvider(@Named("url") final String url, @Named("username") final String username,
				@Named("password") final String password) {
			this.url = h2_connection_url;
			this.username = h2_user;
			this.password = h2_password;
		}

		@Override
		public DataSource get() {
			final JdbcDataSource dataSource = new JdbcDataSource();
			dataSource.setURL(url);
			dataSource.setUser(username);
			dataSource.setPassword(password);
			return dataSource;
		}
	}

}
