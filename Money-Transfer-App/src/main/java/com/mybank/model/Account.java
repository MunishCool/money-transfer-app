package com.mybank.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mybank.enums.AccountType;

public class Account {

	@JsonProperty
	private long accountId;

	@JsonProperty(required = true)
	private AccountType accountType;

	@JsonProperty(required = true)
	private String customerName;

	@JsonProperty(required = true)
	private String customerEmail;

	@JsonProperty(required = true)
	private String customerAddress;

	@JsonProperty(required = true)
	private String customerMobile;

	@JsonProperty(required = true)
	private String customerIdProof;

	@JsonProperty
	private String customerPassword;

	@JsonProperty
	private BigDecimal balance;

	@JsonProperty(required = true)
	private String currencyCode;

	@JsonProperty(required = true)
	private boolean accountStatus;

	public Account() {
	}

	public Account(String customerName, String customerEmail, String customerAddress, String customerMobile,
			String customerIdProof, String customerPassword, BigDecimal balance, String currencyCode) {
		this.customerName = customerName;
		this.customerEmail = customerEmail;
		this.customerAddress = customerAddress;
		this.customerMobile = customerMobile;
		this.customerIdProof = customerIdProof;
		this.customerPassword = customerPassword;
		this.balance = balance;
		this.currencyCode = currencyCode;
	}

	public Account(long accountId, String customerName, String customerEmail, String customerAddress,
			String customerMobile, String customerIdProof, String customerPassword, BigDecimal balance,
			String currencyCode, AccountType accountType, boolean accountStatus) {
		this.accountId = accountId;
		this.customerName = customerName;
		this.customerEmail = customerEmail;
		this.customerAddress = customerAddress;
		this.customerMobile = customerMobile;
		this.customerIdProof = customerIdProof;
		this.customerPassword = customerPassword;
		this.balance = balance;
		this.currencyCode = currencyCode;
		this.accountType = accountType;
		this.accountStatus = accountStatus;
	}

	public String getcustomerEmail() {
		return customerEmail;
	}

	public void setcustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}

	public String getcustomerAddress() {
		return customerAddress;
	}

	public void setcustomerAddress(String customerAddress) {
		this.customerAddress = customerAddress;
	}

	public long getAccountId() {
		return accountId;
	}

	public String getcustomerName() {
		return customerName;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getCustomerMobile() {
		return customerMobile;
	}

	public void setCustomerMobile(String customerMobile) {
		this.customerMobile = customerMobile;
	}

	public String getCustomerIdProof() {
		return customerIdProof;
	}

	public void setCustomerIdProof(String customerIdProof) {
		this.customerIdProof = customerIdProof;
	}

	public String getCustomerPassword() {
		return customerPassword;
	}

	public void setCustomerPassword(String customerPassword) {
		this.customerPassword = customerPassword;
	}

	public boolean isAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(boolean accountStatus) {
		this.accountStatus = accountStatus;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Account account = (Account) o;

		if (accountId != account.accountId)
			return false;
		if (!customerName.equals(account.customerName))
			return false;
		if (!customerEmail.equals(account.customerEmail))
			return false;
		if (!customerAddress.equals(account.customerAddress))
			return false;
		if (!balance.equals(account.balance))
			return false;
		return currencyCode.equals(account.currencyCode);

	}

	@Override
	public int hashCode() {
		int result = (int) (accountId ^ (accountId >>> 32));
		result = 31 * result + customerName.hashCode();
		result = 31 * result + customerEmail.hashCode();
		result = 31 * result + customerAddress.hashCode();
		result = 31 * result + balance.hashCode();
		result = 31 * result + currencyCode.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Account{" + "accountId=" + accountId + ", " + "accountType='" + accountType + '\'' + ", "
				+ "customerName='" + customerName + '\'' + ", " + "balance=" + balance + ", " + "customerEmail="
				+ customerEmail + "," + "customerMobile=" + customerMobile + "," + "customerIdProof=" + customerIdProof
				+ "," + " " + "customerAddress=" + customerAddress + ", " + "currencyCode='" + currencyCode + '\''
				+ '}';
	}
}
