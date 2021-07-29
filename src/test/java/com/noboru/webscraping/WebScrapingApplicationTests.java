package com.noboru.webscraping;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
class WebScrapingApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void mustCalculateInformationAndReturnJsonList() throws Exception {
		String queryParamUrlRepository = "https://github.com/edsonnoboru/web-scraping";

		mockMvc.perform(MockMvcRequestBuilders.get("/api/git-hub/repository/info?url=" + queryParamUrlRepository))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.[0].extension", is(notNullValue())))
				.andExpect(jsonPath("$.[0].count", is(notNullValue())))
				.andExpect(jsonPath("$.[0].lines", is(notNullValue())))
				.andExpect(jsonPath("$.[0].bytes", is(notNullValue())));
	}

}
