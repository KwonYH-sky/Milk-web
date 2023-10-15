package com.milk.milkweb.controller;

import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.dto.CommentListDto;
import com.milk.milkweb.dto.CommentUpdateDto;
import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/comment")
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/write")
	public @ResponseBody ResponseEntity saveComment(@RequestBody @Valid CommentFormDto commentFormDto, @AuthenticationPrincipal CustomUserDetails principal) {
		try {
			commentService.saveComment(commentFormDto, principal.getName());
		} catch (Exception e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/board/{boardId}")
	public @ResponseBody ResponseEntity getCommentPage(@PathVariable Long boardId, @RequestParam(value = "page", defaultValue = "0") int page, @AuthenticationPrincipal CustomUserDetails principal) {
		String email = principal != null ? principal.getName() : "Not Login";
		try {
			Page<CommentListDto> dtoPage = commentService.getCommentList(page, boardId, email);
			return new ResponseEntity<>(dtoPage, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@PatchMapping("/update")
	public @ResponseBody ResponseEntity updateComment(@RequestBody @Valid CommentUpdateDto commentUpdateDto, @AuthenticationPrincipal CustomUserDetails principal) {
		try {
			commentService.updateComment(commentUpdateDto, principal.getName());
			return new ResponseEntity<>(commentUpdateDto, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@DeleteMapping("/delete/{commentId}")
	public @ResponseBody ResponseEntity deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal CustomUserDetails principal){
		try {
			commentService.deleteComment(commentId, principal.getName());
			return new ResponseEntity<>("댓글 삭제 성공", HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}
}
