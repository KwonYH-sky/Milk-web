package com.milk.milkweb.controller;

import com.milk.milkweb.dto.BoardDetailDto;
import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardUpdateDto;
import com.milk.milkweb.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

	@GetMapping(value = "/{id}")
	public String boardDetail(@PathVariable Long id, Model model) {
		BoardDetailDto detail = boardService.getDetail(id);
		model.addAttribute("boardDetail", detail);
		return "/board/boardDetail";
	}

	@GetMapping(value = "/update/{id}")
	public String getboardUpdateForm(@PathVariable Long id, Principal principal,Model model) {
		try {
			BoardUpdateDto boardUpdateDto = boardService.getUpdateForm(id, principal.getName());
			model.addAttribute("boardUpdateDto", boardUpdateDto);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "redirect:/";
		}
		return "/board/boardUpdateForm";
	}

}
