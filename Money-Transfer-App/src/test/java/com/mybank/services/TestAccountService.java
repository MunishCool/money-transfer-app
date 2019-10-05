package com.mybank.services;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
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
import com.mybank.enums.AccountType;
import com.mybank.model.Account;
import com.mybank.server.App;

public class TestAccountService {

	protected ObjectMapper mapper = new ObjectMapper();
	protected URIBuilder builder = new URIBuilder().setScheme("http").setHost("localhost:7000");
	protected static PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager();

	protected static HttpClient client;
	protected static DAOFactory h2DaoFactory = DAOFactory.getDAOFactory(DAOFactory.H2);

	@BeforeClass
	public static void setup() throws Exception {

		String arr[] = null;
		App.startServer(arr);
		// h2DaoFactory.bumpDummyData();
		connManager.setDefaultMaxPerRoute(100);
		connManager.setMaxTotal(200);
		client = HttpClients.custom().setConnectionManager(connManager).setConnectionManagerShared(true).build();

	}

	@AfterClass
	public static void closeClient() throws Exception {
		HttpClientUtils.closeQuietly(client);
	}

	/*
	 * TC A1 Positive Category = AccountService Scenario: test get user account by
	 * user name return 200 OK
	 */
	@Test
	public void testGetAccountByUserName() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/1").build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();

		assertTrue(statusCode == 200);
		// check the content
		String jsonString = EntityUtils.toString(response.getEntity());
		// read JSON response like DOM Parser
		JsonNode rootNode = mapper.readTree(jsonString);
		assertTrue(rootNode.get("data").get("customerName").asText().equals("munish"));
	}

	/*
	 * TC A2 Positive Category = AccountService Scenario: test get all user accounts
	 * return 200 OK
	 */
	@Test
	public void testGetAllAccounts() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/all").build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		// check the content
		String jsonString = EntityUtils.toString(response.getEntity());
		Account[] accounts = mapper.readValue(jsonString, Account[].class);
		assertTrue(accounts.length > 0);
	}

	/*
	 * TC A3 Positive Category = AccountService Scenario: test get account balance
	 * given account ID return 200 OK
	 */
	@Test
	public void testGetAccountBalance() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/1/balance").build();
		HttpGet request = new HttpGet(uri);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		// check the content, assert user test2 have balance 100
		String balance = EntityUtils.toString(response.getEntity());

		// read JSON response like DOM Parser
		JsonNode rootNode = mapper.readTree(balance);
		double val = rootNode.get("data").asDouble();

		BigDecimal res = new BigDecimal(val).setScale(4, RoundingMode.HALF_EVEN);
		BigDecimal db = new BigDecimal(100).setScale(4, RoundingMode.HALF_EVEN);
		assertTrue(res.equals(db));
	}

	/*
	 * TC A4 Positive Category = AccountService Scenario: test create new user
	 * account return 200 OK
	 */
	@Test
	public void testCreateAccount() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/create").build();
		BigDecimal balance = new BigDecimal(10).setScale(4, RoundingMode.HALF_EVEN);
		Account acc = new Account(7, "test7", "test2@gmail.com", "China", "8900112288", "UID", "123", balance, "CNY",
				AccountType.SAVING_ACCOUNT,true);
		String jsonInString = mapper.writeValueAsString(acc);
		StringEntity entity = new StringEntity(jsonInString);
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
		String jsonString = EntityUtils.toString(response.getEntity());
		Account aAfterCreation = mapper.readValue(jsonString, Account.class);
		assertTrue(aAfterCreation.getcustomerName().equals("test7"));
		assertTrue(aAfterCreation.getCurrencyCode().equals("CNY"));
	}

	/*
	 * TC A5 Negative Category = AccountService Scenario: test create user account
	 * already existed. return 500 INTERNAL SERVER ERROR
	 */
	@Test
	public void testCreateExistingAccount() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/create").build();
		Account acc = new Account("test1", "test1@gmail.com", "USD", "8900112288", "UID", "123", new BigDecimal(0),
				"USD");
		String jsonInString = mapper.writeValueAsString(acc);
		StringEntity entity = new StringEntity(jsonInString);
		HttpPost request = new HttpPost(uri);
		request.setHeader("Content-type", "application/json");
		request.setEntity(entity);
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 500);

	}

	/*
	 * TC A6 Positive Category = AccountService Scenario: delete valid user account
	 * return 200 OK
	 */
	@Test
	public void testDeleteAccount() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/3").build();
		HttpDelete request = new HttpDelete(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		int statusCode = response.getStatusLine().getStatusCode();
		assertTrue(statusCode == 200);
	}

	/*
	 * TC A7 Negative Category = AccountService Scenario: test delete non-existent
	 * account. return 404 NOT FOUND return 404 NOT FOUND
	 */
	@Test
	public void testDeleteNonExistingAccount() throws IOException, URISyntaxException {
		URI uri = builder.setPath("/api/account/300").build();
		HttpDelete request = new HttpDelete(uri);
		request.setHeader("Content-type", "application/json");
		HttpResponse response = client.execute(request);
		String jsonString = EntityUtils.toString(response.getEntity());
		// read JSON response like DOM Parser
		JsonNode rootNode = mapper.readTree(jsonString);
		int statusCode = rootNode.get("responseCode").asInt();
		assertTrue(statusCode == 404);
	}

}
