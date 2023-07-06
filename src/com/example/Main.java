package com.example;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        // 获取当前目录路径
        String currentDirectory = System.getProperty("user.dir");
        System.out.println("当前目录: " + currentDirectory);

        // 打印当前目录下的文件
        System.out.println("当前目录下的文件: ");
        Arrays.asList(Objects.requireNonNull(new File(currentDirectory).listFiles()))
                .stream()
                .filter(File::isFile)
                .forEach(System.out::println);
        System.out.println();

        // 获取当前目录下的所有图片文件
        List<File> imageFiles = getImageFiles(currentDirectory);
        // 遍历图片文件并执行移动操作
        for (File imageFile : imageFiles) {
            String key = getKeyFromFileName(imageFile.getName());
            File matchingFile = findMatchingFile(currentDirectory, key);

            if (matchingFile != null) {
                moveFilesToNewDirectory(imageFile, matchingFile);
                System.out.println(String.format("移动文件: <%s>, <%s>", imageFile.getName(), matchingFile.getName()));
            }
        }
    }

    // 获取当前目录下的所有图片文件
    private static List<File> getImageFiles(String directory) {
        List<File> imageFiles = new ArrayList<>();
        File[] files = new File(directory).listFiles();
        if (files != null) {
            for (File file : files) {
                if (isImageFile(file)) {
                    imageFiles.add(file);
                }
            }
        }
        return imageFiles;
    }

    // 判断文件是否为图片文件
    private static boolean isImageFile(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
    }

    // 从文件名中提取key
    private static String getKeyFromFileName(String fileName) {
        return fileName.substring(0, fileName.indexOf(" "));
    }

    // 查找包含指定key的文件
    private static File findMatchingFile(String directory, String key) {
        File[] files = new File(directory).listFiles();
        if (files != null) {
            for (File file : files) {
                // file中包含key，且file为文件，且file不是图片
                if (file.getName().contains(key) && !isImageFile(file) && file.isFile()) {
                    return file;
                }
            }
        }
        return null;
    }

    // 移动文件到新目录
    private static void moveFilesToNewDirectory(File file1, File file2) {
        String newDirectoryName = file1.getName().substring(0, file1.getName().lastIndexOf('.'));
        File newDirectory = new File(file1.getParent(), newDirectoryName);
        if (!newDirectory.exists()) {
            newDirectory.mkdir();
        }

        try {
            Path source1 = file1.toPath();
            Path target1 = new File(newDirectory, file1.getName()).toPath();
            Files.move(source1, target1, StandardCopyOption.REPLACE_EXISTING);

            Path source2 = file2.toPath();
            Path target2 = new File(newDirectory, file2.getName()).toPath();
            Files.move(source2, target2, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
