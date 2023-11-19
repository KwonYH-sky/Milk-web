package com.milk.milkweb.controller;

import com.milk.milkweb.dto.MainBoardDto;
import com.milk.milkweb.service.BoardService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MainController.class)
class MainControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private BoardService boardService;

	@Test
	@WithMockUser
	@DisplayName("메인 화면 게시물 GET 테스트")
	void getMainTest() throws Exception {
		// given
		List<MainBoardDto> mainBoardList = new ArrayList<>();
		List<MainBoardDto> dailyBestList = new ArrayList<>();
		List<MainBoardDto> weeklyBestList = new ArrayList<>();

		given(boardService.getMainBoard()).willReturn(mainBoardList);
		given(boardService.getDailyBest()).willReturn(dailyBestList);
		given(boardService.getWeeklyBest()).willReturn(weeklyBestList);

		// when, then
		mockMvc.perform(get("/").with(csrf()))
				.andExpect(status().isOk())
				.andExpect(model().attribute("boards", mainBoardList))
				.andExpect(model().attribute("dailyBest", dailyBestList))
				.andExpect(model().attribute("weeklyBest", weeklyBestList));
	}
}