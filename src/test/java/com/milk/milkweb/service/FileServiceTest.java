package com.milk.milkweb.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {

	private static final String uploadLocation = "testUploadLocation";

	@Mock
	private MultipartFile multipartFile;

	@InjectMocks
	private FileService fileService;

	@AfterAll
	static void afterAll() {
		deleteFile(uploadLocation);
	}

	@Test
	@DisplayName("폴더 생성 테스트")
	void makeDirTest() {
		// given
		String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", File.separator);

		// when
		String result = fileService.makeDir(uploadLocation);

		// then
		assertThat(new File(uploadLocation, datePath).exists()).as("존재 여부").isTrue();
		assertThat(result).isEqualTo(datePath);
	}

	/**
	 * 테스트용 파일 생성후 삭제하는 메소드
	 * 파일이면 삭제 폴더면 재귀 DFS
	 * @param path 파일경로
	 */
	private static void deleteFile(String path) {
		File deleteFolder = new File(path);

		if (deleteFolder.exists()) {
			File[] deleteFolderList = deleteFolder.listFiles();

			for (File file : deleteFolderList) {

				if (file.isFile())
					file.delete();
				else
					deleteFile(file.getPath());

				file.delete();
			}
			deleteFolder.delete();
		}
	}
}