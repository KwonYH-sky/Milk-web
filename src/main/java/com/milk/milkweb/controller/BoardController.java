package com.milk.milkweb.controller;

import com.milk.milkweb.dto.*;
import com.milk.milkweb.service.BoardLikeService;
import com.milk.milkweb.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RequestMapping(value = "/board")
@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final BoardLikeService boardLikeService;

	@GetMapping(value = "/write")
	public String entryWriteBoardForm(Model model, Principal principal) {
		if (!Optional.ofNullable(principal).isPresent()) {
			model.addAttribute("errorMessage", "로그인 해주세요.");
			return "redirect:";
		}
		model.addAttribute("boardFormDto", new BoardFormDto());
		return "board/boardForm";
	}

	@PostMapping(value = "/write")
	public String writeBoard(@Valid BoardFormDto boardFormDto, BindingResult bindingResult, Principal principal, Model model) {

		if (bindingResult.hasErrors()) {
			return "board/boardForm";
		}
		try {
			boardService.saveBoard(boardFormDto, principal.getName());
		} catch (Exception e) {
			model.addAttribute("errorMessage", "게시물 등록 중 에러가 발생하였습니다.");
			return "board/boardForm";
		}

		return "redirect:/board/list";
	}

	@GetMapping(value = {"", "/list"})
	public String getBoardList(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		try {
			Page<BoardListDto> boardListDtos = boardService.getBoardList(page);
			model.addAttribute("paging", boardListDtos);
		} catch (Exception e) {
			model.addAttribute("paging", boardService.getBoardList(0));
		}

		return "/board/boardList";
	}

	@GetMapping(value = "/{id}")
	public String boardDetail(@PathVariable Long id, Model model) {
		try {
			BoardDetailDto detail = boardService.getDetail(id);
			model.addAttribute("boardDetail", detail);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "게시물이 존재하지 않습니다.");
			return "redirect:/";
		}

		return "/board/boardDetail";
	}

	@GetMapping(value = "/update/{id}")
	public String getBoardUpdateForm(@PathVariable Long id, Principal principal, Model model) {
		try {
			BoardUpdateDto boardUpdateDto = boardService.getUpdateForm(id, principal.getName());
			model.addAttribute("boardUpdateDto", boardUpdateDto);
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "redirect:/board/{id}";
		}
		return "/board/boardUpdateForm";
	}

	@PostMapping(value = "/update/{id}")
	public String updateBoard(@Valid BoardUpdateDto boardUpdateDto, @PathVariable Long id, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "/board/boardUpdateForm";
		}
		boardService.updateBoard(boardUpdateDto, id);
		return "redirect:/board/list";
	}

	@DeleteMapping(value = "/delete/{id}")
	public String deleteBoard(@PathVariable Long id, Principal principal, Model model) throws Exception {
		try {
			boardService.deleteBoard(id, principal.getName());
		} catch (Exception e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "redirect:/";
		}
		return "redirect:/board/list";
	}

	@PostMapping(value = "/like")
	public @ResponseBody ResponseEntity likeBoard(@RequestBody BoardLikeDto boardLikeDto, Principal principal) {
		if (principal == null)
			return new ResponseEntity<>("Not Login", HttpStatus.UNAUTHORIZED);
		try {
			boardLikeService.addBoardLike(boardLikeDto, principal.getName());
		} catch (Exception e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}

		return new ResponseEntity<>(boardLikeDto, HttpStatus.OK);
	}

	@GetMapping(value = "/like/{id}")
	public @ResponseBody ResponseEntity getLike(@PathVariable Long id) {
		try {
			int like = boardLikeService.getBoardLike(id);
			return new ResponseEntity<>(like, HttpStatus.OK);
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}
}
