package com.milk.milkweb.controller;

import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.BoardImgService;
import com.milk.milkweb.service.BoardLikeService;
import com.milk.milkweb.service.BoardService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;


@RequestMapping(value = "/board")
@Controller
@RequiredArgsConstructor
@Slf4j
public class BoardController {

	private final BoardService boardService;
	private final BoardLikeService boardLikeService;
	private final BoardImgService boardImgService;

	@GetMapping(value = "/write")
	public String entryWriteBoardForm(Model model, @AuthenticationPrincipal CustomUserDetails principal) {
		log.debug("principal = " + principal);
		model.addAttribute("boardFormDto", new BoardFormDto());
		return "board/boardForm";
	}

	@PostMapping(value = "/write")
	public String writeBoard(@Valid BoardFormDto boardFormDto, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails principal, Model model) {
		log.debug("principal = "+ principal);
		if (bindingResult.hasErrors())
			return "board/boardForm";

		try {
			boardService.saveBoard(boardFormDto, principal.getName());
		} catch (Exception e) {
			model.addAttribute("errorMessage", "게시물 등록 중 에러가 발생하였습니다.");
			return "board/boardForm";
		}

		return "redirect:/board/list";
	}

	@GetMapping(value = {"", "/list"})
	public String getBoardList(Model model, @RequestParam(value = "page", defaultValue = "0") int page, BoardSearchDto boardSearchDto) {
		try {
			Page<BoardListDto> boardListDtos = boardSearchDto.isSearching() ? 
					boardService.getSearchBoardList(boardSearchDto, page) : boardService.getBoardList(page);

			model.addAttribute("paging", boardListDtos);
			model.addAttribute("boardSearchDto", boardSearchDto); // 현재 검색 파라미터를 모델에 추가
		} catch (Exception e) {
			model.addAttribute("paging", boardService.getBoardList(0));
		}
		return "board/boardList";
	}

	@GetMapping(value = "/{id}")
	public String boardDetail(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails principal) {
		Member member = Optional.ofNullable(principal).map(CustomUserDetails::getMember).orElse(Member.builder().role(Role.USER).build());
		try {
			BoardDetailDto detail = boardService.getDetail(id, member);
			model.addAttribute("boardDetail", detail);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "게시물이 존재하지 않습니다.");
			return "redirect:/";
		}

		return "board/boardDetail";
	}

	@GetMapping(value = "/update/{id}")
	public String getBoardUpdateForm(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal, Model model) {
		try {
			BoardUpdateDto boardUpdateDto = boardService.getUpdateForm(id, principal.getName());
			model.addAttribute("boardUpdateDto", boardUpdateDto);
		} catch (Exception e) {
			model.addAttribute("errorMessage", "권한이 없습니다.");
			return "redirect:/board/{id}";
		}
		return "board/boardUpdateForm";
	}

	@PostMapping(value = "/update/{id}")
	public String updateBoard(@Valid BoardUpdateDto boardUpdateDto, @PathVariable Long id,
	                          BindingResult bindingResult, Model model,
	                          @AuthenticationPrincipal CustomUserDetails userDetails) {
		if (bindingResult.hasErrors())
			return "board/boardUpdateForm";

		try {
			boardService.validateMemberBoard(id, userDetails.getName());
			boardService.updateBoard(boardUpdateDto, id);
		} catch (Exception e) {
			log.error("BoardController updateBoard() error : " + e.getMessage());
			model.addAttribute("errorMessage", "게시물 수정중 오류 발생하였습니다.");
		}

		return "redirect:/board/" + id;
	}

	@DeleteMapping(value = "/delete/{id}")
	public String deleteBoard(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails principal, Model model) throws Exception {
		try {
			boardService.deleteBoard(id, principal.getName());
		} catch (Exception e) {
			model.addAttribute("errorMessage", "권한이 없습니다.");
			return "redirect:/board/" + id;
		}
		return "redirect:/board/list";
	}

	@PostMapping(value = "/like")
	public @ResponseBody ResponseEntity likeBoard(@RequestBody BoardLikeDto boardLikeDto, @AuthenticationPrincipal CustomUserDetails principal) {
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

	@PostMapping(value = "/uploadImg")
	public @ResponseBody ResponseEntity postImg(@RequestPart(value = "img") MultipartFile multipartFile) {
		try {
		 	BoardImgUploadDto boardImg = boardImgService.uploadImg(multipartFile);
			return new ResponseEntity<>(boardImg, HttpStatus.OK);
		} catch (Exception e) {
			log.error("BoardController postImg() Exception : " + e.getMessage());
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
	}

	@GetMapping(value = "/images")
	public @ResponseBody ResponseEntity getImg(@RequestParam(value = "imgName") String imgName) {
		log.debug("BoardController getImg() : imgName : " + imgName);
		try {
			BoardImgDownloadDto dto = boardImgService.downloadImg(imgName);
			log.debug("BoardController getImg() dto : " + dto.toString());
			return new ResponseEntity<>(dto.getResource(), dto.getHeader(), HttpStatus.OK);
		} catch (Exception e) {
			log.error("BoardController getImg() Exception : " + e.getMessage());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
}
