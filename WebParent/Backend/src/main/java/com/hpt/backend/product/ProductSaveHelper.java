package com.hpt.backend.product;

import com.hpt.backend.FileUploadUtils;
import com.hpt.common.entity.Product;
import com.hpt.common.entity.ProductImage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ProductSaveHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductSaveHelper.class);

    /**
     * Read name of image file from MultipartFile to save to the product object
     *
     * @param multipartFile MultipartFile
     * @param product       Product object
     */
    public static void setMainImageName(MultipartFile multipartFile, Product product) {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
            product.setMainImage(fileName);
        }
    }

    /**
     * Save id and extras name corrected
     *
     * @param imageIDs   id of image
     * @param imageNames name of image
     * @param product    product object
     */
    public static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames,
                                                  Product product) {
        if (imageIDs == null || imageIDs.length == 0) return;

        Set<ProductImage> images = new HashSet<>();

        for (int count = 0; count < imageIDs.length; count++) {
            Integer id = Integer.parseInt(imageIDs[count]);
            String name = imageNames[count];

            images.add(new ProductImage(id, name, product));
        }

        product.setImages(images);
    }

    /**
     * Save new extra image name to product
     *
     * @param extraImageMultiparts MultipartFile
     * @param product              Product object
     */
    public static void setNewExtraImageNames(MultipartFile[] extraImageMultiparts, Product product) {
        for (MultipartFile multipartFile : extraImageMultiparts) {
            if (!multipartFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));

                if (!product.containsImageName(fileName)) {
                    product.addExtraImage(fileName);
                }
            }
        }
    }

    /**
     * Save details to product
     *
     * @param detailIDs    id of detail
     * @param detailNames  name of detail
     * @param detailValues value of detail
     * @param product      product object
     */
    public static void setProductDetails(String[] detailIDs, String[] detailNames, String[] detailValues, Product product) {
        if (detailNames == null || detailNames.length == 0) return;

        for (int count = 0; count < detailNames.length; count++) {
            String name = detailNames[count];
            String value = detailValues[count];
            Integer id = Integer.parseInt(detailIDs[count]);

            if (id != 0) {
                product.addDetail(id, name, value);
            } else if (!name.isEmpty() && !value.isEmpty()) {
                product.addDetail(name, value);
            }
        }
    }

    /**
     * Save main image and extra images to file system
     *
     * @param mainImageMultipart   MultipartFile of main image
     * @param extraImageMultiparts MultipartFile of extra images
     * @param savedProduct         Product object
     * @throws IOException if an I/O error occurs
     */
    public static void saveUploadedImages(MultipartFile mainImageMultipart,
                                          MultipartFile[] extraImageMultiparts, Product savedProduct) throws IOException {
        if (!mainImageMultipart.isEmpty()) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(mainImageMultipart.getOriginalFilename()));
            String uploadDir = "product-photos/" + savedProduct.getId();

            FileUploadUtils.cleanDir(uploadDir);
            FileUploadUtils.saveFile(uploadDir, fileName, mainImageMultipart);
        }

        if (extraImageMultiparts.length > 0) {
            String uploadDir = "product-photos/" + savedProduct.getId() + "/extras";

            for (MultipartFile multipartFile : extraImageMultiparts) {
                if (multipartFile.isEmpty()) continue;

                String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
                FileUploadUtils.saveFile(uploadDir, fileName, multipartFile);
            }
        }
    }

    /**
     * Delete extra images from file system
     *
     * @param product Product object
     */
    public static void deleteExtraImagesWeredRemovedOnForm(Product product) {
        String extraImageDir = "product-photos/" + product.getId() + "/extras";
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
