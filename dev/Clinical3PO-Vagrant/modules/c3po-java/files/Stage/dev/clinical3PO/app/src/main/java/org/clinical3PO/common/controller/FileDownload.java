package org.clinical3PO.common.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.clinical3PO.services.JobSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/FileDownload")
public class FileDownload {

	@Autowired
	JobSearchService jobSearchService;
	private static final int BUFFER_SIZE = 4096;

	private @Autowired ServletContext servletContext;

	@RequestMapping(value="/DownloadFile/{id}", method = RequestMethod.GET)
    public void doDownload(@PathVariable("id") String fileNameID, HttpServletRequest request,
            HttpServletResponse response) throws IOException {
 
    	String fileName = "UploadedFile-"+ fileNameID + ".txt";
    	String fullPath = jobSearchService.getAppDataDirectory() + File.separator+"batchUploads"+File.separator + fileName;
    	
    	File downloadFile = new File(fullPath);
        FileInputStream inputStream = new FileInputStream(downloadFile);
         
        String mimeType = servletContext.getMimeType(fullPath);
        if (mimeType == null) {
            // set to binary type if MIME mapping not found
            mimeType = "application/octet-stream";
        }
 
        response.setContentType(mimeType);
        response.setContentLength((int) downloadFile.length());
 
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                downloadFile.getName());
        response.setHeader(headerKey, headerValue);
 
        OutputStream outStream = response.getOutputStream();
 
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = -1;
 
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, bytesRead);
        }
 
        inputStream.close();
        outStream.close();
 
    }
	
}
