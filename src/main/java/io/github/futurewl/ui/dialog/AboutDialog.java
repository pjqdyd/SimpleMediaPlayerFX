package io.github.futurewl.ui.dialog;

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
import org.springframework.stereotype.Component;

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
public class AboutDialog extends BaseDialog {

    private Stage owner;
    private final Button closeButton = new Button();
    private final Hyperlink link = new Hyperlink();
    private final VBox stageBox = new VBox(10);
    private final HBox closeBox = new HBox();
    private final Label name = new Label(PropertiesUtils.readDetails().get("name"));
    private final Label version = new Label(PropertiesUtils.readDetails().get("version"));

    public void create() {
        dialog = new Stage();
        prepareStage(); // 准备舞台
        addListeners(); // 添加监听器
        Scene scene = prepareScene(); // 准备场景
        dialog.setScene(scene); // 设置场景
    }


    /**
     * 准备舞台
     */
    private void prepareStage() {
        dialog.initStyle(StageStyle.TRANSPARENT); // 初始化风格
        dialog.initModality(Modality.WINDOW_MODAL); // 初始化 模态
    }

    /**
     * 准备场景
     *
     * @return
     */
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

    /**
     * 添加监听器
     */
    private void addListeners() {
        closeButton.setOnAction((e) -> dialog.close());
        link.setOnAction((e) -> {
            try {
                Desktop.getDesktop().browse(new URI(PropertiesUtils.readDetails().get("link")));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });
    }

    public void show() {
        dialog.show();
    }

    public Stage getOwner() {
        return owner;
    }

    public void setOwner(Stage owner) {
        this.owner = owner;
    }
}
