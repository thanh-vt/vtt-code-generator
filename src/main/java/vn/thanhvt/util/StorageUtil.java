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
import vn.thanhvt.constant.AppConstant;

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

        private boolean darkModeEnabled = false;

        private String lastInDir = Paths.get(System.getProperty(AppConstant.HOME_DIR)).toAbsolutePath().toString();

        private String lastOutDir = Paths.get(System.getProperty(AppConstant.HOME_DIR)).toAbsolutePath().toString();

    }

    private final Path BASE_DIR = Paths.get(System.getProperty(AppConstant.WORKING_DIR));

    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Setting SETTING = Setting.builder()
        .darkModeEnabled(Boolean.FALSE)
        .lastInDir(Paths.get(System.getProperty(AppConstant.HOME_DIR)).toAbsolutePath().toString())
        .lastOutDir(Paths.get(System.getProperty(AppConstant.HOME_DIR)).toAbsolutePath().toString())
        .build();

    public void initSetting() {
        Path settingFile = BASE_DIR.resolve("setting.json");
        if (!Files.exists(settingFile)) {
            try {
                Files.createFile(settingFile);
                Files.write(settingFile, OBJECT_MAPPER.writeValueAsBytes(SETTING));
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
