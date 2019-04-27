package com.colomertrave.springboot.datajpa.app.models.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileServiceImpl implements IUploadFileService {

	private static final String UPLOADS_FOLDER = "uploads";

	private final Logger log = LoggerFactory.getLogger(getClass());

	@Override
	public Resource load(String filename) throws MalformedURLException {
		Path pathPhoto = getPath(filename);
		log.info("************ " + pathPhoto);

		Resource recurso = new UrlResource(pathPhoto.toUri());

		if (!recurso.exists() || !recurso.isReadable()) {
			throw new RuntimeException("User's photo can not be downloaded!");
		}

		return recurso;
	}

	@Override
	public String copy(MultipartFile filename) throws IOException {
		String uniqueFilename = UUID.randomUUID().toString() + "_" + filename.getOriginalFilename();
		Path rootPath = getPath(uniqueFilename);
		log.info("***************** rootPath: " + rootPath);
		
		Files.copy(filename.getInputStream(), rootPath);
		
		return uniqueFilename;
	}

	@Override
	public boolean delete(String filename) {
		Path rootPath = getPath(filename);
		File archivo = rootPath.toFile();
		log.info("***************** BORRANDO... " + archivo.getAbsolutePath());
		if (archivo.exists() && archivo.canRead()) {
			if (archivo.delete()) {
				return true;
			}
		}
		
		return false;
	}
	
	public Path getPath(String filename) {
		return Paths.get(UPLOADS_FOLDER).resolve(filename).toAbsolutePath();
	}

}
