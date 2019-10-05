package com.mybank.dao;

import static junit.framework.TestCase.assertTrue;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mybank.account.repositories.InMemoryAccountRepository;
import com.mybank.custom.exception.AccountException;
import com.mybank.db.connection.DAOFactory;
import com.mybank.db.connection.H2DAOFactory;
import com.mybank.enums.AccountType;
import com.mybank.model.Account;
import com.mybank.model.UserTransaction;

public class TestAccountBalance {

	private static Logger log = Logger.getLogger(TestAccountDAO.class);
	private static final DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);
	private static final int THREADS_COUNT = 100;

	@BeforeClass
	public static void setup() {
		// load dummy data
		h2DaoFactory.bumpDummyData();
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testAccountSingleThreadSameCcyTransfer() throws AccountException {

		final InMemoryAccountRepository accountDAO = h2DaoFactory.getAccountDAO();

		BigDecimal transferAmount = new BigDecimal(50.01234).setScale(4, RoundingMode.HALF_EVEN);

		UserTransaction transaction = new UserTransaction("EUR", transferAmount, 3L, 4L);

		long startTime = System.currentTimeMillis();

		accountDAO.transferAccountBalance(transaction);
		long endTime = System.currentTimeMillis();

		log.info("TransferAccountBalance finished, time taken: " + (endTime - startTime) + "ms");

		Account accountFrom = accountDAO.getAccountById(3);

		Account accountTo = accountDAO.getAccountById(4);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(
				accountFrom.getBalance().compareTo(new BigDecimal(449.9877).setScale(4, RoundingMode.HALF_EVEN)) == 0);
		assertTrue(accountTo.getBalance().equals(new BigDecimal(550.0123).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testAccountMultiThreadedTransfer() throws InterruptedException, AccountException {
		final InMemoryAccountRepository accountDAO = h2DaoFactory.getAccountDAO();
		// transfer a total of 200USD from 100USD balance in multi-threaded
		// mode, expect half of the transaction fail
		final CountDownLatch latch = new CountDownLatch(THREADS_COUNT);
		for (int i = 0; i < THREADS_COUNT; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						UserTransaction transaction = new UserTransaction("USD",
								new BigDecimal(2).setScale(4, RoundingMode.HALF_EVEN), 1L, 2L);
						accountDAO.transferAccountBalance(transaction);
					} catch (Exception e) {
						log.error("Error occurred during transfer ", e);
					} finally {
						latch.countDown();
					}
				}
			}).start();
		}

		latch.await();

		Account accountFrom = accountDAO.getAccountById(1);

		Account accountTo = accountDAO.getAccountById(2);

		log.debug("Account From: " + accountFrom);

		log.debug("Account From: " + accountTo);

		assertTrue(accountFrom.getBalance().equals(new BigDecimal(0).setScale(4, RoundingMode.HALF_EVEN)));
		assertTrue(accountTo.getBalance().equals(new BigDecimal(300).setScale(4, RoundingMode.HALF_EVEN)));

	}

	@Test
	public void testTransferFailOnDBLock() throws AccountException, SQLException {
		final String SQL_LOCK_ACC = "SELECT * FROM Account WHERE accountId = 5 FOR UPDATE";
		Connection conn = null;
		PreparedStatement lockStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;

		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				AccountType accountType = AccountType.valueOf(rs.getString("accountType"));
				fromAccount = new Account(rs.getLong("accountId"), rs.getString("customerName"),
						rs.getString("customerEmail"), rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"), accountType, rs.getBoolean("accountStatus"));
				if (log.isDebugEnabled())
					log.debug("Locked Account: " + fromAccount);
			}

			if (fromAccount == null) {
				throw new AccountException("Locking error during test, SQL = " + SQL_LOCK_ACC);
			}
			// after lock account 5, try to transfer from account 6 to 5
			// default h2 timeout for acquire lock is 1sec
			BigDecimal transferAmount = new BigDecimal(50).setScale(4, RoundingMode.HALF_EVEN);

			UserTransaction transaction = new UserTransaction("GBP", transferAmount, 6L, 5L);
			h2DaoFactory.getAccountDAO().transferAccountBalance(transaction);
			conn.commit();
		} catch (Exception e) {
			log.error("Exception occurred, initiate a rollback");
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				log.error("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
		}

		// now inspect account 3 and 4 to verify no transaction occurred
		BigDecimal originalBalance = new BigDecimal(500).setScale(4, RoundingMode.HALF_EVEN);
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(6).getBalance().equals(originalBalance));
		assertTrue(h2DaoFactory.getAccountDAO().getAccountById(5).getBalance().equals(originalBalance));
	}

}
