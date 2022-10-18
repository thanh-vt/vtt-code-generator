package vn.thanhvt.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Data;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import vn.thanhvt.util.RsaCrypto;
import vn.thanhvt.util.UiUtil;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

@Data
public class RsaSignatureController implements Initializable {

    private static final String CLIENT_PRIVATE_KEY = "-----BEGIN PRIVATE KEY-----\n" +
            "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJbRnPar4wTfr3HJ\n" +
            "sjS2PajZU6uJ+fbqlpTqGYmioAEGry6KQwIvUt2NE1O7fTm+LRsIe1Z3YCFkqEVG\n" +
            "c78WFc532Q5bmkXpBqenpPRz/hB+5cQ4dDjThAdLdvpycTIlVWDf/WGmVYDUYLKd\n" +
            "maBZCSH4563k69rclK6t8jMDv1iRAgMBAAECgYBZn4yYJULN5aMbilFE87smeKc4\n" +
            "+9A4A+Wh7o9oc+SDV5aIoI77YGXs1GXUz0i2JKYnN14b3uqYTXmVWS11M0mXM5+S\n" +
            "gG4ryHqznPOe6rpDdaKOsW5YueO4UiZVYQhobveXHe1MgZK5glq/O5k3ie5RdMpg\n" +
            "jCKdoYyBKyEB3KGEtQJBAMeCPaK8kcFYbiiR1TvkXG+9WDFi0WrFUgcptx97muYj\n" +
            "NkwtPtXzSE+TOkcJ3ltAAh+h43tVHWlu1ViAwwrxLGsCQQDBhf3ZK3PCCjY2RDgm\n" +
            "T8mvr6WyJXVRHgijQ1VuaUC4FjIxSeDqsgOwI6FGWvCYeJ/U4fjHFLGn9RpJpIug\n" +
            "4k3zAkACnv5qTenoTV4dIFVZ1RU1zpoDBBZWPcNnHrAuNLSDQ4EqHQoPS4Pur7N7\n" +
            "TXDjaALfIYRYkXSydudMo1xsn2CDAkAojI7YBkbHDaS2Ui0Bug4khcVOQpMGYCin\n" +
            "LOClteAdsmjp8vTxuEMVkSGlwEBRclTrIj4iWK+w5aaQb63iFAkJAkB5NUNVFDCt\n" +
            "AV6nPbIi+odC0liCiVXD7cBD5xMC/o3nTEg1oA2GZn6l2QoaGFgLAUhorYw9CYAQ\n" +
            "MzOxkwnLS+FO\n" +
            "-----END PRIVATE KEY-----";
    private static final String CLIENT_PUBLIC_KEY = "-----BEGIN PUBLIC KEY-----\n" +
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW0Zz2q+ME369xybI0tj2o2VOr\n" +
            "ifn26paU6hmJoqABBq8uikMCL1LdjRNTu305vi0bCHtWd2AhZKhFRnO/FhXOd9kO\n" +
            "W5pF6Qanp6T0c/4QfuXEOHQ404QHS3b6cnEyJVVg3/1hplWA1GCynZmgWQkh+Oet\n" +
            "5Ova3JSurfIzA79YkQIDAQAB\n" +
            "-----END PUBLIC KEY-----";

    @FXML
    private TextArea inputTextArea;

    private AutoCompletionBinding<String> autoCompletionBinding;

    @FXML
    private TextArea outputTextArea;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.setProperty("line.separator","\r\n");
//        this.acl.setLoadClassesCallback(() -> {
//            try {
//                Pair<File, Boolean> archiveInfo = this.acl.getArchiveInfo();
//                Set<String> loadedSourceClassNames = this.dbRowsToCodeService
//                        .applyConfig(archiveInfo.getKey(), archiveInfo.getValue());
//                this.autoCompletionBinding = TextFields
//                        .bindAutoCompletion(this.classNameInput, loadedSourceClassNames);
//                this.autoCompletionBinding.setVisibleRowCount(10);
//                this.autoCompletionBinding.setMinWidth(500);
//            } catch (Exception ex) {
//                System.err.println(ex);
//            }
//        });
//        this.acl.setUnloadClassesCallback(() -> {
//            this.dbRowsToCodeService.clearConfig();
//            this.autoCompletionBinding.dispose();
//        });
    }

    @FXML
    void generate(ActionEvent event) {
        try {
            String data = this.inputTextArea.getText().replaceAll("\n", System.lineSeparator());;
            String outputText = RsaCrypto.sign(data,
                    CLIENT_PRIVATE_KEY,
                    false);
            boolean pass = RsaCrypto.verify(data, outputText, CLIENT_PUBLIC_KEY, false);
            System.out.println(pass);
            this.outputTextArea.setText(outputText);
        } catch (Exception e) {
            UiUtil.showError(e);
        }
    }

    @FXML
    void onOpenConfig(ActionEvent event) {
//        try {
//            Dialog<DbRowsToCodeSetting> settingDialog = ResourceUtil
//                    .<DbRowsToCodeSetting, ConfigController>initDialog("config", (dialog, controller) -> {
//                        controller.setOnActionHandler((buttonData, setting) -> {
//                            switch (buttonData) {
//                                case OK_DONE:
//                                    this.selectedDbRowsToCodeSetting = setting;
//                                    dialog.close();
//                                    break;
//                                case APPLY:
//                                    this.selectedDbRowsToCodeSetting = setting;
//                                    break;
//                                case CANCEL_CLOSE:
//                                    dialog.close();
//                                    break;
//                            }
//                        });
//                        controller.initSetting(this.selectedDbRowsToCodeSetting);
//                    });
//            settingDialog.initModality(Modality.APPLICATION_MODAL);
//            settingDialog.setTitle("Config");
//            ResourceUtil.applyDarkTheme(settingDialog.getDialogPane());
//            settingDialog.showAndWait();
//
//        } catch (IOException | InstantiationException | IllegalAccessException e) {
//            UiUtil.showError(e);
//        }
    }
}
