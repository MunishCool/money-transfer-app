package com.mybank.db.connection;

import com.mybank.account.repositories.InMemoryAccountRepository;

/**
 * 
 * @author munish
 *
 */
public abstract class DAOFactory {

	public static final int H2 = 1;

	public abstract void bumpDummyData();

	public abstract InMemoryAccountRepository getAccountDAO();

	public static DAOFactory getDAOFactory(int factoryCode) {

		switch (factoryCode) {
		case H2:
			return new H2DAOFactory();
		default:
			// by default using H2 in memory database
			return new H2DAOFactory();
		}
	}
}
