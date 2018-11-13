package io.github.futurewl.controller;

import io.github.futurewl.ui.AboutDialog;
import io.github.futurewl.ui.SliderBar;
import io.github.futurewl.ui.WarningDialog;
import io.github.futurewl.util.DateTimeUtil;
import io.github.futurewl.util.FileUtils;
import io.github.futurewl.util.PropertiesUtils;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.stereotype.Controller;

import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * 功能描述：媒体播放器控制器
 *
 * @author weilai create by 2018/11/13:2:13 PM
 * @version 1.0
 */
@Controller
public class MediaPlayerController implements Initializable {

    @FXML
    private Button play; // 播放按钮
    @FXML
    private Button stop; // 停止按钮
    @FXML
    private Button playlist; // 播放列表按钮

    @FXML
    private ToggleButton volume;

    @FXML
    private ToggleGroup group;

    @FXML
    private MenuBar menuBar; // 菜单栏

    @FXML
    private Label playTime; // 播放时间

    @FXML
    private SliderBar timeSlider; // 实现滑动条

    @FXML
    private SliderBar volumeSlider; // 声音滑动条

    @FXML
    private HBox mediaControl; // 媒体控制区

    @FXML
    private MediaView mediaView; // 媒体视图

    @FXML
    private BorderPane root; // 边境围栏

    private Stage stage = new Stage(); // 舞台
    private ObservableList playListFiles = FXCollections.observableArrayList();
    private ObjectProperty<Path> selectedMedia = new SimpleObjectProperty<>(); // 已选择的媒体文件
    private ObjectProperty<Path> deletedMedia = new SimpleObjectProperty<>(); // 已删除的媒体文件
    private int previousValue;
    private boolean stopRequested = false;
    private boolean atEndOfMedia = false;
    private Duration duration; // 持续时间
    private PlayListController playlistController; // 播放列表控制器
    private Scene playlistScene; // 播放列表场景
    private FadeTransition fadeTransition; // 淡出过渡

