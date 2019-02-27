package com.kdanmobile.pdfviewer.utils.threadpools;

import android.app.Activity;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * @classname：SimpleBackgroundTask
 * @author：luozhipeng
 * @date：27/7/18 10:53
 * @description： Async异步线程处理, 主要解决对leak的处理
 */
public abstract class SimpleBackgroundTask<T> extends AsyncTask<Void, Void, T> {
    WeakReference<Activity> weakActivity;

    public SimpleBackgroundTask(Activity activity) {
        weakActivity = new WeakReference<>(activity);
    }

    @Override
    protected final T doInBackground(Void... voids) {
        return onRun();
    }

    @Override
    protected void onPostExecute(T t) {
        Activity activity = weakActivity.get();
        if ((null == activity) || activity.isFinishing() || activity.isDestroyed()) {
            cancel(true);
            return;
        } else {
            onSuccess(t);
            cancel(true);
        }
    }

    public SimpleBackgroundTask<T> execute() {
        executeOnExecutor(ThreadPoolUtils.getInstance().poolExecutor);
        return this;
    }

    abstract protected T onRun();

    abstract protected void onSuccess(T result);
}
