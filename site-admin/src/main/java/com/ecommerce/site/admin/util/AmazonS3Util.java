package com.ecommerce.site.admin.util;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class AmazonS3Util {

	private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3Util.class);

	private static final String BUCKET_NAME;
	
	static {
		BUCKET_NAME = System.getenv("AWS_BUCKET_NAME");
	}
	
	public static @NotNull List<String> listFolder(String folderName) {
		S3Client client = S3Client.builder().build();
		ListObjectsRequest listRequest = ListObjectsRequest
				.builder()
				.bucket(BUCKET_NAME)
				.prefix(folderName)
				.build();
		
		ListObjectsResponse response = client.listObjects(listRequest);
		List<S3Object> contents = response.contents();
		ListIterator<S3Object> listIterator = contents.listIterator();
		List<String> listKeys = new ArrayList<>();
		
		while (listIterator.hasNext()) {
			S3Object object = listIterator.next();
			listKeys.add(object.key());
		}
		
		return listKeys;
	}
	
	public static void uploadFile(String folderName, String fileName, @NotNull InputStream inputStream) {
		try (inputStream) {
			S3Client s3Client = S3Client.builder().build();
			PutObjectRequest request = PutObjectRequest
					.builder()
					.bucket(BUCKET_NAME)
					.key(folderName + "/" + fileName).acl("public-read")
					.build();

			int contentLength = inputStream.available();
			s3Client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));
		} catch (IOException e) {
			LOGGER.error("Could not upload file to Amazon S3", e);
		}
	}

	private static List<File> listFiles(File directory) {
		List<File> fileList = new ArrayList<>();

		for (File file : directory.listFiles()) {
			if (file.isFile()) {
				fileList.add(file);
			} else if (file.isDirectory()) {
				fileList.addAll(listFiles(file));
			}
		}

		return fileList;
	}
	
	public static void deleteFile(String fileName) {
		S3Client client = S3Client.builder().build();
		
		DeleteObjectRequest request = DeleteObjectRequest
				.builder()
				.bucket(BUCKET_NAME)
				.key(fileName)
				.build();

		client.deleteObject(request);
	}
	
	public static void removeFolder(String folderName) {
		S3Client client = S3Client.builder().build();
		ListObjectsRequest listRequest = ListObjectsRequest
				.builder()
				.bucket(BUCKET_NAME)
				.prefix(folderName + "/")
				.build();
		
		ListObjectsResponse response = client.listObjects(listRequest);
		List<S3Object> contents = response.contents();

		for (S3Object object : contents) {
			DeleteObjectRequest request = DeleteObjectRequest.builder().bucket(BUCKET_NAME)
					.key(object.key()).build();
			client.deleteObject(request);
			System.out.println("Deleted " + object.key());
		}		
	}

	public static void uploadDirectory(String folderName) {
		S3Client client = S3Client.builder().build();

		File localDirectory = new File(folderName);

		if (!localDirectory.exists()) {
			System.out.println("Local directory does not exist.");
			return;
		}

		if (!localDirectory.isDirectory()) {
			System.out.println("Local path is not a directory.");
			return;
		}

		List<File> files = listFiles(localDirectory);

		for (File file : files) {
			String key = file.getPath().replace(localDirectory.getPath(), "").replace("\\", "/").substring(1);

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(BUCKET_NAME)
					.key(key)
					.acl(ObjectCannedACL.PUBLIC_READ)
					.build();

			try {
				PutObjectResponse response = client.putObject(putObjectRequest, RequestBody.fromFile(file));
				System.out.println("Uploaded file: " + key + ", ETag: " + response.eTag());
			} catch (S3Exception e) {
				System.err.println(e.awsErrorDetails().errorMessage());
			}
		}
	}

	public static void deleteAllFiles() {
		S3Client s3Client = S3Client.builder()
				.credentialsProvider(DefaultCredentialsProvider.create())
				.build();

		ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
				.bucket(BUCKET_NAME)
				.build();

		ListObjectsV2Response listObjectsResponse;

		do {
			listObjectsResponse = s3Client.listObjectsV2(listObjectsRequest);

			for (S3Object s3Object : listObjectsResponse.contents()) {
				DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
						.bucket(BUCKET_NAME)
						.key(s3Object.key())
						.build();

				s3Client.deleteObject(deleteObjectRequest);
				System.out.println("Deleted file: " + s3Object.key());
			}

			listObjectsRequest = ListObjectsV2Request.builder()
					.bucket(BUCKET_NAME)
					.continuationToken(listObjectsResponse.nextContinuationToken())
					.build();

		} while (listObjectsResponse.isTruncated());

		System.out.println("All files in bucket " + BUCKET_NAME + " have been deleted.");
	}
}
