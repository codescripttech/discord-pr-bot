package br.com.codescript.bot.configuration;

import br.com.codescript.bot.util.FileUtil;
import lombok.SneakyThrows;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class CodeConfiguration {

    private static JSONObject jsonObject;

    private static final String FILE_NAME = "configuration.json";

    private CodeConfiguration() {}

    @SneakyThrows
    public static void init() {
        FileUtil.createIfNotExists(FILE_NAME);
        FileReader fileReader = new FileReader(FILE_NAME);
        jsonObject = (JSONObject) new JSONParser().parse(fileReader);
    }

    public static String getString(String key) {
        return (String) jsonObject.get(key);
    }
}
