package com.kdanmobile.pdfviewer.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;

import com.kdanmobile.pdfviewer.utils.threadpools.ThreadPoolUtils;
import com.orhanobut.logger.Logger;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @classname：PrintAdapter
 * @author：luozhipeng
 * @date：13/4/18 18:10
 * @description：
 */
public class PrintAdapter extends PrintDocumentAdapter {
    private int pagescount = 0;
    private final Context context;
    private final String path;
    private Runnable runnable;

    public PrintAdapter(Context context, String path, int pagescount) {
        this.context = context;
        this.path = path;
        this.pagescount = pagescount;
    }

    /**
     * @param ：[oldAttributes, newAttributes, cancellationSignal, callback, extras]
     * @return : void
     * @methodName ：onLayout created by luozhipeng on 13/4/18 22:11.
     * @description ：每次用户更改影响输出的打印设置（例如不同的页面大小或页面方向）时调用，为应用程序提供计算要打印的页面布局的机会。至少，此方法必须返回打印文档中预期的页数。
     */
    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes, CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle extras) {
        // Respond to cancellation request
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (pagescount > 0) {
            //构建文档配置信息
            PrintDocumentInfo printDocumentInfo = new PrintDocumentInfo
                    .Builder(GlobalConfigs.CURRENT_DOCUMENT_NAME)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(pagescount)
                    .build();
            callback.onLayoutFinished(printDocumentInfo, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    /**
     * @param ：[pages, destination, canclleationSignal, callback]
     * @return : void
     * @methodName ：onWrite created by luozhipeng on 13/4/18 22:11.
     * @description ：调用将打印的页面转换为要打印的文件。此方法可以在每次onLayout（）调用后调用一次或多次。
     */
    @Override
    public void onWrite(final PageRange[] pages, final ParcelFileDescriptor destination, final CancellationSignal canclleationSignal, final WriteResultCallback callback) {
        runnable = new Runnable() {
            @Override
            public void run() {
                // Write file to PrintedPdfDocument
                OutputStream outputStream = null;
                InputStream inputStream = null;
                try {
                    inputStream = new FileInputStream(path);
                    outputStream = new FileOutputStream(destination.getFileDescriptor());

                    int bytesRead;
                    byte[] buffer = new byte[1024];
                    while ((bytesRead = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    Logger.t("PrintAdapter").d("1、print onWriteFinished successed");
                    callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
                } catch (Exception e) {
                    Logger.t("PrintAdapter").d("2、print onWriteFinished failed");
                    callback.onWriteFailed(e.toString());
                } finally {
                    CloseUtils.closeIO(inputStream, outputStream);
                }
            }
        };
        ThreadPoolUtils.getInstance().execute(runnable);
    }

    @Override
    public void onFinish() {
        super.onFinish();
        if (null != runnable) {
            ThreadPoolUtils.getInstance().remove(runnable);
        }
        Logger.t("PrintAdapter").d("3、print onFinish");
    }
}
