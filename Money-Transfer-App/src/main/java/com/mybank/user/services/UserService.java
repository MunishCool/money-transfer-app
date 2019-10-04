package com.mybank.user.services;

import java.util.List;

import com.mybank.custom.exception.UserException;
import com.mybank.model.User;
import com.mybank.response.JsonResponse;

public interface UserService {

	List<User> getAllUsers() throws UserException;

	JsonResponse getUserById(long userId) throws UserException;

	User createUser(User user) throws UserException;

	JsonResponse deleteUserById(long userId) throws UserException;

	User updateUser(Long userId,User user) throws UserException;
}
