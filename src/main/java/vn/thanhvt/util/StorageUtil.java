package vn.thanhvt.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;

/**
 * @author pysga
 * @created 5/22/2022 - 2:43 PM
 * @project json-to-code-generator
 * @since 1.0
 **/
@UtilityClass
public class StorageUtil {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Setting {

        private String lastOutDir;

    }

    private final Path BASE_DIR = Paths.get(System.getProperty("user.dir"));

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Setting SETTING = new Setting(Paths.get(System.getProperty("user.home")).toAbsolutePath().toString());

    static {
        Path settingFile = BASE_DIR.resolve("setting.json");
        if (!Files.exists(settingFile)) {
            try {
                Files.createFile(settingFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                SETTING = OBJECT_MAPPER.readValue(settingFile.toFile(), Setting.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveSetting() {
        Path settingFile = BASE_DIR.resolve("setting.json");
        try {
            Files.write(settingFile, OBJECT_MAPPER.writeValueAsBytes(SETTING));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Setting getSetting() {
        return SETTING;
    }

}
