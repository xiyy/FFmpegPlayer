package com.xi.liuliu.ffmpegplayer.http;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.xi.liuliu.ffmpegplayer.http.model.BaseResModel;
import com.xi.liuliu.ffmpegplayer.view.LoadingAlertDialog;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by liuliu on 2018/2/1.
 */

public abstract class HttpResultObserver<T> implements Observer<BaseResModel<T>> {
    private static final String TAG = "HttpResultObserver";
    protected Context mContext;
    private String defaultShowMsg = "正在加载..";
    private boolean isShowDialog = false;
    private String requestName = "未知接口";
    private LoadingAlertDialog loadingAlertDialog;
    private static final String TOKEN_OVERDUE = "101";
    private static final String TOKEN_INFO_ERROR = "102";
    private static final String NO_TOKEN = "103";
    private static final String WRONG_PASSWORD = "password error.";
    private static final String USER_NOT_FIND = "user not found.";
    private Disposable mDisposable;


    public HttpResultObserver(Context mContext, String requestName) {
        this.mContext = mContext;
        this.requestName = requestName;
    }


    public HttpResultObserver(Context mContext, String showMsg, String requestName) {
        this(mContext, requestName);
        this.defaultShowMsg = showMsg;
        isShowDialog = true;
    }


    public HttpResultObserver(Context mContext, boolean isShowDialog, String requestName) {
        this(mContext, requestName);
        this.isShowDialog = isShowDialog;
    }


    @Override
    public void onSubscribe(@NonNull Disposable d) {
        mDisposable = d;
        //订阅的时候
        if (isShowDialog) {
            onRequestStart();
        }
    }


    @Override
    public void onNext(@NonNull BaseResModel<T> tBaseResModel) {
        //成功
        if (tBaseResModel.isSuccess()) {
            onSuccess(tBaseResModel.getData());
        } else {
            onCodeError(tBaseResModel.getMessage());
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        mDisposable.dispose();
        if (isShowDialog) {
            closeProgressDialog();
        }
        Log.i(TAG, requestName + "  接口请求失败  失败信息:" + e);

    }

    @Override
    public void onComplete() {
        if (isShowDialog) {
            closeProgressDialog();
        }
        mDisposable.dispose();
    }

    protected abstract void onSuccess(T t);

    protected void onCodeError(String codeMessage) {
        if (isShowDialog) {
            closeProgressDialog();
        }
        if (codeMessage == null) {
            return;
        }

        switch (codeMessage) {
            case TOKEN_OVERDUE:
                //ToastUtil.toastShort(mContext, "登录过期,将重新登录!");
                break;
            case TOKEN_INFO_ERROR:
                //ToastUtil.toastShort(mContext, "token有误,将重新登录!");
                break;
            case NO_TOKEN:
//                ToastUtil.toastShort(mContext, "token有误,将重新登录!");
//                Intent intent = new Intent(mContext, LoginActivity.class);
//                mContext.startActivity(intent);
                break;
            case WRONG_PASSWORD:
                //ToastUtil.toastShort(mContext, "密码错误!");
                break;
            case USER_NOT_FIND:
                //ToastUtil.toastShort(mContext, "没有此用户!");
                break;
            default:
                break;
        }

    }

    private void onRequestStart() {
        loadingAlertDialog = new LoadingAlertDialog(mContext);
        loadingAlertDialog.showDialog(defaultShowMsg);
    }

    private void closeProgressDialog() {
        loadingAlertDialog.dismiss();
    }
}
