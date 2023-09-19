package com.milk.milkweb.controller;

import com.milk.milkweb.dto.MainBoardDto;
import com.milk.milkweb.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping(value = "/")
@Controller
@RequiredArgsConstructor
public class MainController {

	private final BoardService boardService;

	@GetMapping
	public String getMain(Model model) {
		List<MainBoardDto> boardList = boardService.getMainBoard();
		model.addAttribute("boards", boardList);
		return "index";
	}
}
