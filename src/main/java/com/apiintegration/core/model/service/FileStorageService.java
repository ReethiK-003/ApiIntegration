package com.apiintegration.core.model.service;

import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import org.springframework.core.io.UrlResource;

import lombok.Getter;

@Service
public class FileStorageService {

	@Getter
	private Path fileStorageLocation;

	@Value("${file.upload.path}")
	private String uploadPath;

	@Value("${file.download.path}")
	private String downloadPath;

	@PostConstruct
	public void setStorageLocation() {
		this.fileStorageLocation = Paths.get(uploadPath).toAbsolutePath().normalize();

		try {
			Files.createDirectories(this.fileStorageLocation);
		} catch (Exception ex) {
			throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
		}
	}

//	public String storeFile(MultipartFile file, String targetFileName) {
//		// Normalize file name
//		String fileName = StringUtils.cleanPath(targetFileName);
//
//		try {
//			// Check if the file's name contains invalid characters
//			if (fileName.contains("..")) {
//				throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
//			}
//
//			// Copy file to the target location (Replacing existing file with the same name)
//			Path targetLocation = this.fileStorageLocation.resolve(fileName);
//			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//			return downloadDir + fileName;
//		} catch (IOException ex) {
//			throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
//		}
//	}
//
//	public String storeFile(byte[] file, String targetFileName) {
//		// Normalize file name
//		String fileName = StringUtils.cleanPath(targetFileName);
//
//		try {
//			// Check if the file's name contains invalid characters
//			if (fileName.contains("..")) {
//				throw new RuntimeException("Sorry! Filename contains invalid path sequence " + fileName);
//			}
//
//			// Copy file to the target location (Replacing existing file with the same name)
//			Path targetLocation = this.fileStorageLocation.resolve(fileName);
//			Files.copy(new ByteArrayInputStream(file), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//			return downloadDir + fileName;
//		} catch (IOException ex) {
//			throw new RuntimeException("Could not store file " + fileName + ". Please try again!", ex);
//		}
//	}

	public Resource loadFileAsResource(String fileName) {
		try {
			Path filePath = this.fileStorageLocation.resolve(FilenameUtils.getName(fileName)).normalize();
			Resource resource = new UrlResource(filePath.toUri());
			if (resource.exists()) {
				return resource;
			} else {
				throw new RuntimeException("File not found " + fileName);
			}
		} catch (MalformedURLException ex) {
			throw new RuntimeException("File not found " + fileName, ex);
		}
	}

//	public String getMimeTypeOfFile(String fileName) {
//		try {
//			Path path = getFullFilePath(fileName);
//			return Files.probeContentType(path);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	public Path getFullFilePath(String fileName) {
//		return this.fileStorageLocation.resolve(FilenameUtils.getName(fileName)).normalize();
//	}
//
//	public void deleteFile(String fileName) throws IOException {
//		Path filePath = this.fileStorageLocation.resolve(FilenameUtils.getName(fileName)).normalize();
//
//		Files.delete(filePath);
//	}
//
//	public String downloadAndStoreFile(String remoteUrl, String targetFileName) throws IOException {
//		URL url = new URL(remoteUrl);
//
//		// Copy file to the target location (Replacing existing file with the same name)
//		Path targetLocation = this.fileStorageLocation.resolve(StringUtils.cleanPath(targetFileName));
//		Files.copy(url.openStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
//
//		return downloadDir + targetFileName;
//	}
}
