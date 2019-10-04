package com.mybank.server;

import org.apache.log4j.Logger;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mybank.db.connection.DAOFactory;
import com.mybank.entrypoint.EntrypointType;

public class App {
	private static Logger log = Logger.getLogger(App.class);

	public static void startServer(String[] args) {
		// Setting H2 database with some dummy data
		log.info("######### Initialize h2 in memory database #########");
		DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

		h2DaoFactory.bumpDummyData();
		log.info("######### Initialisation Complete #######");

		Injector injector = Guice.createInjector(new AppModule());
		injector.getInstance(Startup.class).boot(EntrypointType.REST, args);

	}

	public static void main(String[] args) {

		startServer(args);

	}
}
