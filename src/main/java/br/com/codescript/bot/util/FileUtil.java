package br.com.codescript.bot.util;

import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtil {

    @SneakyThrows
    public static void createIfNotExists(String name) {
        Path path = Paths.get(name);
        if (!Files.exists(path)) {
            Files.createFile(path);
            InputStream inputStream = FileUtil.class.getResourceAsStream(String.format("/%s", name));
            OutputStream outputStream = Files.newOutputStream(path);

            int value;
            while ((value = inputStream.read()) != -1) {
                outputStream.write(value);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
    }
}
