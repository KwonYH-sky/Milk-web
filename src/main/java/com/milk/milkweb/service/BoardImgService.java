package com.milk.milkweb.service;

import com.milk.milkweb.entity.BoardImg;
import com.milk.milkweb.repository.BoardImgRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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


	public BoardImg uploadImg(MultipartFile uploadImg) throws IOException {
		log.info("BoardImgService.uploadImg() run");
		String path = fileService.makeDir(boardImgLocation);
		String imgName = fileService.uploadFile(boardImgLocation +"/"+ path, uploadImg);

		BoardImg boardImg = BoardImg.builder()
				.imgName(imgName)
				.imgOriName(uploadImg.getOriginalFilename())
				.imgUrl("/board/images/" + imgName)
				.path(path)
				.createdTime(LocalDateTime.now())
				.build();

		return boardImgRepository.save(boardImg);
	}
}
