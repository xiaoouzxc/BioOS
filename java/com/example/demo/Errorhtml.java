package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Errorhtml {

	@GetMapping("error")
	public String result(Model model) {
		return "noListError.html";
	}

}
