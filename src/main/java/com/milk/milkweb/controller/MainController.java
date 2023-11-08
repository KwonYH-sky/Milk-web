package com.milk.milkweb.controller;

import com.milk.milkweb.dto.MainBoardDto;
import com.milk.milkweb.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping(value = "/")
@Controller
@RequiredArgsConstructor
public class MainController {

	private final BoardService boardService;

	@GetMapping
	public String getMain(Model model) {
		Map<String, List<MainBoardDto>> lists = new HashMap<>();
		lists.put("boards", boardService.getMainBoard());
		lists.put("dailyBest", boardService.getDailyBest());
		lists.put("weeklyBest", boardService.getWeeklyBest());
		model.addAllAttributes(lists);
		return "index";
	}
}
