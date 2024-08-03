package org.kerminator.hello;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.kerminator.hello.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


@SpringBootTest
@AutoConfigureMockMvc
class HelloApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void contextLoads() {}

	@Test
	void root() throws Exception {
		mockMvc.perform(get("/")).andExpect(status().isOk()).andExpect(content().string("Hello World"));
	}

	@Test
	void testtest() throws Exception {
		mockMvc.perform(get("/test")).andExpect(status().isOk()).andExpect(content().string("Test Test"));
	}

	@Test
	void getemptyproduct() throws Exception {
		mockMvc.perform(get("/products")).andExpect(status().is2xxSuccessful());
	}

	@Test
	void createproduct() throws Exception {
		Product product = new Product("Testi","Ukko");

		mockMvc.perform( MockMvcRequestBuilders
				.post("/products")
				.content(objectMapper.writeValueAsString(product))
				.contentType(MediaType.APPLICATION_JSON));


		//);
	}
}
