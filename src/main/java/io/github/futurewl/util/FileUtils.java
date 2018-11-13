package io.github.futurewl.util;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 功能描述：文件工具类
 *
 * @author weilai create by 2018/11/13:2:13 PM
 * @version 1.0
 */
public final class FileUtils {

    /**
     * 将列表文件转换为列表路径
     *
     * @param listOfFile
     * @return
     */
    public static List<Path> convertListFileToListPath(List<File> listOfFile) {
        return listOfFile.stream().map(File::toPath).collect(Collectors.toList());
    }

}
