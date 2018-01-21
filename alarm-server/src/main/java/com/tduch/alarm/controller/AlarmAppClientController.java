package com.tduch.alarm.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AlarmAppClientController {

	private final static Logger LOGGER = LoggerFactory.getLogger(AlarmAppClientController.class);
	
	
	@RequestMapping(
			  value = "/main", 
			  method = RequestMethod.GET
	)
	public ModelAndView appView() {
		LOGGER.info("return react app.");
		ModelAndView mav = new ModelAndView("index");
	    return mav;
	}
}
