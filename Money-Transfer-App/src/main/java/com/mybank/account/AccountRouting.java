package com.mybank.account;

import static io.javalin.ApiBuilder.delete;
import static io.javalin.ApiBuilder.get;
import static io.javalin.ApiBuilder.path;
import static io.javalin.ApiBuilder.post;
import static io.javalin.ApiBuilder.put;

import java.math.BigDecimal;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mybank.entrypoint.Routing;
import com.mybank.model.Account;
import com.mybank.model.UserTransaction;

import io.javalin.Javalin;

/**
 * 
 * @author munish
 *
 */
@Singleton
class AccountRouting extends Routing<AccountController> {
	private Javalin javalin;

	@Inject
	public AccountRouting(Javalin javalin) {
		this.javalin = javalin;
	}

	@Override
	public void bindRoutes() {
		javalin.routes(() -> {

			path("api/account/all", () -> {
				get(ctx -> getController().getAllAccounts(ctx));
			});
			path("api/account/:accountId", () -> {
				get(ctx -> getController().getAccountById(ctx, Long.valueOf(ctx.param("accountId"))));
			});
			path("api/account/:accountId/balance", () -> {
				get(ctx -> getController().getAccountBalanceById(ctx, Long.valueOf(ctx.param("accountId"))));
			});
			path("api/account/create", () -> {
				post(ctx -> getController().createAccount(ctx, ctx.bodyAsClass(Account.class)));

			});
			path("api/account/update/:field/:value/:accountId", () -> {
				put(ctx -> getController().updateAccount(ctx, ctx.param("field"), ctx.param("value"),
						Long.valueOf(ctx.param("accountId"))));

			});
			path("api/account/:accountId/deposit/:amount", () -> {
				put(ctx -> getController().depositAmountToAccount(ctx, Long.valueOf(ctx.param("accountId")),
						BigDecimal.valueOf(Double.valueOf(ctx.param("amount")))));
			});
			path("api/account/:accountId/withdraw/:amount", () -> {
				put(ctx -> getController().withDramAmountFromAccount(ctx, Long.valueOf(ctx.param("accountId")),
						BigDecimal.valueOf(Double.valueOf(ctx.param("amount")))));
			});
			path("api/account/:accountId", () -> {
				delete(ctx -> getController().deleteAccountById(ctx, Long.valueOf(ctx.param("accountId"))));
			});
			path("api/account/transaction", () -> {
				post(ctx -> getController().transferFunds(ctx, ctx.bodyAsClass(UserTransaction.class)));

			});
		});
	}
}
