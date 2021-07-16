package org.kymjs.aframe.ui;

import org.kymjs.aframe.KJActivityManager;
import org.kymjs.aframe.utils.DensityUtils;
import org.kymjs.aframe.utils.StringUtils;
import org.kymjs.aframe.utils.SystemTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 应用程序UI相关工具类
 * 
 * @author kymjs(kymjs123@gmail.com)
 */
public class ViewInject {

    private ViewInject() {}

    private static class ClassHolder {
        private static final ViewInject instance = new ViewInject();
    }

    public static ViewInject create() {
        return ClassHolder.instance;
    }

    public static void toast(String msg) {
        try {
            toast(KJActivityManager.create().topActivity(), msg);
        } catch (Exception e) {
        }
    }

    public static void longToast(String msg) {
        try {
            longToast(KJActivityManager.create().topActivity(), msg);
        } catch (Exception e) {
        }
    }

    public static void longToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * 返回一个退出确认对话框
     */
    public void getExitDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("确定退出吗？");
        builder.setCancelable(false);
        builder.setNegativeButton("取消", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                KJActivityManager.create().AppExit(context);
            }
        });
        builder.create();
        builder.show();
        builder = null;
    }

    /**
     * 用于创建PopupWindow封装一些公用属性
     */
    private PopupWindow createWindow(View view, int w, int h, int argb) {
        PopupWindow popupView = new PopupWindow(view, w, h);
        popupView.setFocusable(true);
        popupView.setBackgroundDrawable(new ColorDrawable(argb));
        popupView.setOutsideTouchable(true);
        return popupView;
    }

    /**
     * 返回一个日期对话框（暂定）
     */
    public void getDateDialog(String title, final TextView textView) {
        final String[] time = SystemTool.getDataTime("yyyy-MM-dd").split("-");
        final int year = StringUtils.toInt(time[0], 0);
        final int month = StringUtils.toInt(time[1], 1);
        final int day = StringUtils.toInt(time[2], 0);
        DatePickerDialog dialog = new DatePickerDialog(textView.getContext(),
                new OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                            int monthOfYear, int dayOfMonth) {
                        textView.setText(year + "-" + (monthOfYear + 1) + "-"
                                + dayOfMonth);
                    }
                }, year, month - 1, day);
        dialog.setTitle(title);
        dialog.show();
    }

    /**
     * 返回一个等待信息弹窗
     * 
     * @param aty
     *            要显示弹出窗的Activity
     * @param msg
     *            弹出窗上要显示的文字
     * @param cancel
     *            dialog是否可以被取消
     */
    public static ProgressDialog getprogress(Activity aty, String msg,
            boolean cancel) {
        // 实例化一个ProgressBarDialog
        ProgressDialog progressDialog = new ProgressDialog(aty);
        progressDialog.setMessage(msg);
        progressDialog.getWindow().setLayout(DensityUtils.getScreenW(aty),
                DensityUtils.getScreenH(aty));
        progressDialog.setCancelable(cancel);
        // 设置ProgressBarDialog的显示样式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        return progressDialog;
    }

    public abstract class UICallBack {
        void onCallBack() {};

        void onCallBack(int i) {};

        void onCallBack(String str) {};

        void onCallBack(int i, String str) {};
    }
}
