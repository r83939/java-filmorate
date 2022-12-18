package ru.yandex.practicum.filmorate;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import junit.framework.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.yandex.practicum.filmorate.controller.UserController;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTests {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private UserController userController;

	@Test
	void contextLoads() {
		assertThat(userController).isNotNull();
	}

	@Test
	void whenCorrectRequestShouldReturns200() throws Exception {
		String requestBody = "{\"login\":\"joan\",\"name\":\"Joan\",\"email\":\"joan@gmail.ru\",\"birthday\":\"1941-01-09\"}";
		MvcResult result =  mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isOk())
				.andReturn();
		JsonObject convertedObject = new Gson().fromJson(result.getResponse().getContentAsString(), JsonObject.class);
		Assert.assertTrue("Неверный логин",convertedObject.get("login").getAsString().equals("joan"));
		Assert.assertTrue("Неверное имя",convertedObject.get("name").getAsString().equals("Joan"));
		Assert.assertTrue("Неверная email",convertedObject.get("email").getAsString().equals("joan@gmail.ru"));
		Assert.assertTrue("Неверный день рождения",convertedObject.get("birthday").getAsString().equals("1941-01-09"));
	}

	@Test
	void whenEmailEmptyShouldReturns400() throws Exception {
		String requestBody = "{\"login\":\"danila\",\"name\":\"Danila\",\"email\":\"\",\"birthday\":\"1980-08-20\"}";
		mockMvc.perform(post("/users")
						.contentType("application/json")
				        .content(requestBody))
				        .andExpect(status().isBadRequest());
	}

	@Test
	void whenEmailWithoutATShouldReturns400() throws Exception {
		String requestBody = "{\"login\":\"ivan\",\"name\":\"Ivan\",\"email\":\"ivanpetrovmail.ru\",\"birthday\":\"1990-08-20\"}";
		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void whenLoginEmptyShouldReturns400() throws Exception {
		String requestBody = "{\"login\":\"\",\"name\":\"Ivan\",\"email\":\"ivanpetrov@mail.ru\",\"birthday\":\"1990-08-20\"}";
		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void whenLoginHaveWhiteSpaceShouldReturns400() throws Exception {
		String requestBody = "{\"login\":\"ivan petrov\",\"name\":\"Ivan\",\"email\":\"ivanpetrov@mail.ru\",\"birthday\":\"1990-08-20\"}";
		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isBadRequest());
	}

	@Test
	void whenNameEmptyNameEqualLoginAndShouldReturn200() throws Exception {
		String requestBody = "{\"login\":\"ivan\",\"name\":\"\",\"email\":\"ivanpetrov@mail.ru\",\"birthday\":\"1990-08-20\"}";
		MvcResult result =  mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(requestBody))
				        .andExpect(status().isOk())
				        .andReturn();
		JsonObject convertedObject = new Gson().fromJson(result.getResponse().getContentAsString(), JsonObject.class);
		Assert.assertTrue("Имя для отображения должно быть как логин",convertedObject.get("name").getAsString().equals("ivan"));
	}

	@Test
	void whenBirthdayInFutureShouldReturn400() throws Exception {
		String requestBody = "{\"login\":\"ivan\",\"name\":\"ivan\",\"email\":\"ivanpetrov@mail.ru\",\"birthday\":\"2030-08-20\"}";
		mockMvc.perform(post("/users")
						.contentType("application/json")
						.content(requestBody))
				.andExpect(status().isBadRequest());
	}
}
