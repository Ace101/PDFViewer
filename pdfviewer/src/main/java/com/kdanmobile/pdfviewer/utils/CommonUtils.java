package com.kdanmobile.pdfviewer.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import com.kdanmobile.pdfviewer.R;
import com.kdanmobile.pdfviewer.base.ProApplication;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @classname：CommonUtils
 * @author：gongzuo
 * @date：2018/8/14 下午5:11
 * @description：
 */
public class CommonUtils {
    public final static String DATE_DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_YMD_FORMAT = "yyyy/MM/dd";

    /**
     * @param ：[time, format]
     * @return : java.lang.String
     * @methodName ：getDate created by luozhipeng on 5/12/17 13:37.
     * @description ：时间毫秒数按指定格式转字符串 yyyy-MM-dd HH:mm:ss
     */
    public static String onGetFormatDate(long time, String format) {
        SimpleDateFormat ft = new SimpleDateFormat(format, Locale.US);
        return ft.format(new Date(time));
    }

    /**
     * @param ：[context, file]
     * @return : android.net.Uri
     * @methodName ：onGetUriBySystem created by luozhipeng on 30/11/17 10:14.
     * @description ：获取对应的Uri,做了7.0系统版本兼容
     */
    public static Uri onGetUriBySystem(Context context, File file) {
        Uri uri = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.M) {
                uri = Uri.fromFile(file);
            } else {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * @param ：[context, file]
     * @return : android.net.Uri
     * @methodName ：onGetShareUriBySystem created by liujiyuan on 2018/10/15 下午2:26.
     * @description ：获取对应的文件Uri,做了8.0系统版本兼容，特别说明：需要与获取图片uri的适配区分开
     */
    public static Uri onGetShareUriBySystem(Context context, File file) {
        Uri uri = null;
        try {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                uri = Uri.fromFile(file);
            } else {
                uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    /**
     * @methodName：getImageContentUri created by wangzhe on 10/16/18 10:02.
     * @description：从数据库中获取文件的uri
     */
    public static Uri getFileUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[]{MediaStore.Files.FileColumns._ID},
                MediaStore.Files.FileColumns.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
            Uri baseUri = MediaStore.Files.getContentUri("external");
            cursor.close();
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Files.FileColumns.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);
            } else {
                return null;
            }
        }
    }

    /**
     * @param ：[ct, packageName, isOutside]
     * @return : void
     * @methodName ：openMarket created by luozhipeng on 5/12/17 13:44.
     * @description ：打开手机市场app
     */
    public static void openMarket(Context ct, String packageName, boolean isOutside) {
        String market = "market://details?id=%s";
        market = String.format(market, packageName);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (isOutside) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        intent.setData(Uri.parse(market));
        try {
            ct.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtil.showToast(ct, R.string.settings_about_us_app_not_found);
        }
    }

    public static void shareFiles(Context context, ArrayList<Uri> uris) {
        // 多文件分享
        try {
            if (uris.size() > 1) {
                Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                intent.setType("*/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.common_share)));
            } else {
                shareFile(context, uris.get(0));
            }
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtil.showToast(context, R.string.share_fail);
        }
    }

    public static void shareFile(Context context, Uri uri) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.setType("*/*");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(intent, context.getResources().getString(R.string.common_share)));
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtil.showToast(context, R.string.share_fail);
        }
    }

    /**
     * @param ：[context]
     * @return : void
     * @methodName ：shareCurrentDocument created by liujiyuan on 2018/9/13 下午3:35.
     * @description ：分享当前正在阅读的pdf文件
     */
    public static void shareCurrentDocument(Context context) {
        shareFile(context, onGetShareUriBySystem(context, new File(GlobalConfigs.FILE_ABSOLUTE_PATH)));
    }

    /**
     * @param ：[context]
     * @return : void
     * @methodName ：sendEmail created by wangzhe on 2018/8/14 下午5:29.
     * @description ：发送邮件到support@pdfreaderpro.com
     */
    public static void sendEmail(Context context) {
        try {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:support@pdfreaderpro.com"));
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            ToastUtil.showToast(context, R.string.not_found_app);
        }
    }

    /**
     * @methodName：openWebSite created by wangzhe on 2018/9/6 下午4:35.
     * @description：跳转到外部浏览器
     */
    public static void openWebSite(Context context) {
        String url = "http://www.pdfreaderpro.com/";
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * @param activity    当前activity
     * @param imageUri    拍照后照片存储路径
     * @param requestCode 调用系统相机请求码
     */
    public static void takePicture(Activity activity, Uri imageUri, int requestCode) {
        //调用系统相机
        Intent intentCamera = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intentCamera.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intentCamera.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍照结果保存至photo_file的Uri中，不保留在相册中
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        activity.startActivityForResult(intentCamera, requestCode);
    }

    /**
     * @param activity    当前activity
     * @param requestCode 打开相册的请求码
     */
    public static void openPic(Activity activity, int requestCode) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
        photoPickerIntent.setType("image/*");
        activity.startActivityForResult(photoPickerIntent, requestCode);
    }

    /**
     * @param activity    当前activity
     * @param orgUri      剪裁原图的Uri
     * @param desUri      剪裁后的图片的Uri
     * @param aspectX     X方向的比例
     * @param aspectY     Y方向的比例
     * @param width       剪裁图片的宽度
     * @param height      剪裁图片高度
     * @param requestCode 剪裁图片的请求码
     */
    public static void cropImageUri(Activity activity, Uri orgUri, Uri desUri, int aspectX, int aspectY, int width, int height, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(orgUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", aspectX);
        intent.putExtra("aspectY", aspectY);
        intent.putExtra("outputX", width);
        intent.putExtra("outputY", height);
        intent.putExtra("scale", true);
        //将剪切的图片保存到目标Uri中
        intent.putExtra(MediaStore.EXTRA_OUTPUT, desUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * @param ：[context, file, pageCounts, isEncrypted]
     * @return : void
     * @methodName ：printCurrentDocument created by liujiyuan on 2018/9/13 下午5:31.
     * @description ：打印当前阅读的文本
     */
    public static void printCurrentDocument(final Context context, final File file, final int pageCounts, final boolean isEncrypted) {
        /****** context 必须是Activity类型的上下文。否则异常：java.lang.IllegalStateException: Can print only from an activity ******/
        try {
            /****** 最低兼容的系统4.4，版本19。 ******/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (isEncrypted) {
                    ToastUtil.showToast(context, R.string.encrypted_document_not_print);
                } else {
                    if (null != file && file.exists()) {
                        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
                        PrintAttributes.Builder builder = new PrintAttributes
                                .Builder()
                                .setResolution(new PrintAttributes.Resolution("id", Context.PRINT_SERVICE, 200, 200))
                                .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                                .setMinMargins(PrintAttributes.Margins.NO_MARGINS);
                        printManager.print(GlobalConfigs.CURRENT_DOCUMENT_NAME, new PrintAdapter(context, file.getAbsolutePath(), pageCounts), builder.build());
                    }
                }
            } else {
                ToastUtil.showToast(context, R.string.print_systemversion_not_support);
            }
        } catch (Exception e) {
            ToastUtil.showToast(context, R.string.print_not_working);
        }
    }

}
