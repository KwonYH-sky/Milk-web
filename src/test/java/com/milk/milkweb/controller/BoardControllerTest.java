package com.milk.milkweb.controller;

import com.milk.milkweb.config.OAuth2Config;
import com.milk.milkweb.config.SecurityConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.BoardFormDto;
import com.milk.milkweb.dto.BoardListDto;
import com.milk.milkweb.dto.BoardSearchDto;
import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.BoardImgService;
import com.milk.milkweb.service.BoardLikeService;
import com.milk.milkweb.service.BoardService;
import com.milk.milkweb.service.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfig.class, OAuth2Config.class})
@WebMvcTest(BoardController.class)
public class BoardControllerTest {

	private CustomUserDetails mockUser;

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CustomOAuth2UserService customOAuth2UserService;
	@MockBean
	private BoardService boardService;
	@MockBean
	private BoardLikeService boardLikeService;
	@MockBean
	private BoardImgService boardImgService;


	@BeforeEach
	void setUp() {
		mockUser = new CustomUserDetails(Member.builder()
				.id(1L)
				.name("김우유")
				.email("test@test")
				.password("1234")
				.role(Role.ADMIN)
				.build());
	}

	@Test
	@WithMockUser
	@DisplayName("Board 쓰기 GET 인증 성공 테스트")
	void entryWriteBoardFormTest() throws Exception {
		// when, then
		mockMvc.perform(get("/board/write"))
				.andExpect(view().name("board/boardForm"))
				.andExpect(model().attributeExists("boardFormDto"))
				.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Board 쓰기 POST 인증 성공 테스트")
	void writeBoardTest() throws Exception {
		// given
		BoardFormDto boardFormDto = new BoardFormDto();
		boardFormDto.setTitle("테스트");
		boardFormDto.setContent("테스트 내용");

		// when, then
		mockMvc.perform(post("/board/write")
						.with(csrf()).with(user(mockUser))
						.param("title", boardFormDto.getTitle())
						.param("content", boardFormDto.getContent()))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("Board 목록 GET 요청 - 검색X")
	void getBoardListTest() throws Exception {
		// given
		given(boardService.getBoardList(0)).willReturn(new PageImpl<>(new ArrayList<BoardListDto>()));

		// when, then
		mockMvc.perform(get("/board/list")
						.queryParam("page", String.valueOf(0)))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("paging"))
				.andExpect(model().attributeExists("boardSearchDto"));
		verify(boardService, times(1)).getBoardList(anyInt());
	}

	@Test
	@DisplayName("Board 목록 GET 요청 - 검색O")
	void getBoardListSearchTest() throws Exception {
		// given
		BoardSearchDto boardSearchDto = new BoardSearchDto();
		boardSearchDto.setSearchType("제목");
		boardSearchDto.setKeyword("테스트");

		given(boardService.getSearchBoardList(any(BoardSearchDto.class) ,anyInt())).willReturn(new PageImpl<>(new ArrayList<BoardListDto>()));

		// when, then
		mockMvc.perform(get("/board/list")
						.queryParam("page", String.valueOf(0))
						.queryParam("searchType", boardSearchDto.getSearchType())
						.queryParam("keyword", boardSearchDto.getKeyword()))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("paging"))
				.andExpect(model().attributeExists("boardSearchDto"));
		verify(boardService, times(1)).getSearchBoardList(any(BoardSearchDto.class), anyInt());
	}
}
