package io.github.futurewl.ui;

import io.github.futurewl.util.PropertiesUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 功能描述：关于对话框
 *
 * @author weilai create by 2018/11/13:2:13 PM
 * @version 1.0
 */
public class AboutDialog {

    private final Stage stage = new Stage();
    private final Button closeButton = new Button();
    private final Hyperlink link = new Hyperlink();
    private final VBox stageBox = new VBox(10);
    private final HBox closeBox = new HBox();
    private final Label name = new Label(PropertiesUtils.readDetails().get("name"));
    private final Label version = new Label(PropertiesUtils.readDetails().get("version"));

    public AboutDialog(Stage primaryStage) {
        prepareStage(primaryStage);
        addListeners();
        stage.setScene(prepareScene());
    }

    public void showAbout() {
        stage.show();
    }

    /**
     * 准备舞台
     *
     * @param primaryStage
     */
    private void prepareStage(Stage primaryStage) {
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryStage);
    }

    private Scene prepareScene() {
        stageBox.setId("about");
        stageBox.setPadding(new Insets(0, 0, 0, 10));
        closeButton.setId("close");
        closeBox.setAlignment(Pos.TOP_RIGHT);
        closeBox.getChildren().add(closeButton);
        name.setId("header1");
        version.setId("version");
        link.setText("点击链接联系我们");
        link.setId("link");
        stageBox.getChildren().addAll(closeBox, name, version, link);
        Scene scene = new Scene(stageBox, 400, 150);
        scene.getStylesheets().add(getClass().getResource("/style/media_player.css").toExternalForm());
        return scene;
    }

    private void addListeners() {
        closeButton.setOnAction((e) -> stage.close());
        link.setOnAction((e) -> {
            try {
                Desktop.getDesktop().browse(new URI(PropertiesUtils.readDetails().get("link")));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
    }

}
