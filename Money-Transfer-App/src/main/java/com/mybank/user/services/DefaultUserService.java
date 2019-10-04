package com.mybank.user.services;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.jetty.http.HttpStatus;

import com.mybank.custom.exception.UserException;
import com.mybank.model.User;
import com.mybank.response.JsonResponse;
import com.mybank.user.repositories.UserRepository;

@Singleton
class DefaultUserService implements UserService {
	private UserRepository userRepository;

	@Inject
	public DefaultUserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Find all Users
	 * 
	 * @return
	 * @throws UserException
	 */
	@Override
	public List<User> getAllUsers() throws UserException {
		List<User> Users = userRepository.getAllUsers();
		return Users;
	}

	/**
	 * Find by User id
	 * 
	 * @param UserId
	 * @return
	 * @throws UserException
	 */
	@Override
	public JsonResponse getUserById(long UserId) throws UserException {
		JsonResponse response = new JsonResponse();
		User User = userRepository.getUserById(UserId);
		if (null != User) {
			response.setData(User);
			response.setMessage("Successfully fetched the User details");
		} else {
			response.setData(null);
			response.setStatus("NOT_FOUND_404");
		}

		return response;
	}

	/**
	 * Create User
	 * 
	 * @param User object
	 * @return
	 * @throws UserException
	 */
	@Override
	public User createUser(User User) throws UserException {
		final long UserId = userRepository.createUser(User);
		return userRepository.getUserById(UserId);
	}

	/**
	 * Deposit User by User Id
	 * 
	 * @param UserId
	 * @return
	 * @throws UserException
	 */
	public JsonResponse deleteUserById(long UserId) throws UserException {
		JsonResponse response = new JsonResponse();
		int deleteCount = userRepository.deleteUserById(UserId);
		if (deleteCount == 1) {
			response.setMessage("Successfully deleted the User");
			response.setData(deleteCount);
			response.setResponseCode(HttpStatus.OK_200);
			response.setStatus(JsonResponse.getSuccessStatus());
			return response;
		}
		if (deleteCount == 0) {
			response.setMessage("No record found with UserId {} " + UserId);
			response.setData(null);
			response.setResponseCode(HttpStatus.NOT_FOUND_404);
			response.setStatus(JsonResponse.getSuccessStatus());
			return response;
		} else {
			response.setMessage("Something went wrong");
			response.setResponseCode(HttpStatus.INTERNAL_SERVER_ERROR_500);
			response.setData(null);
			response.setStatus(JsonResponse.getStatusFailure());
			return response;
		}
	}


	@Override
	public User updateUser(Long userId, User user) throws UserException {
		final long UserId = userRepository.updateUser(userId, user);
		System.out.println(UserId);
		return userRepository.getUserById(userId);
	}

}
