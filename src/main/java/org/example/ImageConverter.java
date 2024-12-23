package org.example;

import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;

public class ImageConverter {
    private static final String INPUT_FOLDER = "src/main/resources/input-files";
    private static final String OUTPUT_FOLDER = "output-files";
    private static final int THREADS_QUANTITY = 3;

    public static void main(String[] args) {
        File inputFolder = new File(INPUT_FOLDER);
        File outputFolder = new File(OUTPUT_FOLDER);

        try {
            System.out.println("Start converting");

            deleteOutputContent(outputFolder);
            List<File> validatedFiles = inputValidation(inputFolder);
            convertToWebP(validatedFiles);

            System.out.println("End converting");
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void deleteOutputContent(File outputFolder) throws IOException {
        if (outputFolder.exists()) {
            System.out.println("Cleaning output folder...");
            for (File file : outputFolder.listFiles()) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
            outputFolder.delete();
        }

        outputFolder.mkdir();
    }

    private static void deleteDirectory(File directory) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                deleteDirectory(file);
            } else {
                file.delete();
            }
        }
        directory.delete();
    }

    public static List<File> inputValidation(File inputFolder) throws IOException {
        List<File> validatedFiles = new ArrayList<>();

        if (!inputFolder.isDirectory()) {
            throw new IOException("Input folder does not exist. It should be in src/main/resources/input-files");
        }

        File[] files = inputFolder.listFiles();
        if (files == null || files.length == 0) {
            throw new IOException("Input folder is empty");
        }

        for (File file : files) {
            String fileExtension = file.getName().substring(file.getName().lastIndexOf(".")).toLowerCase();

            // skip webp
            if (fileExtension.equals(".webp")) {
                File outputFile = new File(OUTPUT_FOLDER + "/" + file.getName());
                Files.copy(file.toPath(), outputFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Copied WebP file: " + file.getName());
            }

            // convert to webp
            else if (fileExtension.equals(".jpg") || fileExtension.equals(".jpeg") || fileExtension.equals(".png")) {
                validatedFiles.add(file);
            }

            // skip unsupported files
            else {
                File unchangeableFolder = new File(OUTPUT_FOLDER + "/unchangeableFiles");
                if (unchangeableFolder.mkdirs()) {
                    System.out.println("Created folder for unchangeable files");
                }
                File unchangeableFile = new File(unchangeableFolder + "/" + file.getName());
                Files.copy(file.toPath(), unchangeableFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Moved unchangeable file: " + file.getName());
            }
        }

        return validatedFiles;
    }


    private static void convertToWebP(List<File> inputFiles) throws IOException {
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS_QUANTITY);
        List<Future<Void>> futures = new ArrayList<>();


        for (File inputFile : inputFiles) {
            Callable<Void> task = () -> {
                ImmutableImage image = ImmutableImage.loader().fromFile(inputFile);
                String outputFilePath = OUTPUT_FOLDER + "/" + inputFile.getName().substring(0, inputFile.getName().lastIndexOf(".")) + ".webp";

                try {
                    image.output(WebpWriter.MAX_LOSSLESS_COMPRESSION, outputFilePath);
                    System.out.println("Converted to WebP: " + outputFilePath);

                } catch (IOException e) {
                    System.err.println("Converting error : " + e.getMessage());
                }
                return null;
            };

            futures.add(executorService.submit(task));
        }


        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("Error waiting for task to complete: " + e.getMessage());
            }
        }

        executorService.shutdown();
    }

}