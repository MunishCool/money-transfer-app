package com.mybank.db.connection;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.RunScript;

import com.mybank.account.repositories.InMemoryAccountRepository;
import com.mybank.util.Utils;

/**
 * H2 DAO
 */
/**
 * 
 * @author munish
 *
 */
public class H2DAOFactory extends DAOFactory {
	private static final String h2_driver = Utils.getStringProperty("h2_driver");
	private static final String h2_connection_url = Utils.getStringProperty("h2_connection_url");
	private static final String h2_user = Utils.getStringProperty("h2_user");
	private static final String h2_password = Utils.getStringProperty("h2_password");
	private static Logger log = Logger.getLogger(H2DAOFactory.class);

	private final InMemoryAccountRepository accountDAO = new InMemoryAccountRepository();

	H2DAOFactory() {
		// init: load driver
		DbUtils.loadDriver(h2_driver);
	}

	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(h2_connection_url, h2_user, h2_password);

	}

	public InMemoryAccountRepository getAccountDAO() {
		return accountDAO;
	}

	@Override
	public void bumpDummyData() {
		log.info("######## listening account table data ##########");
		Connection conn = null;
		try {
			conn = H2DAOFactory.getConnection();
			RunScript.execute(conn, new FileReader("db/dummydata.sql"));
		} catch (SQLException e) {
			log.error("bumpDummyData(): Error populating account data: ", e);
			throw new RuntimeException(e);
		} catch (FileNotFoundException e) {
			log.error("bumpDummyData(): Error finding dummy db file ", e);
			throw new RuntimeException(e);
		} finally {
			DbUtils.closeQuietly(conn);
		}
	}

}
