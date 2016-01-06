package io.brant.example.jdbc;

import io.brant.example.jdbc.entity.User;
import io.brant.example.jdbc.entity.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/api/v1/users/create")
	public String createUser() {
		User user = new User();
		user.setEmail("dlstj3039@gmail.com");
		user.setFirstName("Brant");
		user.setLastName("Hwang");
		user.setPassword("1234");

		userService.create(user);

		return "created";
	}

	@RequestMapping(value = "/api/v1/users/{id}")
	public User getUser(@PathVariable Long id) {
		return userService.findById(id);
	}
}
