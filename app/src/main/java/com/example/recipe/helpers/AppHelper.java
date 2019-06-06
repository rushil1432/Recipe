package com.example.recipe.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.example.recipe.R;
import com.example.recipe.app.AppConstants;

import es.dmoral.toasty.Toasty;

public class AppHelper {


    /**
     * method to check if android version is lollipop
     *
     * @return this return value
     */
    public static boolean isAndroid5() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isAndroid26() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
    }




    /**
     * method to check if there is a connection
     *
     * @param context this is  parameter for isNetworkAvailable  method
     * @return return value
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }


    /**
     * method to launch the activities
     *
     * @param mContext  this is the first parameter for LaunchActivity  method
     * @param mActivity this is the second parameter for LaunchActivity  method
     */
    public static void LaunchActivity(Activity mContext, Class mActivity) {
        Intent mIntent = new Intent(mContext, mActivity);
        mContext.startActivity(mIntent);

    }

    /**
     * method to get color
     *
     * @param context this is the first parameter for getColor  method
     * @param id      this is the second parameter for getColor  method
     * @return return value
     */
    public static int getColor(Context context, int id) {
        if (isAndroid5()) {
            return ContextCompat.getColor(context, id);
        } else {
            return context.getResources().getColor(id);
        }
    }


    /**
     * method to get drawable
     *
     * @param context this is the first parameter for getDrawable  method
     * @param id      this is the second parameter for getDrawable  method
     * @return return value
     */
    public static Drawable getDrawable(Context context, int id) {
        if (isAndroid5()) {
            return ContextCompat.getDrawable(context, id);
        } else {
            return context.getResources().getDrawable(id);
        }
    }

    public static void ErrorToast(Context mContext, String Message) {

        Toasty.error(mContext,Message, Toast.LENGTH_SHORT,true).show();
    }

    public static void SuccessToast(Context mContext, String Message) {

        Toasty.success(mContext,Message, Toast.LENGTH_SHORT,true).show();
    }

    public static void WarningToast(Context mContext, String Message) {

        Toasty.warning(mContext,Message, Toast.LENGTH_SHORT,true).show();
    }

    public static void InfoToast(Context mContext, String Message) {

        Toasty.info(mContext,Message, Toast.LENGTH_SHORT,true).show();
    }

    public static void NormalToast(Context mContext, String Message) {

        Toasty.normal(mContext,Message, Toast.LENGTH_SHORT).show();
    }



    /**
     * method for LogCat
     *
     * @param Message this is  parameter for LogCat  method
     */
    public static void LogCat(String Message) {
        if (AppConstants.DEBUGGING_MODE)
            Log.e(AppConstants.TAG, Message);
    }

    public static void LogDCat(String Message) {
            Log.d(AppConstants.TAG, Message);
    }



    /**
     * method for Log cat Throwable
     *
     * @param Message this is  parameter for LogCatThrowable  method
     */
    public static void LogCat(Throwable Message) {
        if (AppConstants.DEBUGGING_MODE)
            Log.e(AppConstants.TAG, " Throwable " + Message.getMessage());
    }



    public static boolean checkAllPermissions(Activity activity, String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * method to request permissions
     *
     * @param mActivity  this is the first parameter for requestPermission  method
     * @param permission this is the second parameter for requestPermission  method
     */
    public static void requestPermission(final Activity mActivity, String permission) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission)) {
            String title = null;
            String Message = null;
            switch (permission) {
                case Manifest.permission.CAMERA:
                    title = mActivity.getString(R.string.camera_permission);
                    Message = mActivity.getString(R.string.camera_permission_message);
                    break;
                case Manifest.permission.RECORD_AUDIO:
                    title = mActivity.getString(R.string.audio_permission);
                    Message = mActivity.getString(R.string.record_audio_permission_message);
                    break;

                case Manifest.permission.MODIFY_AUDIO_SETTINGS:
                    title = mActivity.getString(R.string.camera_permission);
                    Message = mActivity.getString(R.string.settings_audio_permission_message);
                    break;
                case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                    title = mActivity.getString(R.string.storage_permission);
                    Message = mActivity.getString(R.string.write_storage_permission_message);
                    break;
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                    title = mActivity.getString(R.string.storage_permission);
                    Message = mActivity.getString(R.string.read_storage_permission_message);
                    break;
                case Manifest.permission.READ_CONTACTS:
                    title = mActivity.getString(R.string.contacts_permission);
                    Message = mActivity.getString(R.string.read_contacts_permission_message);
                    break;
                case Manifest.permission.WRITE_CONTACTS:
                    title = mActivity.getString(R.string.contacts_permission);
                    Message = mActivity.getString(R.string.write_contacts_permission_message);
                    break;
                case Manifest.permission.VIBRATE:
                    title = mActivity.getString(R.string.vibrate_permission);
                    Message = mActivity.getString(R.string.vibrate_permission_message);
                    break;
                case Manifest.permission.RECEIVE_SMS:
                    title = mActivity.getString(R.string.receive_sms_permission);
                    Message = mActivity.getString(R.string.receive_sms_permission_message);
                    break;

                case Manifest.permission.READ_SMS:
                    title = mActivity.getString(R.string.read_sms_permission);
                    Message = mActivity.getString(R.string.read_sms_permission_message);
                    break;
                case Manifest.permission.CALL_PHONE:
                    title = mActivity.getString(R.string.call_phone_permission);
                    Message = mActivity.getString(R.string.call_phone_permission_message);
                    break;
                case Manifest.permission.GET_ACCOUNTS:
                    title = mActivity.getString(R.string.get_accounts_permission);
                    Message = mActivity.getString(R.string.get_accounts_permission_message);
                    break;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setTitle(title);
            builder.setMessage(Message);

            builder.setPositiveButton(mActivity.getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", mActivity.getPackageName(), null);
                    intent.setData(uri);
                    mActivity.startActivityForResult(intent, AppConstants.PERMISSION_REQUEST_CODE);
                }
            });
            builder.setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
        } else {

            ActivityCompat.requestPermissions(mActivity, new String[]{permission}, AppConstants.PERMISSION_REQUEST_CODE);
        }
    }
}
