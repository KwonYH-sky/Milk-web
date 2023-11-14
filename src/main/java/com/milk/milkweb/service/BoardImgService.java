package com.milk.milkweb.service;

import com.milk.milkweb.dto.BoardImgDownloadDto;
import com.milk.milkweb.dto.BoardImgUploadDto;
import com.milk.milkweb.entity.BoardImg;
import com.milk.milkweb.repository.BoardImgRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class BoardImgService {

	@Value("${boardImgLocation}")
	private String boardImgLocation;

	private final BoardImgRepository boardImgRepository;
	private final FileService fileService;


	public BoardImgUploadDto uploadImg(MultipartFile uploadImg) throws IOException {
		log.info("BoardImgService.uploadImg() run");
		String path = fileService.makeDir(boardImgLocation);
		String imgName = fileService.uploadFile(boardImgLocation + File.separator + path, uploadImg);

		BoardImg boardImg = BoardImg.builder()
				.imgName(imgName)
				.imgOriName(uploadImg.getOriginalFilename())
				.path(path)
				.createdTime(LocalDateTime.now())
				.build();

		boardImgRepository.save(boardImg);

		return BoardImgUploadDto.builder()
				.boardImgID(boardImg.getId())
				.imgName(boardImg.getImgName())
				.build();
	}

	public BoardImgDownloadDto downloadImg(String imgName) throws IOException {
		BoardImg boardImg = boardImgRepository.findByImgName(imgName).orElseThrow(EntityNotFoundException::new);
		log.debug("BoardImgService downloadImg() boardImg : " + boardImg.toString());
		Resource resource = fileService.downloadFile(boardImgLocation, boardImg.getPath(), boardImg.getImgName());
		log.debug("BoardImgService downloadImg() resource : " + resource.toString());
		HttpHeaders header = new HttpHeaders();

		Path path = Paths.get(boardImgLocation,boardImg.getPath(), boardImg.getImgName());
		log.debug("BoardImgService downloadImg() path : " + path);
		header.add("Content-type", Files.probeContentType(path));
		log.debug("BoardImgService downloadImg() header.add");

		return BoardImgDownloadDto.builder()
				.resource(resource)
				.header(header)
				.build();
	}
}
