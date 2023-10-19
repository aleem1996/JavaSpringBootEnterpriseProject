package com.gr.censusmanagement.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.gr.censusmanagement.constant.ErrorCodes;
import com.gr.common.v2.exception.model.ApiErrorDto;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileUtil {
	
	private static final List<String> allowedFileExtensions = Arrays.asList("xlsx");
	
	public static void writeFile(byte[] content, String filename, UUID sessionId, String batchId, String path) throws IOException{

		String folderPath = path + (Util.isNullOrEmpty(sessionId.toString()) ? "" : sessionId);
		new File(folderPath).mkdirs();

		File file = new File(folderPath + File.separator + filename);
		deleteOldFilesInSession(folderPath, batchId);

		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream fop = new FileOutputStream(file);

		fop.write(content);
		fop.flush();
		fop.close();

	}
	
	public static void deleteOldFilesInSession(String folderPath, String batchId) throws IOException {
		List<File> files = Files.walk(Paths.get(folderPath)).filter(Files::isRegularFile).map(java.nio.file.Path::toFile).collect(Collectors.toList());
		for (File file : files) {
			if (!file.getName().contains(batchId)) {
				file.delete();
			}
		}
	}
	
	public static void validateFiles(String folderPath, List<ApiErrorDto> apiErrors, List<String> fileHeadersSorted) {
		validateFileExtension(folderPath, allowedFileExtensions, apiErrors);
		if (apiErrors.isEmpty()) {
			validateFileHeaders(folderPath, fileHeadersSorted, apiErrors);
		}
	}
	
	public static void validateFileExtension(String folderPath, List<String> allowedFileExtensions, List<ApiErrorDto> apiErrors) {
		try {
			String filePath = CensusExcelUtil.getFilesFromPath(folderPath).get(0).getAbsolutePath();
			File file = new File(filePath);
			if (!allowedFileExtensions.contains(getFileExtension(file.getName()))) {
				apiErrors.add(ApiErrorDto.of(ErrorCodes.BR_CAS_202, "File Extension", "File extension is invalid."));
			}

		} catch (IOException e) {
			apiErrors.add(ApiErrorDto.of(ErrorCodes.BR_CAS_202, "File Extension", "File is corrupted and cannot be uploaded."));
			log.error(e.getMessage());
		}

	}
	
	public static void validateFileHeaders(String folderPath, List<String> fileHeadersSorted, List<ApiErrorDto> apiErrors) {

		try {
			String filePath = CensusExcelUtil.getFilesFromPath(folderPath).get(0).getAbsolutePath();
			File file = new File(filePath);
			List<String> uploadedFileHeaders = CensusExcelUtil.getHeaders(0, file.getAbsolutePath());
			if (Util.isNotNullAndEmpty(uploadedFileHeaders)) {
				Collections.sort(uploadedFileHeaders);
			}
			if (Util.isNullOrEmpty(uploadedFileHeaders) || !uploadedFileHeaders.containsAll(fileHeadersSorted)) {
				apiErrors.add(ApiErrorDto.of(ErrorCodes.BR_CAS_202, "File Headers", "File cannot be uploaded as the header info does not match with the template."));
			}
		} catch (Exception e) {
			apiErrors.add(ApiErrorDto.of(ErrorCodes.BR_CAS_202, "File Headers", "Bulk upload feature does not support Strict Open XML. Please provide a regular Excel file."));
			log.error(e.getMessage());
		}
	}
	
    public static String getFileExtension(String filename) {
        String extension = null;
        if (!(Util.isNullOrEmpty(filename))) {
            int extDot = filename.lastIndexOf('.');
            if (extDot > 0) {
                extension = filename.substring(extDot + 1);
            }
        }
        
        return extension;
    }
	
	public static void deleteOldExportFiles(String folderPath) throws IOException {
		List<File> files = Files.walk(Paths.get(folderPath)).filter(Files::isRegularFile).map(java.nio.file.Path::toFile).collect(Collectors.toList());
		for (File file : files) {
			BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
			long creationTime = attrs.creationTime().toMillis();
			long currentTime = System.currentTimeMillis();
			long ageInMillis = currentTime - creationTime;
			long maxAgeInMillis = 60 * 60 * 1000; // 1 hour

			if (ageInMillis >= maxAgeInMillis) {
				file.delete();
			}
		}
	}

}
