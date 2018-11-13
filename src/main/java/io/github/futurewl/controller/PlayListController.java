package io.github.futurewl.controller;

import io.github.futurewl.util.FileUtils;
import io.github.futurewl.util.PropertiesUtils;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 功能描述：播放列表控制器
 *
 * @author weilai create by 2018/11/13:2:13 PM
 * @version 1.0
 */
@Controller
public class PlayListController implements Initializable {

    @FXML
    private Button add; // 添加按钮

    @FXML
    private Button delete; // 删除按钮

    @FXML
    private ListView playList; // 播放列表

    private ObservableList playListFiles = FXCollections.observableArrayList();
    private ObjectProperty<Path> selectedMedia = new SimpleObjectProperty<>();
    private ObjectProperty<Path> deletedMedia = new SimpleObjectProperty<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        playList.setOnMouseClicked((click) -> {
            if (click.getClickCount() == 2) {
                if (playList.getSelectionModel().getSelectedItem() != null) {
                    selectedMedia.setValue((Path) playList.getSelectionModel().getSelectedItem());
                }
            }
        });
    }

    @FXML
    public void add(ActionEvent event) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("文件选择", PropertiesUtils.readFormats()));
        List<Path> listOfFiles;
        listOfFiles = FileUtils.convertListFileToListPath(chooser.showOpenMultipleDialog(((Button) event.getSource()).getScene().getWindow()));
        if (listOfFiles != null) {
            listOfFiles.forEach(System.out::println);
            playListFiles.addAll(listOfFiles);
            playListFiles.forEach(System.out::println);
            playList.setItems(playListFiles);
        }
    }

    @FXML
    public void delete(ActionEvent event) {
        if (playList.getSelectionModel().getSelectedItem() != null) {
            if (null != playListFiles || !playListFiles.isEmpty()) {
                deletedMedia.setValue((Path) playList.getSelectionModel().getSelectedItem());
                playListFiles.remove(playList.getSelectionModel().getSelectedItem());
                playList.setItems(playListFiles);
            }
        }
    }

    public ObservableList listViewItems() {
        return playListFiles;
    }

    public ObjectProperty<Path> selectedFile() {
        return selectedMedia;
    }

    public ObjectProperty<Path> deletedFile() {
        return deletedMedia;
    }
}
