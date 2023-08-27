package com.milk.milkweb.controller;

import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@RequestMapping(value = "/board")
@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@GetMapping(value = "/write")
	public String entryWriteBoardForm (Model model) {
		model.addAttribute("boardFormDto", new BoardFormDto());
		return "board/boardForm";
	}

	@PostMapping(value = "/write")
	public String writeBoard(@Valid BoardFormDto boardFormDto, BindingResult bindingResult, Principal principal, Model model) {

		if (bindingResult.hasErrors()){
			return "board/boardForm";
		}
		try {
			boardService.saveBoard(boardFormDto, principal.getName());
		} catch (Exception e) {
			model.addAttribute("errorMessage", "게시물 등록 중 에러가 발생하였습니다.");
			return "board/boardForm";
		}

		return "redirect:/";
	}

	@GetMapping(value = "/list")
	public String getBoardList (Model model) {
		List<BoardListDto> boardListDtos = boardService.getBoardList();
		model.addAttribute("boardListDtos", boardListDtos);
		return "/board/boardList";
	}
}
