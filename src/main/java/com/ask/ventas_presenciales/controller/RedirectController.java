package com.ask.ventas_presenciales.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class RedirectController {
	
	@RequestMapping("/")
	public String redirectToProducts() {
		return "redirect:/productos";
	}

}