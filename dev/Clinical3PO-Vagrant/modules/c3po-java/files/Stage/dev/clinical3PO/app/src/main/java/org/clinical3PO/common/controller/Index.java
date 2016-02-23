package org.clinical3PO.common.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.clinical3PO.common.environment.EnvironmentType;
import org.clinical3PO.common.security.model.User;

@Controller
@RequestMapping("/")
public class Index {

	private static final Logger logger = LoggerFactory.getLogger(Index.class);

	@Autowired
	private EnvironmentType envType;

	@RequestMapping(value="/", method = RequestMethod.GET)
	public String patientSearch(ModelMap model) {
		if (envType == EnvironmentType.DEVELOPMENT && logger.isDebugEnabled()){
			logger.debug("User: "+(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
		}

		return "Index";
	}

	@RequestMapping(value="/Login", method = RequestMethod.GET)
	public String login(ModelMap model) {
		return "Login";
	}

	@RequestMapping(value="/LoginFailed", method = RequestMethod.GET)
	public String loginerror(ModelMap model) {
		model.addAttribute("error", "true");
		return "Login";
	}

	@RequestMapping(value="/Logout", method = RequestMethod.GET)
	public String logout(ModelMap model) {
		return "Login";
	}
}