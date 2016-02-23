package org.clinical3PO.common.test.controller;

// This Test case to test the FileDownload Controller using MockMvc.
import org.clinical3PO.common.controller.FileDownload;
import org.clinical3PO.services.JobSearchService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@RunWith(MockitoJUnitRunner.class)


public class FileDownloadTest {

	private MockMvc mockMvc;
	@Mock
	private JobSearchService jobSearchService;
	@Mock
	MockServletContext servletContext=new MockServletContext();
	@Mock
	MockHttpServletRequest request=new MockHttpServletRequest();
	@Mock
	MockHttpServletResponse response=new MockHttpServletResponse();
	@InjectMocks
	FileDownload fileDownload=new FileDownload();

	@Before	
	public void setup() throws Exception{

		MockitoAnnotations.initMocks(this);

		this.mockMvc = MockMvcBuilders.standaloneSetup(fileDownload).build();
		String ContentType = servletContext.getMimeType("src/test/resources/${clinical3PO.app.dataDirectory}");
		Mockito.when(jobSearchService.getAppDataDirectory()).thenReturn("src/test/resources/${clinical3PO.app.dataDirectory}");
		Mockito.when(servletContext.getMimeType("src/test/resources/${clinical3PO.app.dataDirectory}")).thenReturn(ContentType);
		Mockito.when(response.getContentType()).thenReturn(ContentType);
	}

	@Test
	public void doDownloadTest() throws Exception
	{
		mockMvc.perform(
				MockMvcRequestBuilders.get("/FileDownload/DownloadFile/{id}",10))
				.andDo(print())
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(content().string("135323,135325,135326,135327,135330,135331,135336,135341,135342,135343,135346,135347,135352,135355,135357|HR|ff0000"))
				.andExpect(content().contentType("application/octet-stream"))
				.andExpect(header().string("Content-Disposition", String.format("attachment; filename=\"UploadedFile-10.txt\"")));
	}
}
