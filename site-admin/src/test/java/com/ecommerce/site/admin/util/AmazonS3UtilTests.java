package com.ecommerce.site.admin.util;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;


public class AmazonS3UtilTests {

	@Test
	public void testListFolder() {
		String folderName = "product-images/10/";
		List<String> listKeys = AmazonS3Util.listFolder(folderName);
		listKeys.forEach(System.out::println);
	}
	
	@Test
	public void testUploadFile() throws FileNotFoundException {
		String folderName = "test-upload";
		String fileName = "test.txt";
		String filePath = "D:\\test\\" + fileName;
		
		InputStream inputStream = new FileInputStream(filePath);
		
		AmazonS3Util.uploadFile(folderName, fileName, inputStream);
	}
	
	@Test
	public void testDeleteFile() {
		String fileName = "test-upload/test.txt";
		AmazonS3Util.deleteFile(fileName);
	}
	
	@Test
	public void testRemoveFolder() {
		String folderName = "test-upload";
		AmazonS3Util.removeFolder(folderName);
	}

	@Test
	public void testUploadDirectory() {
		AmazonS3Util.uploadDirectory("C:\\Users\\mrche\\Downloads\\images");
	}

	@Test
	public void testDeleteAllFiles() {
		AmazonS3Util.deleteAllFiles();
	}

}
