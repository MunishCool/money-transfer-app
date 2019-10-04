package com.mybank.user.repositories;

import java.util.List;

import com.mybank.custom.exception.UserException;
import com.mybank.model.User;

public interface UserRepository {

	User getUserById(long userId) throws UserException;

	List<User> getAllUsers() throws UserException;

	long createUser(User user) throws UserException;

	int deleteUserById(long userId) throws UserException;

	long updateUser(long userId, User user) throws UserException;
}
