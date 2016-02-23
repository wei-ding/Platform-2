package org.clinical3PO.common.test.controller;

//This is the test case for Index Controller using MockMvc.
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.clinical3PO.common.controller.Index;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.runners.MockitoJUnitRunner;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@RunWith(MockitoJUnitRunner.class)
public class IndexTest {

	private MockMvc mockMvc;

	@Before	
	public void setup() {
		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");

		mockMvc = MockMvcBuilders.standaloneSetup(new Index())
				.setViewResolvers(viewResolver)
				.build();
	}

	@Test
	public void PatientSearchTest()throws Exception
	{
		mockMvc.perform(
				MockMvcRequestBuilders.get("/"))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("Index"))
				.andExpect(forwardedUrl("/WEB-INF/pages/Index.jsp")); 
	}

	@Test
	public void LoginTest()throws Exception
	{
		this.mockMvc.perform(
				MockMvcRequestBuilders.get("/Login"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.view().name("Login"))
				.andExpect(forwardedUrl("/WEB-INF/pages/Login.jsp"));
	}
	@Test
	public void LoginerrorTest()throws Exception
	{
		this.mockMvc.perform(
				get("/LoginFailed"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("error", "true"))
				.andExpect(view().name("Login"))
				.andExpect(forwardedUrl("/WEB-INF/pages/Login.jsp"));
	}
	@Test
	public void LogoutTest()throws Exception
	{
		this.mockMvc.perform(
				get("/Logout"))
				.andExpect(status().isOk())
				.andExpect(view().name("Login"))
				.andExpect(forwardedUrl("/WEB-INF/pages/Login.jsp")); 
	}	
}





