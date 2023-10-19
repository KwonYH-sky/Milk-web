package com.milk.milkweb.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class FileService {

	public String makeDir(String uploadLocation) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String datePath = format.format(new Date()).replace("-", File.separator);

		File dir = new File(uploadLocation, datePath);
		if (!dir.exists())
			dir.mkdirs();

		log.info("FileService.makeDir() - dir.getName(): " + dir.getName());
		return datePath;
	}

	public String uploadFile(String uploadLocation, MultipartFile multipartFile) throws IOException {

		UUID uuid = UUID.randomUUID();
		String extension = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf('.'));
		String savedFileName = uuid + extension;

		multipartFile.transferTo(new File(uploadLocation, savedFileName));

		return savedFileName;
	}

	public Resource downloadFile(String downloadLocation, String path, String fileName) {
		Resource resource = new FileSystemResource(downloadLocation + "\\" + path + "\\" + fileName);
		log.debug("FIleService downloadFile() resource " + resource.toString());
		if (!resource.exists()){
			log.error("FileService downloadFile() resource is not exists.");
			throw new RuntimeException();
		}
		return resource;
	}

}
