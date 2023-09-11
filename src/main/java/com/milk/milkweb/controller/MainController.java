package com.milk.milkweb.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value = "/")
@Controller
@RequiredArgsConstructor
public class MainController {

	@GetMapping
	public String main() {
		return "index";
	}
}
