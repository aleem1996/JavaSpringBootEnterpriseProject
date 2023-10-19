package com.gr.censusmanagement.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;




public class CensusExcelUtil {
	
	public static List<File> getFilesFromPath(String folderPath) throws IOException {
	    return Files.walk(Paths.get(folderPath))
	                .filter(Files::isRegularFile)
	                .filter(path -> path.toString().endsWith(".xlsx")) // filter by file extension
	                .map(java.nio.file.Path::toFile)
	                .collect(Collectors.toList());
	}

	public static List<String> getHeaders(int sheetNumber, String filePath) throws IOException{
		List<String> headers = new ArrayList<String>();
		InputStream inputStream = new FileInputStream(new File(filePath));
		Workbook workbook = WorkbookFactory.create(inputStream);
		Sheet sheet = workbook.getSheetAt(sheetNumber);
		Row row = sheet.getRow(0);
		if (!isRowEmpty(row)) {
			for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
				Cell cell = row.getCell(cellNum);
				headers.add(Util.isNull(cell) ? "" : cell.toString().trim());
			}
		}
		if (Util.isNotNull(inputStream)) {
			inputStream.close();
		}
		return headers;
	}
	
	public static boolean isRowEmpty(Row row) {
		if (Util.isNull(row)) {
			return true;
		}
		if (row.getLastCellNum() <= 0) {
			return true;
		}
		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
			Cell cell = row.getCell(cellNum);
			if (Util.isNotNull(cell) && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
				return false;
			}
		}
		return true;
	}

}
