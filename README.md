简易媒体播放器
=============

A simple media player in JavaFX. It has the following features:

1. Supports drag and drop playing option. You can directly drop media into the MediaView to play them. You can also open a media using File -> Open.
2. A playlist to control all you media. You can either use the add button to add media to the playlist or drag and drop media into its stage.
3. Play, Pause, Next, Previous options.
4. An interactive time-slider and volume slider control.
5. Currently supports just one theme.

如何使用此项目
----------------------

你可以克隆本仓库然后执行以下命令：

**_Maven_**
 
To Run
                    
    $ mvn jfx:run

To Build a Native Package 

    $ mvn jfx:native 
    
    
注意:

   1.mvn jfx:run提示无法找到主类:
   在maven plugin的javafx插件的"<configration>"的"<mainClass>"中的主类写全包名
 
   2.mvn jfx:run提示springboot信息无法创建bean MediaPlayerController:
   这里是引入插件jar包版本的问题,将javafx插件的版本改为最新的8.8.3即可解决问题
      
