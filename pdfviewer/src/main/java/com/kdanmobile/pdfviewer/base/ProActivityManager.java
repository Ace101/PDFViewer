package com.kdanmobile.pdfviewer.base;

import android.app.Activity;

import java.util.Stack;

/**
 * @classname：ProActivityManager
 * @author：luozhipeng
 * @date：23/7/18 12:10
 * @description： activity管理类
 */
public class ProActivityManager {
    private final Stack<Activity> activities = new Stack<>();

    private ProActivityManager() {

    }

    private final static class Instance {
        private final static ProActivityManager instance = new ProActivityManager();
    }

    public static ProActivityManager getInstance() {
        return Instance.instance;
    }

    /**
     * @param activity
     * @方法说明:将当前Activity推入栈中
     * @方法名称:pullActivity
     * @返回 void
     */
    public synchronized void pullActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.push(activity);
        }
    }

    /**
     * @param ：[]
     * @return : android.app.Activity
     * @methodName ：currentActivity created by luozhipeng on 22/11/17 17:32.
     * @description :获得当前栈顶Activity
     */
    public Activity currentActivity() {
        return activities.lastElement();
    }

    /**
     * @param ：[cls]
     * @return : android.app.Activity
     * @methodName ：getActivityByClass created by luozhipeng on 22/11/17 17:31.
     * @description ：获取指定的activity
     */
    public Activity getActivityByClass(Class cls) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                if (activity != null && !activity.isFinishing()) {
                    return activity;
                }
            }
        }
        return null;
    }

    /**
     * @param ：[activity]
     * @return : void
     * @methodName ：destoryActivity created by luozhipeng on 22/11/17 17:31.
     * @description ：销毁指定的Activity
     */
    public synchronized void destoryActivity(Activity activity, boolean isDestoryed) {
        if (activity != null) {
            if (activities.contains(activity)) {
                // 在从自定义集合中取出当前Activity时，也进行了Activity的关闭操
                activities.remove(activity);
            }

            if (isDestoryed && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * @param ：[cls]
     * @return : void
     * @methodName ：destoryActivity created by luozhipeng on 22/11/17 17:31.
     * @description ：结束指定类名的Activity
     */
    public synchronized void destoryActivity(Class<?> cls) {
        for (Activity activity : activities) {
            if (activity.getClass().equals(cls)) {
                destoryActivity(activity, true);
                return;//必须跳出循环
            }
        }
    }

    /**
     * @param ：[]
     * @return : void
     * @methodName ：destoryCurrentActivity created by luozhipeng on 22/11/17 17:30.
     * @description ：销毁当前的activity
     */
    public synchronized void destoryCurrentActivity() {
        Activity activity = activities.pop();
        if ((activity != null) && !activity.isFinishing()) {
            activity.finish();
        }
    }

    /**
     * @方法说明:退出栈中所有的activity
     * @方法名称:destoryAllActivity
     * @返回 void
     */
    public synchronized void destoryAllActivity() {
        while (!activities.isEmpty()) {
            Activity activity = activities.pop();
            if ((null != activity) && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }
}
