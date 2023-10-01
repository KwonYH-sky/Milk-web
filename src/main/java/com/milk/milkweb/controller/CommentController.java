package com.milk.milkweb.controller;

import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.dto.CommentListDto;
import com.milk.milkweb.service.CommentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequestMapping(value = "/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/write")
	public @ResponseBody ResponseEntity saveComment(@RequestBody CommentFormDto commentFormDto, Principal principal) {
		try {
			commentService.saveComment(commentFormDto, principal.getName());
		} catch (EntityNotFoundException e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/board/{boardId}")
	public @ResponseBody ResponseEntity getCommentPage(@PathVariable Long boardId, @RequestParam("page") int page) {
		try {
			Page<CommentListDto> dtoPage = commentService.getCommentList(page, boardId);
			return new ResponseEntity<>(dtoPage, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e, HttpStatus.BAD_REQUEST);
		}
	}
}
