package com.mybank.account.repositories;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.inject.Singleton;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;

import com.mybank.custom.exception.AccountException;
import com.mybank.db.connection.H2DAOFactory;
import com.mybank.model.Account;
import com.mybank.model.MoneyUtil;
import com.mybank.model.UserTransaction;

/**
 * 
 * @author munish
 *
 */
@Singleton
public class InMemoryAccountRepository implements AccountRepository {

	private final static String SQL_GET_ACC_BY_ID = "SELECT * FROM Account WHERE accountId = ? ";
	private final static String SQL_LOCK_ACC_BY_ID = "SELECT * FROM Account WHERE accountId = ? FOR UPDATE";
	private final static String SQL_CREATE_ACC = "INSERT INTO Account (customerName,customerEmail,customerAddress,customerMobile,customerIdProof,customerPassword, balance, currencyCode) VALUES (?, ?, ?,?,?,?,?,?)";
	private final static String SQL_UPDATE_ACC_BALANCE = "UPDATE Account SET balance = ? WHERE AccountId = ? ";
	private final static String SQL_GET_ALL_ACC = "SELECT * FROM Account";
	private final static String SQL_DELETE_ACC_BY_ID = "DELETE FROM Account WHERE accountId = ?";

	private static Logger log = Logger.getLogger(InMemoryAccountRepository.class);

