package com.mybank.user;

import static io.javalin.ApiBuilder.delete;
import static io.javalin.ApiBuilder.get;
import static io.javalin.ApiBuilder.path;
import static io.javalin.ApiBuilder.post;
import static io.javalin.ApiBuilder.put;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mybank.entrypoint.Routing;
import com.mybank.model.User;

import io.javalin.Javalin;

@Singleton
class UserRouting extends Routing<UserController> {
	private Javalin javalin;

	@Inject
	public UserRouting(Javalin javalin) {
		this.javalin = javalin;
	}

	@Override
	public void bindRoutes() {
		javalin.routes(() -> {
			path("api/user/all", () -> {
				get(ctx -> getController().getAllUsers(ctx));
			});
			path("api/user/:UserId", () -> {
				get(ctx -> getController().getUserById(ctx, Long.valueOf(ctx.param("UserId"))));
			});
			path("api/user/create", () -> {
				post(ctx -> getController().createUser(ctx, ctx.bodyAsClass(User.class)));

			});
			path("api/user/update/:UserId", () -> {
				put(ctx -> getController().updateUser(ctx, Long.valueOf(ctx.param("UserId")),
						ctx.bodyAsClass(User.class)));

			});
			path("api/user/:UserId", () -> {
				delete(ctx -> getController().deleteUserById(ctx, Long.valueOf(ctx.param("UserId"))));
			});
		});
	}
}
