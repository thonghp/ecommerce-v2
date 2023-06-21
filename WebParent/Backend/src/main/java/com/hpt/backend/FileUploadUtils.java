package com.hpt.backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUploadUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileUploadUtils.class);

    /**
     * Save the file to the specified directory. If the directory does not exist, a new directory will be created
     *
     * @param uploadDir     the directory to save the file
     * @param fileName      the name of the file
     * @param multipartFile the object containing the data of an uploaded file
     * @throws IOException if file cannot be saved
     */
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save file: " + fileName, e);
        }
    }

    /**
     * Delete files in the specified directory, subdirectories and files in subdirectories are not affected.
     *
     * @param dir the directory path to delete files
     */
    public static void cleanDir(String dir) {
        Path dirPath = Paths.get(dir);

        try {
            Files.list(dirPath).forEach(file -> {
                if (!Files.isDirectory(file)) {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
//                        System.out.println("Could not delete file: " + file);
                        LOGGER.error("Could not delete file: " + file);
                    }
                }
            });
        } catch (IOException e) {
//            System.out.println("Could not list directory: " + dirPath);
            LOGGER.error("Could not list directory: " + dirPath);
        }
    }

    /**
     * Deletes the specified directory and the files contained in the specified directory. If the specified directory
     * has subdirectories and files, only files can be deleted, but the specified directory and subdirectories cannot
     * be deleted.
     *
     * @param dir the directory path to delete
     */
    public static void removeDir(String dir) {
        cleanDir(dir);

        try {
            Files.delete(Paths.get(dir));
        } catch (IOException e) {
//            System.out.println("Could not remove directory: " + dir);
            LOGGER.error("Could not remove directory: " + dir);
        }
    }
}
