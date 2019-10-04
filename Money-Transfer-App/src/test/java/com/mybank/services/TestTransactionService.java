package com.mybank.services;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybank.db.connection.DAOFactory;
import com.mybank.model.Account;
import com.mybank.model.UserTransaction;
import com.mybank.server.App;

/**
 * 
 * @author munish
 *
 */
public class TestTransactionService {

	protected ObjectMapper mapper = new ObjectMapper();
	protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:7000");
	protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

	protected static HttpClient client;
	protected static DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

	@BeforeClass
	public static void setup() throws Exception {

		String arr[] = null;
		App.startServer(arr);
		h2DaoFactory.bumpDummyData();
		connManager.setDefaultMaxPerRoute(100);
		connManager.setMaxTotal(200);
		client = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();

	}

	@AfterClass
	public static void closeClient() throws Exception {
		HttpClientUtils.closeQuietly(client);
	}

	/*
	 * Test case for success deposit in an account
	 */
	@Test
	public void testDeposit() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/1/deposit/100").build();
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		String jsonString = EntityUtils.toString(response.getEntity());
		Account afterDeposit = mapper.readValue(jsonString, Account.class);
		// check balance is increased from 100 to 200
		assertTrue(afterDeposit.getBalance().equals(new BigDecimal(200).setScale(4, RoundingMode.HALF_EVEN)));

	}

	/*
	 * Test case for success withdraw amount from an account
	 */
	@Test
	public void testWithDrawSufficientFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/2/withdraw/100").build();
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		String jsonString = EntityUtils.toString(response.getEntity());
		Account afterDeposit = mapper.readValue(jsonString, Account.class);
		// check balance is decreased from 200 to 100
		assertTrue(afterDeposit.getBalance().equals(new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN)));

	}

	/*
	 * test withdraw money from account given account number, no sufficient fund in
	 * account return 500 INTERNAL SERVER ERROR
	 */
	@Test
	public void testWithDrawNonSufficientFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/2/withdraw/1000").build();
		HttpPut request = new HttpPut(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();

		assertTrue(statusCode == 500);
	}

	/*
	 * test transaction from one account to another with source account has
	 * sufficient fund return 200 OK
	 */
	@Test
	public void testTransactionEnoughFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/transaction").build();
		BigDecimal amount = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
		UserTransaction transaction = new UserTransaction("EUR", amount, 3L, 4L);

		String jsonInString = mapper.writeValueAsString(transaction);
		StringEntity entity = new StringEntity(jsonInString);
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
	}

	/*
	 * test transaction from one account to another with source account has no
	 * sufficient fund return 500 INTERNAL SERVER ERROR
	 */
	@Test
	public void testTransactionNotEnoughFund() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/transaction").build();
		BigDecimal amount = new BigDecimal(100000).setScale(4, RoundingMode.HALF_EVEN);
		UserTransaction transaction = new UserTransaction("EUR", amount, 3L, 4L);

		String jsonInString = mapper.writeValueAsString(transaction);
		StringEntity entity = new StringEntity(jsonInString);
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 500);
	}

	/*
	 * test transaction from one account to another with source/destination account
	 * with different currency code return 500 INTERNAL SERVER ERROR
	 */
	@Test
	public void testTransactionDifferentCcy() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/transaction").build();
		BigDecimal amount = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		UserTransaction transaction = new UserTransaction("USD", amount, 3L, 4L);

		String jsonInString = mapper.writeValueAsString(transaction);
		StringEntity entity = new StringEntity(jsonInString);
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 500);

	}

	@Test
	public void getAccountDetailsByAccountId() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/1").build();
		HttpGet request = new HttpGet(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		String jsonString = EntityUtils.toString(response.getEntity());
		// read JSON response like DOM Parser
		JsonNode rootNode = mapper.readTree(jsonString);
		int accountId = rootNode.get("data").get("accountId").asInt();
		// check balance is increased from 100 to 200
		assertTrue(accountId == 1);

	}

}
