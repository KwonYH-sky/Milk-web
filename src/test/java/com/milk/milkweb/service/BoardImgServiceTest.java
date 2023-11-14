package com.milk.milkweb.service;

import com.milk.milkweb.dto.BoardImgDownloadDto;
import com.milk.milkweb.dto.BoardImgUploadDto;
import com.milk.milkweb.entity.BoardImg;
import com.milk.milkweb.repository.BoardImgRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class BoardImgServiceTest {

	@Mock
	private FileService fileService;
	@Mock
	private BoardImgRepository boardImgRepository;

	@InjectMocks
	private BoardImgService boardImgService;

	private final String boardImgLocation = "TestLocation";

	@BeforeEach
	void setUp() {
		ReflectionTestUtils.setField(boardImgService, "boardImgLocation", boardImgLocation);
	}

	@Test
	@DisplayName("이미지 업로드 테스트")
	void uploadImgTest() throws IOException {
		// given
		MultipartFile mockFile = new MockMultipartFile("testImg", "testImg.jpg", "image/jpeg", "fileImage".getBytes());

		given(fileService.makeDir(boardImgLocation)).willReturn("testDir");
		given(fileService.uploadFile(anyString(), any(MultipartFile.class))).willReturn(mockFile.getName());

		given(boardImgRepository.save(any())).willReturn(BoardImg.builder().build());

		// when
		BoardImgUploadDto result = boardImgService.uploadImg(mockFile);

		// then
		assertThat(result).as("존재 여부").isNotNull();
		assertThat(result.getImgName()).isEqualTo(mockFile.getName());
	}

	@Test
	@DisplayName("이미지 다운로드 테스트")
	void downloadImgTest() throws IOException {
		// given
		String mockImgName = "testImg";
		BoardImg boardImg = BoardImg.builder().imgName(mockImgName).path("testPath").build();
		given(boardImgRepository.findByImgName(anyString())).willReturn(Optional.ofNullable(boardImg));

		Resource mockResource = mock(Resource.class);
		given(fileService.downloadFile(eq(boardImgLocation), anyString(), anyString())).willReturn(mockResource);

		// when
		BoardImgDownloadDto result = boardImgService.downloadImg(mockImgName);

		// then
		assertThat(result).as("존재 여부").isNotNull();
		assertThat(result.getResource()).isEqualTo(mockResource);
	}
}