    /**
     * 播放
     *
     * @param event
     */
    @FXML
    public void playAction(ActionEvent event) {
        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
        if (null != mediaPlayer) {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.UNKNOWN || status == MediaPlayer.Status.HALTED) {
                // don't do anything in these states
                return;
            }
            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.READY || status == MediaPlayer.Status.STOPPED) {
                // rewind the movie if we're sitting at the end
                if (atEndOfMedia) {
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    atEndOfMedia = false;
                }
                mediaPlayer.play();
            } else {
                mediaPlayer.pause();
            }
        } else {
            event.consume();
        }
    }

    /**
     * 停止
     *
     * @param event
     */
    @FXML
    public void stopAction(ActionEvent event) {
        MediaPlayer mediaPlayer = mediaView.getMediaPlayer();
        if (null != mediaPlayer) {
            mediaPlayer.stop();
            play.setId("play");
        } else {
            event.consume();
        }
    }

    /**
     * 打开播放列表
     *
     * @param event
     */
    @FXML
    public void openPlaylist(ActionEvent event) {
        stage.setScene(playlistScene);
        stage.initOwner(((Button) event.getSource()).getScene().getWindow());
        stage.show();
        Bindings.bindContentBidirectional(playListFiles, playlistController.listViewItems());
        selectedMedia.bind(playlistController.selectedFile());
        deletedMedia.bind(playlistController.deletedFile());
    }

    /**
     * 静音 取消静音
     *
     * @param event
     */
    @FXML
    public void muteUnMute(ActionEvent event) {
        if (volumeSlider.sliderValueProperty().intValue() == 0) {
            volumeSlider.sliderValueProperty().setValue(previousValue);
        } else {
            previousValue = volumeSlider.sliderValueProperty().intValue();
            volumeSlider.sliderValueProperty().setValue(0);
        }
    }

    /**
     * 打开文件
     *
     * @param event
     */
    @FXML
    public void openFile(ActionEvent event) {
        try {
            FileChooser chooser = new FileChooser();
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Files", PropertiesUtils.readFormats()));
            Path newFile;
            newFile = chooser.showOpenDialog(((MenuItem) event.getSource()).getParentPopup().getScene().getWindow()).toPath();
            if (!playListFiles.contains(newFile)) {
                playListFiles.add(newFile);
                playVideo(newFile.toString());
            } else {
                playVideo(newFile.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑
     *
     * @param event
     */
    @FXML
    public void exitPlayer(ActionEvent event) {
        stage.close();
    }

    /**
     * 关于
     *
     * @param event
     */
    @FXML
    public void about(ActionEvent event) {
        AboutDialog aboutDialog = new AboutDialog(stage);
        aboutDialog.showAbout();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fadeTransition = new FadeTransition(Duration.millis(2000), mediaControl);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.setCycleCount(1);
        selectedMedia.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                playVideo(newValue.toString());
            }
        });
        deletedMedia.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                stopAction(null);
            }
        });
    }

    private void playVideo(String mediaUrl) {
        try {
            String mediaUrlForMedia = URLEncoder.encode(mediaUrl, "UTF-8");
            mediaUrlForMedia = "file:/" + (mediaUrlForMedia).replace("\\", "/").replace("+", "%20");
            Media media = new Media(mediaUrlForMedia);
            // create media player
            checkAndStopMediaPlayer();
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaView.setMediaPlayer(mediaPlayer);
            bindMediaPlayerControls(mediaPlayer);
            mediaPlayer.setAutoPlay(true);
            ((Stage) mediaView.getScene().getWindow()).setTitle(Paths.get(mediaUrl).getFileName().toString());
            mediaPlayer.play();
            mediaView.setPreserveRatio(false);
            mediaView.autosize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindMediaPlayerControls(final MediaPlayer mediaPlayer) {
        mediaPlayer.currentTimeProperty().addListener((ov) -> {
            updateValues(mediaPlayer);
        });
        mediaPlayer.setOnPlaying(() -> {
            if (stopRequested) {
                mediaPlayer.pause();
                stopRequested = false;
            } else {
                play.setId("pause");
            }
        });
        mediaPlayer.setOnPaused(() -> play.setId("play"));
        mediaPlayer.setOnReady(() -> {
            duration = mediaPlayer.getMedia().getDuration();
            updateValues(mediaPlayer);
        });
        timeSlider.sliderValueProperty().addListener((ov) -> {
            if (timeSlider.isTheValueChanging()) {
                if (null != mediaPlayer)
                    // multiply duration by percentage calculated by
                    // slider position
                    mediaPlayer.seek(duration.multiply(timeSlider
                            .sliderValueProperty().getValue() / 100.0));
                else
                    timeSlider.sliderValueProperty().setValue(0);
            }
        });
        volumeSlider.sliderValueProperty().addListener((ov) -> {
            if (null != mediaPlayer) {
                // multiply duration by percentage calculated by
                // slider position
                if (volumeSlider.sliderValueProperty().getValue() > 0) {
                    volume.setSelected(false);
                } else if (volumeSlider.sliderValueProperty().getValue() == 0) {
                    volume.setSelected(true);
                }
                mediaPlayer.setVolume(volumeSlider.sliderValueProperty()
                        .getValue() / 100.0);
            } else {
                volumeSlider.sliderValueProperty().setValue(0);
            }
        });
        onFullScreenHideControl((Stage) mediaView.getScene().getWindow());
    }

    private void updateValues(MediaPlayer mediaPlayer) {
        if (playTime != null && timeSlider != null && volumeSlider != null) {
            Platform.runLater(() -> {
                Duration currentTime = mediaPlayer.getCurrentTime();
                playTime.setText(" " + DateTimeUtil.formatTime(currentTime, duration));
                timeSlider.setDisable(duration.isUnknown());
                if (!timeSlider.isDisabled() && duration.greaterThan(Duration.ZERO) && !timeSlider.isTheValueChanging()) {
                    timeSlider.sliderValueProperty().setValue(currentTime.divide(duration.toMillis()).toMillis() * 100.0);
                }
                if (!volumeSlider.isTheValueChanging()) {
                    volumeSlider.sliderValueProperty().setValue((int) Math.round(mediaPlayer.getVolume() * 100));
                }
            });
        }
    }

    private void checkAndStopMediaPlayer() {
        if (null != mediaView.getMediaPlayer()) {
            stopAction(null);
            mediaView.setMediaPlayer(null);
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private DoubleProperty timerSliderWidthProperty() {
        return timeSlider.prefWidthProperty();
    }

    private DoubleProperty mediaViewHeightProperty() {
        return mediaView.fitHeightProperty();
    }

    private DoubleProperty mediaViewWidthProperty() {
        return mediaView.fitWidthProperty();
    }

    public void injectPlayListController(PlayListController playlistController) {
        this.playlistController = playlistController;
    }

    public void injectPlayListRoot(Parent playlistRoot) {
        playlistScene = new Scene(playlistRoot);
    }

    public void applyDragAndDropFeatures(Scene scene) {
        try {
            applyControlHiding(mediaControl);
            scene.setOnDragOver((dragEvent) -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasFiles()) {
                    dragEvent.acceptTransferModes(TransferMode.COPY);
                } else {
                    dragEvent.consume();
                }
            });
            scene.setOnDragDropped((dragEvent) -> {
                Dragboard db = dragEvent.getDragboard();
                if (db.hasFiles()) {
                    for (Path filePath : FileUtils.convertListFileToListPath(db.getFiles())) {
                        try {
                            if (PropertiesUtils.readFormats().contains("*" + filePath.toAbsolutePath().toString().substring(filePath.toAbsolutePath().toString().length() - 4))) {
                                if (null != mediaView.getMediaPlayer()) {
                                    mediaView.getMediaPlayer().stop();
                                }
                                playListFiles.add(filePath);
                                playVideo(filePath.toAbsolutePath().toString());
                            } else {
                                WarningDialog warningDialog = new WarningDialog((Stage) scene.getWindow());
                                warningDialog.show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            scene.addEventFilter(KeyEvent.KEY_PRESSED, (keyEvent) -> {
                if (keyEvent.getCode() == KeyCode.ESCAPE) {
                    ((Stage) scene.getWindow()).setFullScreen(false);
                }
            });
            mediaView.addEventFilter(MouseEvent.MOUSE_PRESSED, (mouseEvent) -> {
                if (mouseEvent.getButton().equals(
                        MouseButton.PRIMARY)) {
                    if (mouseEvent.getClickCount() == 2) {
                        if (((Stage) scene.getWindow()).isFullScreen()) {
                            ((Stage) scene.getWindow()).setFullScreen(false);
                        } else {
                            ((Stage) scene.getWindow()).setFullScreen(true);
                        }
                    }
                }
            });
            scene.addEventFilter(MouseEvent.MOUSE_MOVED, (mouseEvent) -> {
                if (stage.isFullScreen()) {
                    showTempMediaControlBar();
                } else {
                    showConstantMediaControlBar();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyControlHiding(Node node) {
        if (node instanceof Parent) {
            ((Parent) node).getChildrenUnmodifiable().forEach(this::applyControlHiding);
        }
        node.setOnMouseMoved(mouseEvent -> {
            if (mouseEvent.getX() > 0) {
                showConstantMediaControlBar();
            }
        });
    }

    private void onFullScreenHideControl(Stage stage) {
        try {
            stage.fullScreenProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    showTempMediaControlBar();
                } else {
                    showConstantMediaControlBar();
                }
            });
        } catch (Exception iep) {
            iep.printStackTrace();
        }
    }

    private void showTempMediaControlBar() {
        menuBar.setOpacity(0);
        mediaControl.setOpacity(1.0);
        fadeTransition.play();
    }

    private void showConstantMediaControlBar() {
        menuBar.setOpacity(1);
        fadeTransition.stop();
        mediaControl.setOpacity(1.0);
    }

    public void bindSize(Scene scene) {
        this.timerSliderWidthProperty().bind(scene.widthProperty().subtract(500));
        this.mediaViewWidthProperty().bind(scene.widthProperty());
        this.mediaViewHeightProperty().bind(scene.heightProperty().subtract(70));
    }
}