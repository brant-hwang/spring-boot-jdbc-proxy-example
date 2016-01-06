package io.brant.example.jdbc.entity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;


	public User create(User user) {
		userRepository.save(user);
		return user;
	}

	public boolean existUser(String email) {
		User user = findByEmail(email);
		return user != null ? true : false;
	}

	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	public User findById(Long id) {
		return userRepository.findOne(id);
	}
}
