package com.mybank.user;

import io.javalin.Context;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.mybank.custom.exception.UserException;
import com.mybank.model.User;
import com.mybank.user.services.UserService;

@Singleton
class UserController {
    private UserService userService;

    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void getAllUsers(Context ctx) throws UserException {
		ctx.json(userService.getAllUsers());
	}

	public void getUserById(Context ctx, long UserId) throws UserException {
		ctx.json(userService.getUserById(UserId));
	}
	public void createUser(Context ctx, User User) throws UserException {
		ctx.json(userService.createUser(User));
	}
	
	public void updateUser(Context ctx, long UserId,User User) throws UserException {
		ctx.json(userService.updateUser(UserId,User));
	}
	
	public void deleteUserById(Context ctx, long UserId) throws UserException {
		ctx.json(userService.deleteUserById(UserId));
	}
}
