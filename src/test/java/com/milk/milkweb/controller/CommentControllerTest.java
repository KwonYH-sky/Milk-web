package com.milk.milkweb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.milk.milkweb.config.OAuth2Config;
import com.milk.milkweb.config.SecurityConfig;
import com.milk.milkweb.constant.Role;
import com.milk.milkweb.dto.CommentFormDto;
import com.milk.milkweb.dto.CustomUserDetails;
import com.milk.milkweb.entity.Member;
import com.milk.milkweb.service.CommentService;
import com.milk.milkweb.service.CustomOAuth2UserService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SecurityConfig.class, OAuth2Config.class})
@WebMvcTest(CommentController.class)
class CommentControllerTest {

	private CustomUserDetails mockUser;

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private CustomOAuth2UserService customOAuth2UserService;
	@MockBean
	private CommentService commentService;


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
	@DisplayName("Comment 저장 POST 테스트")
	void saveCommentTest() throws Exception {
		// given
		CommentFormDto commentFormDto = CommentFormDto.builder()
				.boardId(1L)
				.comment("Test")
				.build();

		// when, then
		mockMvc.perform(post("/comment/write").with(csrf()).with(user(mockUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentFormDto)))
				.andExpect(status().isOk());

		verify(commentService, times(1)).saveComment(any(CommentFormDto.class), anyString());
	}

	@Test
	@DisplayName("Comment 저장 POST 실패 테스트")
	void saveCommentFailTest() throws Exception {
		// given
		CommentFormDto commentFormDto = CommentFormDto.builder()
				.boardId(1L)
				.comment("Test")
				.build();

		doThrow(new EntityNotFoundException("Error")).when(commentService).saveComment(any(CommentFormDto.class), anyString());

		// when, then
		mockMvc.perform(post("/comment/write").with(csrf()).with(user(mockUser))
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(commentFormDto)))
				.andExpect(status().isBadRequest());

		verify(commentService, times(1)).saveComment(any(CommentFormDto.class), anyString());
	}
}