	/**
	 * Get all accounts.
	 */
	public List<Account> getAllAccounts() throws AccountException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		List<Account> allAccounts = new ArrayList<Account>();
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ALL_ACC);
			rs = stmt.executeQuery();
			while (rs.next()) {
				Account acc = new Account(rs.getLong("accountId"), rs.getString("customerName"),
						rs.getString("customerEmail"), rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"));
				if (log.isDebugEnabled())
					log.debug("getAllAccounts(): Get  Account " + acc);
				allAccounts.add(acc);
			}
			return allAccounts;
		} catch (SQLException e) {
			throw new AccountException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Get account by id
	 */
	@Override
	public Account getAccountById(long accountId) throws AccountException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Account acc = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_GET_ACC_BY_ID);
			stmt.setLong(1, accountId);
			rs = stmt.executeQuery();
			if (rs.next()) {
				acc = new Account(rs.getLong("accountId"), rs.getString("customerName"), rs.getString("customerEmail"),
						rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"));
				if (log.isDebugEnabled())
					log.debug("Retrieve Account By Id: " + acc);
			}
			return acc;
		} catch (SQLException e) {
			throw new AccountException("getAccountById(): Error reading account data", e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, rs);
		}
	}

	/**
	 * Create account
	 */
	@Override
	public long createAccount(Account account) throws AccountException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet generatedKeys = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_CREATE_ACC);
			stmt.setString(1, account.getcustomerName());
			stmt.setString(2, account.getcustomerEmail());
			stmt.setString(3, account.getcustomerAddress());
			stmt.setString(4, account.getCustomerMobile());
			stmt.setString(5, account.getCustomerIdProof());
			stmt.setString(6, Base64.getEncoder().encodeToString(account.getCustomerPassword().getBytes()));
			stmt.setBigDecimal(7, account.getBalance());
			stmt.setString(8, account.getCurrencyCode());
			int affectedRows = stmt.executeUpdate();
			if (affectedRows == 0) {
				log.error("createAccount(): Creating account failed, no rows affected.");
				throw new AccountException("Account Cannot be created");
			}
			generatedKeys = stmt.getGeneratedKeys();
			if (generatedKeys.next()) {
				return generatedKeys.getLong(1);
			} else {
				log.error("Creating account failed, no ID obtained.");
				throw new AccountException("Account Cannot be created");
			}
		} catch (SQLException e) {
			log.error("Error Inserting Account  " + account);
			throw new AccountException("createAccount(): Error creating user account " + account, e);
		} finally {
			DbUtils.closeQuietly(conn, stmt, generatedKeys);
		}
	}

	/**
	 * Delete account by id
	 */
	@Override
	public int deleteAccountById(long accountId) throws AccountException {
		Connection conn = null;
		PreparedStatement stmt = null;
		try {
			conn = H2DAOFactory.getConnection();
			stmt = conn.prepareStatement(SQL_DELETE_ACC_BY_ID);
			stmt.setLong(1, accountId);
			return stmt.executeUpdate();
		} catch (SQLException e) {
			throw new AccountException("deleteAccountById(): Error deleting user account Id " + accountId, e);
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(stmt);
		}
	}

	/**
	 * Update account balance *
	 */
	@Override
	public int updateAccountBalance(long accountId, BigDecimal deltaAmount) throws AccountException {
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account targetAccount = null;
		int updateCount = -1;
		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, accountId);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				targetAccount = new Account(rs.getLong("accountId"), rs.getString("customerName"),
						rs.getString("customerEmail"), rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"));
				if (log.isDebugEnabled())
					log.debug("updateAccountBalance from Account: " + targetAccount);
			}

			if (targetAccount == null) {
				throw new AccountException("updateAccountBalance(): fail to lock account : " + accountId);
			}
			// update account upon success locking
			BigDecimal balance = targetAccount.getBalance().add(deltaAmount);
			if (balance.compareTo(MoneyUtil.zeroAmount) < 0) {
				throw new AccountException("Not sufficient Fund for account: " + accountId);
			}

			updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, balance);
			updateStmt.setLong(2, accountId);
			updateCount = updateStmt.executeUpdate();
			conn.commit();
			if (log.isDebugEnabled())
				log.debug("New Balance after Update: " + targetAccount);
			return updateCount;
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("updateAccountBalance(): User Transaction Failed, rollback initiated for: " + accountId, se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new AccountException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return updateCount;
	}

	/**
	 * Transfer fund between two accounts.
	 * 
	 * @param transaction
	 * @return
	 * @throws AccountException
	 */
	@SuppressWarnings("resource")
	@Override
	public int transferAccountBalance(UserTransaction userTransaction) throws AccountException {
		int result = -1;
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account fromAccount = null;
		Account toAccount = null;

		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock the credit and debit account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, userTransaction.getFromAccountId());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				fromAccount = new Account(rs.getLong("accountId"), rs.getString("customerName"),
						rs.getString("customerEmail"), rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance from Account: " + fromAccount);
			}
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, userTransaction.getToAccountId());
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				toAccount = new Account(rs.getLong("accountId"), rs.getString("customerName"),
						rs.getString("customerEmail"), rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"));
				if (log.isDebugEnabled())
					log.debug("transferAccountBalance to Account: " + toAccount);
			}

			// check locking status
			if (fromAccount == null || toAccount == null) {
				throw new AccountException("Fail to lock both accounts for write");
			}

			// check transaction currency
			if (!fromAccount.getCurrencyCode().equals(userTransaction.getCurrencyCode())) {
				throw new AccountException(
						"Fail to transfer Fund, transaction ccy are different from source/destination");
			}

			// check ccy is the same for both accounts
			if (!fromAccount.getCurrencyCode().equals(toAccount.getCurrencyCode())) {
				throw new AccountException(
						"Fail to transfer Fund, the source and destination account are in different currency");
			}

			// check enough fund in source account
			BigDecimal fromAccountLeftOver = fromAccount.getBalance().subtract(userTransaction.getAmount());
			if (fromAccountLeftOver.compareTo(MoneyUtil.zeroAmount) < 0) {
				throw new AccountException("Not enough Fund from source Account ");
			}
			// proceed with update
			updateStmt = conn.prepareStatement(SQL_UPDATE_ACC_BALANCE);
			updateStmt.setBigDecimal(1, fromAccountLeftOver);
			updateStmt.setLong(2, userTransaction.getFromAccountId());
			updateStmt.addBatch();
			updateStmt.setBigDecimal(1, toAccount.getBalance().add(userTransaction.getAmount()));
			updateStmt.setLong(2, userTransaction.getToAccountId());
			updateStmt.addBatch();
			int[] rowsUpdated = updateStmt.executeBatch();
			result = rowsUpdated[0] + rowsUpdated[1];
			if (log.isDebugEnabled()) {
				log.debug("Number of rows updated for the transfer : " + result);
			}
			// If there is no error, commit the transaction
			conn.commit();
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("transferAccountBalance(): User Transaction Failed, rollback initiated for: " + userTransaction,
					se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new AccountException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return result;
	}

	@Override
	public long updateAccount(String field, String value, long accountId) throws AccountException {
		Connection conn = null;
		PreparedStatement lockStmt = null;
		PreparedStatement updateStmt = null;
		ResultSet rs = null;
		Account targetAccount = null;
		int updateCount = -1;
		try {
			conn = H2DAOFactory.getConnection();
			conn.setAutoCommit(false);
			// lock account for writing:
			lockStmt = conn.prepareStatement(SQL_LOCK_ACC_BY_ID);
			lockStmt.setLong(1, accountId);
			rs = lockStmt.executeQuery();
			if (rs.next()) {
				targetAccount = new Account(rs.getLong("accountId"), rs.getString("customerName"),
						rs.getString("customerEmail"), rs.getString("customerAddress"), rs.getString("customerMobile"),
						rs.getString("customerIdProof"), rs.getString("customerPassword"), rs.getBigDecimal("balance"),
						rs.getString("currencyCode"));
				if (log.isDebugEnabled())
					log.debug("updateAccount for Account: " + targetAccount);
			}

			if (targetAccount == null) {
				throw new AccountException("updateAccountBalance(): fail to lock account : " + accountId);
			}

			if (field.equalsIgnoreCase("customerAddress")) {
				updateStmt = conn.prepareStatement("UPDATE Account SET customerAddress = ? WHERE AccountId = ?");
			} else if (field.equalsIgnoreCase("customerMobile")) {
				updateStmt = conn.prepareStatement("UPDATE Account SET customerMobile = ? WHERE AccountId = ?");
			} else if (field.equalsIgnoreCase("customerIdProof")) {
				updateStmt = conn.prepareStatement("UPDATE Account SET customerIdProof = ? WHERE AccountId = ?");
			} else if (field.equalsIgnoreCase("customerPassword")) {
				updateStmt = conn.prepareStatement("UPDATE Account SET customerPassword = ? WHERE AccountId = ?");
				String pwd = Base64.getEncoder().encodeToString(value.getBytes());
				value = pwd;
			} else if (field.equalsIgnoreCase("currencyCode")) {
				updateStmt = conn.prepareStatement("UPDATE Account SET currencyCode = ? WHERE AccountId = ?");
			}
			updateStmt.setString(1, value);
			updateStmt.setLong(2, accountId);
			updateCount = updateStmt.executeUpdate();
			conn.commit();
			if (log.isDebugEnabled())
				log.debug("Account after Update: " + targetAccount);
			return updateCount;
		} catch (SQLException se) {
			// rollback transaction if exception occurs
			log.error("updateAccount(): User Transaction Failed, rollback initiated for: " + accountId, se);
			try {
				if (conn != null)
					conn.rollback();
			} catch (SQLException re) {
				throw new AccountException("Fail to rollback transaction", re);
			}
		} finally {
			DbUtils.closeQuietly(conn);
			DbUtils.closeQuietly(rs);
			DbUtils.closeQuietly(lockStmt);
			DbUtils.closeQuietly(updateStmt);
		}
		return updateCount;
	}
}
