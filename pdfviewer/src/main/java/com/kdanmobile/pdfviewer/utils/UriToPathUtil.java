package com.kdanmobile.pdfviewer.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * @类名:UriToPathUtil
 * @类描述:专门为Android4.4设计的从U获取文件绝对路径
 * @作者:luozhipeng
 * @创建时间:2015年7月7日-下午2:42:56
 * @修改人:
 * @修改时间:
 * @修改备注:
 * @版本:
 * @Copyright:(c)-2015kdanmobile
 */
public class UriToPathUtil {
    private static class SingleTon {
        private final static UriToPathUtil instance = new UriToPathUtil();
    }

    public static UriToPathUtil getInstance() {
        return SingleTon.instance;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public String getPath(Context context, Uri fileUri) {
        try {
            final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
            if (isKitKat && DocumentsContract.isDocumentUri(context, fileUri)) {
                if (isExternalStorageDocument(fileUri)) {
                    String docId = DocumentsContract.getDocumentId(fileUri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }

                if (isDownloadsDocument(fileUri)) {
                    String id = DocumentsContract.getDocumentId(fileUri);
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                    return getDataColumn(context, contentUri, null, null);
                }

                if (isMediaDocument(fileUri)) {
                    String docId = DocumentsContract.getDocumentId(fileUri);
                    String[] split = docId.split(":");
                    String type = split[0];
                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    String selection = MediaStore.Images.Media._ID + "=?";
                    String[] selectionArgs = new String[]{split[1]};
                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }

            // MediaStore (and general)
            if ("content".equalsIgnoreCase(fileUri.getScheme())) {
                // Return the remote address
                if (isGooglePhotosUri(fileUri)) {
                    return fileUri.getLastPathSegment();
                }
                return getDataColumn(context, fileUri, null, null);
            }

            // File
            if ("file".equalsIgnoreCase(fileUri.getScheme())) {
                return Uri.decode(fileUri.getEncodedPath());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    private String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        try {
            final String[] projection = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if ((cursor != null) && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(index);
            }
        } finally {
            CloseUtils.closeIO(cursor);
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }
}
