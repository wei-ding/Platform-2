package org.clinical3PO.common.test.controller;
//This is the test case for MySearch controller using MockMvc.
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.clinical3PO.common.controller.MySearch;
import org.clinical3PO.common.security.model.User;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.dao.model.JobSearch;
import org.clinical3PO.test.data.builder.JobSearchBuilder;
import org.clinical3PO.test.data.builder.JobSearchParameterBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@RunWith(MockitoJUnitRunner.class)

public class MySearchTest {
	private MockMvc mockMvc;
	@Mock
	private JobSearchService jobSearchService;
	@Mock
	private User user;
	@Mock
	JobSearch jobSearch;

	@InjectMocks
	MySearch mySearch=new MySearch();

	@Before
	public void setup()throws Exception{
		MockitoAnnotations.initMocks(this);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");

		this.mockMvc = MockMvcBuilders.standaloneSetup(mySearch).setViewResolvers(viewResolver).build();

		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(authentication.getPrincipal()).thenReturn(user);


		Mockito.when(user.getId()).thenReturn(1);
	}
	@SuppressWarnings("unchecked")
	@Test
	public void PatientSearchTest()throws Exception{
		SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy/dd/MM HH:mm:ss")  ;

		JobSearchParameter searchParameters=new JobSearchParameterBuilder()
		.key("Patient ID")
		.value("123456")
		.groupId(1)
		.Build();

		JobSearch jobs=new JobSearchBuilder()
		.id(15)
		.createdDate(inputDateFormat.parse("2015/06/22 11:23:50.0"))
		.searchStartTime(inputDateFormat.parse("2015/06/22 11:23:50.0"))
		.outputFileName("PatientId123456Time20150609104448358Output")
		.searchOn("Patient ID")
		.outputDirectory("/home/c3po/clinical3PO-hadoop-output")
		.status("IN-PROGRESS")
		.hadoopOutputDirectory("PatientId123456Time20150609104448358")
		.searchParameters(Arrays.asList(searchParameters))
		.Build();


		Mockito.when(jobSearchService.getJobs(user.getId())).thenReturn(Arrays.asList(jobs));
		mockMvc.perform(
				MockMvcRequestBuilders.get("/MySearch/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("MysearchesView"))
				.andExpect(forwardedUrl("/WEB-INF/pages/MysearchesView.jsp"))
				.andExpect(model().attribute("jobs", hasSize(1)))
				.andExpect(model().attribute("jobs", hasItem(
						allOf(
								hasProperty("id", is(15)),
								hasProperty("createdDate", is(inputDateFormat.parse("2015/06/22 11:23:50.0"))),
								hasProperty("searchStartTime", is(inputDateFormat.parse("2015/06/22 11:23:50.0"))),
								hasProperty("outputFileName", is("PatientId123456Time20150609104448358Output")),
								hasProperty("searchOn", is("Patient ID")),
								hasProperty("outputDirectory", is("/home/c3po/clinical3PO-hadoop-output")), 
								hasProperty("status", is("IN-PROGRESS")),
								hasProperty("searchParameters",is(Arrays.asList(searchParameters))),
								hasProperty("hadoopOutputDirectory", is("PatientId123456Time20150609104448358"))


								)
						)));
	}

}
