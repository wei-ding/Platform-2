package org.clinical3PO.common.angular.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.form.beans.UserInfo;
import org.clinical3PO.common.security.model.User;
import org.clinical3PO.common.security.service.UserService;
import org.clinical3PO.services.JobSearchService;
import org.clinical3PO.services.dao.model.JobSearch;
import org.json.JSONStringer;
import org.perf4j.aop.Profiled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/ang/MySearch")
@Component("MySearchAng")
public class MySearch {
	private static final Logger logger = LoggerFactory.getLogger(MySearch.class);
	@Autowired
	private UserService userService;
	@Autowired
	JobSearchService jobSearch;
	
	
	
	@Autowired
	private EnvironmentType envType;
	@Profiled (tag = "Retrieving the jobs")
	@RequestMapping(value="/json/data",method= RequestMethod.GET, produces = "application/json; charset=utf-8")
	public @ResponseBody String getJobs1(ModelMap model,ServletRequest req, ServletResponse res) {
		logger.debug("Received request to get all jobs");
		
	  
		/*HttpServletResponse response = (HttpServletResponse) res;
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "Content-Type,X-Requested-With,accept,Origin,Access-Control-Request-Method,Access-Control-Request-Headers");
		*/
		User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		assert (user != null);
		
		List<JobSearch> jobs = jobSearch.getJobs(user.getId());
	logger.info("hcUser "+user.getId());
	    String outJsonBuf=null;  
	    
	    JSONStringer jsonString = new JSONStringer();
	    try
	    {
	    	jsonString.array();
	    	} catch(Exception e){
	    		logger.error("Exception: " + e);
	    		}
	    try{
	    	for (JobSearch jobsearch:jobs){
	    		List<String> t1=new ArrayList<String>();
	    		for(int m=0;m<(jobsearch.getSearchParameters()).size();m++){
	    			String t=(jobsearch.getSearchParameters()).get(m).getKey()+":"+(jobsearch.getSearchParameters()).get(m).getValue();
	    			t1.add(t);
	    			}
	    		
	    		jsonString.object();
	    		try
	    		{
	    			jsonString.key("searchBy");
			        jsonString.value(jobsearch.getSearchBy());
	    			jsonString.key("id");
			        jsonString.value(jobsearch.getId());
	    			jsonString.key("searchOn");
			        jsonString.value(jobsearch.getSearchOn());
			        jsonString.key("searchStartTime");
			        jsonString.value(jobsearch.getSearchStartTime());
			        jsonString.key("searchEndTime");
			        jsonString.value(jobsearch.getSearchEndTime());
			        jsonString.key("searchParameters");
			        jsonString.value(t1);
			        jsonString.key("status");
			        jsonString.value(jobsearch.getStatus());
			        }
	    		catch(Exception e){
	    			logger.error("Exception: " + e);
	    			}
	    		jsonString.endObject();
	    		}
	    	}
	    catch(Exception e){
	    	logger.error("Exception"+e);
	    	}
	    try
	    {
	    	jsonString.endArray();
	    	} catch(Exception e){
	    		logger.error("Exception: " + e);
	    		}
	    
	    outJsonBuf = jsonString.toString();
	    logger.info("buffer"+outJsonBuf);
	    
	    if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
			logger.debug("List of Jobs: "+outJsonBuf);
			}
	    if (envType == EnvironmentType.PRODUCTION){
			logger.info("Jobs size "+jobs.size());
			}
	    
	    return (outJsonBuf);
	    
	    }
		
	@RequestMapping(value="/user2",method=RequestMethod.POST, headers = "Content-Type=application/x-www-form-urlencoded")
	public @ResponseBody String user(UserInfo userinfo){

		String username=userinfo.getName();
		User user = userService.loadUserByUsername(username);
      
        	return "{\"name\":\""+user.getUsername()+"\",\"password\":\""+user.getPassword()+"\"}";
        	
        
	}

}
