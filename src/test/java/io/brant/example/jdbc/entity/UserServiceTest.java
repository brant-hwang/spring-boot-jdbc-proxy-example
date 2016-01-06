package io.brant.example.jdbc.entity;

import io.brant.example.jdbc.ApplicationInitializer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SuppressWarnings("SpringJavaAutowiredMembersInspection")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ApplicationInitializer.class})
@EnableTransactionManagement
public class UserServiceTest {

	@Autowired
	private UserService userService;


	@Test
	public void createTest() {
		User user = new User();
		user.setEmail("dlstj3039@gmail.com");
		user.setFirstName("Brant");
		user.setLastName("Hwang");
		user.setPassword("1234");

		userService.create(user);
	}

	@Test
	public void selectTest() {
		User user = userService.findByEmail("dlstj3039@gmail.com");
	}
}