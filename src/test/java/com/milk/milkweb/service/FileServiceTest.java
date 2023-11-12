package com.milk.milkweb.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@ExtendWith(MockitoExtension.class)
class FileServiceTest {

	@TempDir
	private Path tempDir;

	@InjectMocks
	private FileService fileService;

	@Test
	@DisplayName("폴더 생성 테스트")
	void makeDirTest() {
		// given
		String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", File.separator);

		// when
		String result = fileService.makeDir(tempDir.toString());

		// then
		assertThat(new File(tempDir.toString(), datePath).exists()).as("존재 여부").isTrue();
		assertThat(result).isEqualTo(datePath);
	}

	@Test
	@DisplayName("업로드 테스트")
	void uploadFileTest() throws IOException {
		// given
		String originalFilename = "testFile.txt";

		byte[] fileContent = "Test file content".getBytes();
		MockMultipartFile mockMultipartFile = new MockMultipartFile(
				"file",
				originalFilename,
				"text/plain",
				fileContent);

		// When
		String savedFileName = fileService.uploadFile(tempDir.toString(), mockMultipartFile);

		// Then
		assertThat(new File(tempDir.toString(), savedFileName).exists()).isTrue();
	}

	@Test
	@DisplayName("다운로드 테스트")
	void downloadFileTest() throws IOException {
		// given
		String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", File.separator);
		String fileName = "testFile.txt";

		Path testFilePath = Paths.get(tempDir.toString(), datePath, fileName);
		Files.createDirectories(testFilePath.getParent());
		Files.createFile(testFilePath);

		// when
		Resource resource = fileService.downloadFile(tempDir.toString(), datePath, fileName);

		// then
		assertThat(resource).as("존재 여부").isNotNull();
		assertThat(resource.exists()).isTrue();
		assertThat(resource).isInstanceOf(FileSystemResource.class);
	}

	@Test
	@DisplayName("다운로드 실패 테스트")
	void downloadFileFailTest() {
		// given
		String datePath = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).replace("-", File.separator);
		String fileName = "noExist.txt";

		// when, then
		assertThatThrownBy(() -> fileService.downloadFile(tempDir.toString(), datePath, fileName)).as("Exception 던져요?")
				.isInstanceOf(RuntimeException.class);
	}
}