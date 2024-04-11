package com.ecommerce.site.admin.helper;

import com.ecommerce.site.admin.model.entity.Product;
import com.ecommerce.site.admin.model.entity.ProductImage;
import com.ecommerce.site.admin.util.AmazonS3Util;
import com.ecommerce.site.admin.util.FileUploadUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.ecommerce.site.admin.constant.AmazonS3Constant.S3_SERVICE_ACTIVE;


public class ProductSaveHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    public static void deleteExtraImagesRemovedOnForm(@NotNull Product product) {
        if (S3_SERVICE_ACTIVE) {
            String extraImageS3Dir = "product-images/" + product.getId() + "/extras";
            List<String> listObjectKeys = AmazonS3Util.listFolder(extraImageS3Dir);

            for (String objectKey : listObjectKeys) {
                int lastIndexOfSlash = objectKey.lastIndexOf("/");
                String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());

                if (!product.containsImageName(fileName)) {
                    AmazonS3Util.deleteFile(objectKey);
                    System.out.println("Deleted extra image: " + objectKey);
                }
            }
        } else {
            String extraImageDir = "../product-images/" + product.getId() + "/extras";
            Path dirPath = Paths.get(extraImageDir);

            try {
                Files.list(dirPath).forEach(file -> {
                    String filename = file.toFile().getName();
                    if (!product.containsImageName(filename)) {
                        try {
                            Files.delete(file);
                            LOGGER.info("Deleted extra image: " + filename);
                        } catch (IOException e) {
                            LOGGER.error("Could not delete extra image: " + filename);
                        }
                    }
                });
            } catch (IOException ex) {
                LOGGER.error("Could not list directory: " + dirPath);
            }
        }
    }

    public static void setExistingExtraImageNames(String[] imageIds, String[] imageNames, Product product) {
        if (imageIds == null || imageIds.length == 0) {
            return;
        }

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIds.length; count++) {
            Integer id = Integer.parseInt(imageIds[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    public static void setProductDetails(String[] detailIds, String[] detailNames,
                                         String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) {
            return;
        }

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            int id = Integer.parseInt(detailIds[count]);
            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    public static void saveUploadedImages(@NotNull MultipartFile mainImageMultipart,
                                          MultipartFile[] extraImageMultiparts,
                                          Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));

            if (S3_SERVICE_ACTIVE) {
                String uploadS3Dir = "product-images/" + savedProduct.getId();
                List<String> listObjectKeys = AmazonS3Util.listFolder(uploadS3Dir + "/");
                for (String objectKey: listObjectKeys) {
                    if (!objectKey.contains("/extras/")) {
                        AmazonS3Util.deleteFile(objectKey);
                    }
                }
                AmazonS3Util.uploadFile(uploadS3Dir, fileName, mainImageMultipart.getInputStream());
            } else {
                String uploadDir = "../product-images/" + savedProduct.getId();
                FileUploadUtil.cleanDir(uploadDir);
                FileUploadUtil.saveFile(uploadDir, fileName, mainImageMultipart);
            }
        }

        if (extraImageMultiparts.length > 0) {
            if (S3_SERVICE_ACTIVE) {
                String uploadS3Dir = "product-images/" + savedProduct.getId() + "/extras";
                for (MultipartFile multipartFile : extraImageMultiparts) {
                    if (multipartFile.isEmpty()) {
                        continue;
                    }
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    AmazonS3Util.uploadFile(uploadS3Dir, fileName, multipartFile.getInputStream());
                }
            } else {
                String uploadDir = "../product-images/" + savedProduct.getId() + "/extras";
                for (MultipartFile multipartFile : extraImageMultiparts) {
                    if (multipartFile.isEmpty()) {
                        continue;
                    }
                    String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                    FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
                }
            }
        }
    }

    public static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        if (extraImageMultiparts.length > 0) {
            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (!multipartFile.isEmpty()) {
                    String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
                    if (!product.containsImageName(fileName)) {
                        product.addExtraImage(fileName);
                    }
                }
            }
        }
    }

    public static void setMainImageName(@NotNull MultipartFile mainImageMultipart, Product product) {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(mainImageMultipart.getOriginalFilename());
            product.setMainImage(fileName);
        }
    }
}
