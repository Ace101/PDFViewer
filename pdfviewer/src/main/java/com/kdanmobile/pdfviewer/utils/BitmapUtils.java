package com.kdanmobile.pdfviewer.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @classname：BitmapUtils
 * @author：wangzhe
 * @date：2018/9/13 下午2:19
 * @description：
 */
public class BitmapUtils {

    private BitmapUtils() {

    }

    public static Bitmap getBitmapFromFile(File file) {
        if (file.exists()) {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = 1;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
        } else {
            return null;
        }
    }

    public static void saveBitmapToFile(Bitmap bitmap, File file) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
