package io.github.futurewl;

import io.github.futurewl.controller.MediaPlayerController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 功能描述：应用启动类
 *
 * @author weilai create by 2018/11/13:2:13 PM
 * @version 1.0
 */
@SpringBootApplication
public class MainApplication extends Application implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/mediaPlayer.fxml")); // 加载 媒体播放器
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane, 650, 400);
        primaryStage.setScene(scene);
        MediaPlayerController controller = loader.getController();
        FXMLLoader playListLoader = new FXMLLoader(getClass().getResource("/fxml/playList.fxml")); // 加载 播放列表
        playListLoader.load();
        controller.injectPlayListController(playListLoader.getController());
        controller.injectPlayListRoot(playListLoader.getRoot());
        controller.bindSize(scene);
        controller.setStage(primaryStage);
        controller.applyDragAndDropFeatures(scene); // 应用拖放功能
        primaryStage.setTitle("简易媒体播放器");
        primaryStage.setOnCloseRequest(event -> System.exit(0));// 关闭界面后关闭子线程
        primaryStage.show();
    }

}
