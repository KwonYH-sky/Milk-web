package com.milk.milkweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milk.milkweb.config.OAuth2Config;
import com.milk.milkweb.config.SecurityConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.*;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.exception.AlreadyLikeException;
import com.milk.milkweb.exception.MemberValidationException;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import({SecurityConfig.class, OAuth2Config.class})
@WebMvcTest(BoardController.class)
public class BoardControllerTest {

	private CustomUserDetails mockUser;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

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

	@Test
	@DisplayName("Board 상세보기 GET 테스트")
	void boardDetailTest() throws Exception {
		// given
		BoardDetailDto boardDetailDto = BoardDetailDto.builder()
				.title("test")
				.content("테스트")
				.build();

		given(boardService.getDetail(1L, mockUser.getMember())).willReturn(boardDetailDto);

		// when, then
		mockMvc.perform(get("/board/{id}", 1L).with(user(mockUser)))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("boardDetail"))
				.andExpect(view().name("board/boardDetail"));
	}

	@Test
	@DisplayName("Board UpdateForm GET 테스트")
	void getBoardUpdateFormTest() throws Exception {
		// given
		BoardUpdateDto boardUpdateDto = BoardUpdateDto.builder()
				.title("테스트")
				.content("테스트")
				.build();

		given(boardService.getUpdateForm(1L, mockUser.getName())).willReturn(boardUpdateDto);

		// when, then
		mockMvc.perform(get("/board/update/{id}", 1L).with(user(mockUser)))
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("boardUpdateDto"))
				.andExpect(view().name("board/boardUpdateForm"));

	}

	@Test
	@DisplayName("Board UpdateForm GET 인증 실패 테스트")
	void getBoardUpdateFormFailTest() throws Exception {
		// given
		given(boardService.getUpdateForm(1L, mockUser.getName())).willThrow(MemberValidationException.class);

		// when, then
		mockMvc.perform(get("/board/update/{id}", 1L).with(user(mockUser)))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("Board Update Post 요청 테스트")
	void updateBoardTest() throws Exception {
		// given
		BoardUpdateDto mockUpdateDto = BoardUpdateDto.builder()
				.id(1L)
				.title("테스트")
				.content("테스트")
				.build();

		mockMvc.perform(post("/board/update/{id}", 1L).with(csrf()).with(user(mockUser))
						.param("id", mockUpdateDto.getId().toString())
						.param("title", mockUpdateDto.getTitle())
						.param("content", mockUpdateDto.getContent()))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	@DisplayName("Board Delete 요청 테스트")
	void deleteBoardTest() throws Exception {
		// given
		doNothing().when(boardService).deleteBoard(1L, mockUser.getName());

		// when, then
		mockMvc.perform(delete("/board/delete/{id}", 1L).with(csrf()).with(user(mockUser)))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/board/list"));
	}

	@Test
	@DisplayName("Board Delete 요청 실패 테스트")
	void deleteBoardFailTest() throws Exception {
		// given
		doThrow(new MemberValidationException("Test")).when(boardService).deleteBoard(1L, mockUser.getName());

		// when, then
		mockMvc.perform(delete("/board/delete/{id}", 1L).with(csrf()).with(user(mockUser)))
				.andExpect(flash().attributeExists("errorMessage"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/board/" + 1L));
	}

	@Test
	@DisplayName("Board Like POST 테스트")
	void likeBoardTest() throws Exception {
		// given
		BoardLikeDto boardLikeDto = BoardLikeDto.builder()
				.boardId(1L)
				.build();

		// when, then
		mockMvc.perform(post("/board/like").with(csrf()).with(user(mockUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(boardLikeDto)))
				.andExpect(status().isOk());

		verify(boardLikeService, times(1)).addBoardLike(any(BoardLikeDto.class), anyString());
	}

	@Test
	@DisplayName("Board Like POST 미인증 테스트")
	@WithAnonymousUser
	void likeBoardUnauthorizedTest() throws Exception {
		// given
		BoardLikeDto boardLikeDto = BoardLikeDto.builder()
				.boardId(1L)
				.build();

		// when, then
		mockMvc.perform(post("/board/like").with(csrf())
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(boardLikeDto)))
				.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("Board Like POST Bad Request 테스트")
	@WithAnonymousUser
	void likeBoardBadRequestTest() throws Exception {
		// given
		BoardLikeDto boardLikeDto = BoardLikeDto.builder()
				.boardId(1L)
				.build();

		doThrow(new AlreadyLikeException("Test")).when(boardLikeService).addBoardLike(any(BoardLikeDto.class), anyString());

		// when, then
		mockMvc.perform(post("/board/like").with(csrf()).with(user(mockUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(boardLikeDto)))
				.andExpect(status().isBadRequest());
	}
}
