package org.clinical3PO.common.test.controller;
// This is test case for RPatientSearch controller
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.clinical3PO.common.controller.RPatientSearch;
import org.clinical3PO.common.security.model.User;
import org.clinical3PO.model.JobSearchDetails;
import org.clinical3PO.model.JobSearchParameter;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.dao.model.JobSearch;
import org.clinical3PO.test.data.builder.JobSearchBuilder;
import org.clinical3PO.test.data.builder.JobSearchDetailsBuilder;
import org.clinical3PO.test.data.builder.JobSearchParameterBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@RunWith(PowerMockRunner.class)
public class RPatientSearchTest {

	private MockMvc mockMvc;

	@Mock
	private JobSearchService jobSearchService;

	@Mock
	private JobSearch jobSearch;

	@Mock
	private JobSearchParameter jobSearchParameter;

	@Mock
	private JobSearchDetails jobSearchDetails;

	@Mock
	MockServletContext servletContext=new MockServletContext();

	@Mock
	private User user;

	@Mock
	List<JobSearchParameter> searchParameters=new ArrayList<JobSearchParameter>(1);

	@InjectMocks
	RPatientSearch RpatientSearch=new RPatientSearch();

	private LocalValidatorFactoryBean validator() {
		return new LocalValidatorFactoryBean();
	}

	@Before
	public void setup()throws Exception
	{
		MockitoAnnotations.initMocks(this);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/pages/");
		viewResolver.setSuffix(".jsp");

		this.mockMvc = MockMvcBuilders.standaloneSetup(RpatientSearch)
				.setValidator(validator())
				.setViewResolvers(viewResolver).build();

		Authentication authentication = Mockito.mock(Authentication.class);
		SecurityContext securityContext = Mockito.mock(SecurityContext.class);
		Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
		SecurityContextHolder.setContext(securityContext);
		Mockito.when(authentication.getPrincipal()).thenReturn(user);


		Mockito.when(user.getId()).thenReturn(1);

		PowerMockito.whenNew(JobSearch.class).withNoArguments().thenReturn(jobSearch);
		PowerMockito.whenNew(JobSearchDetails.class).withNoArguments().thenReturn(jobSearchDetails); 
	}

	@Test
	public void getPatientTest_WithFormViewAndReturnValidationErrorsForit()throws Exception
	{
		String patientId = "123456789";
		mockMvc.perform(MockMvcRequestBuilders.post("/RPatientSearch/")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("patientId",patientId))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("PatientSearchView"))
				.andExpect(forwardedUrl("/WEB-INF/pages/PatientSearchView.jsp"))
				.andExpect(model().attributeHasFieldErrors("patientSearchForm","patientId"))
				.andExpect(model().attribute("patientSearchForm", hasProperty("patientId", is(patientId))));
	}
	@Test
	public void getPatientTest()throws Exception
	{

		jobSearchParameter=new JobSearchParameterBuilder()
		.key("Patient ID")
		.value("123456")
		.groupId(1)
		.Build();
		PowerMockito.whenNew(JobSearchParameter.class).withArguments("Patient ID","123456",1).thenReturn(jobSearchParameter);
		searchParameters=Arrays.asList(jobSearchParameter);

		jobSearch=new JobSearchBuilder()
		.searchBy(1)
		.searchParameters(searchParameters).Build();

		jobSearchDetails=new JobSearchDetailsBuilder()
		.searchOn("Patient ID R")
		.searchType("PatientId")
		.searchParameters("123456")
		.scriptType("11")
		.scriptParameters("123456").Build();

		jobSearchService.searchJob(jobSearch, jobSearchDetails);

		this.mockMvc.perform(
				MockMvcRequestBuilders.post("/RPatientSearch/")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("patientId","123456"))
				.andDo(print())
				.andExpect(status().isMovedTemporarily())
				.andExpect(view().name("redirect:/MySearch/"))
				.andExpect(redirectedUrl("/MySearch/"))
				.andExpect(model().attribute("patientSearchForm", hasProperty("patientId",is("123456"))));

		Mockito.verify(jobSearchService).searchJob(jobSearch,jobSearchDetails); 
	}
	@Test
	public void patientSearchTest()throws Exception{

		mockMvc.perform(
				MockMvcRequestBuilders.get("/RPatientSearch/"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("PatientSearchView"))
				.andExpect(forwardedUrl("/WEB-INF/pages/PatientSearchView.jsp"));		
	}



}
