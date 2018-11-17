package lsit.texi.user.views;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import lsit.texi.user.R;
import lsit.texi.user.interfaces.IOneBtnDialogListener;
import lsit.texi.user.interfaces.ITwoBtnDialogListener;


/**
 * Created by Vikram on 10/26/17.
 */

public class CustomDialog extends Dialog {
   // private ApplicationGlobal applicationGlobal;
   // private Context _activity;

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);
    }

    public CustomDialog(Context activity) {
        super(activity);
        //Context _activity = activity;
       // ApplicationGlobal applicationGlobal = (ApplicationGlobal) activity.getApplicationContext();
    }

    public void displaySingleButtonDailog(String message, String buttonText, final IOneBtnDialogListener callback) {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setContentView(R.layout.single_button_dialog);
            setCancelable(false);
            TextView text = (TextView) findViewById(R.id.dailog_msg);
            text.setText(message);
            TextView dialogButtonOk = (TextView) findViewById(R.id.txt_ok);
            dialogButtonOk.setText(buttonText);
            dialogButtonOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onPositiveClickListener(CustomDialog.this);
                }
            });
            show();
        } catch (Exception e) {
            Log.e("Exception: ", e.getLocalizedMessage());
        }
    }

    public void displayTwoButtonsDialog(String message, String positiveButtonText, String negativeButtonText, final ITwoBtnDialogListener callback) {

        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setContentView(R.layout.two_buttons_dialog);
            setCancelable(false);
            TextView text = (TextView) findViewById(R.id.dailog_msg);
            text.setText(message);

            TextView dialogButtonOk = (TextView) findViewById(R.id.txt_ok);
            dialogButtonOk.setText(positiveButtonText);

            TextView dialogButtonCancel = (TextView) findViewById(R.id.txt_cancel);
            dialogButtonCancel.setText(negativeButtonText);

            dialogButtonOk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    callback.onPositiveClickListener(CustomDialog.this);
                }
            });

            dialogButtonCancel.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    callback.onNegativeClickListener(CustomDialog.this);
                }
            });
            show();
        } catch (Exception e) {
            Log.e("Exception: ", e.getLocalizedMessage());
        }
    }

    public void displayUiBlockingDialog() {
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            setContentView(R.layout.ui_alert_dialog);
            setCanceledOnTouchOutside(true);
            setCancelable(false);
            show();
        } catch (Exception e) {
            Log.e("Exception: ", e.getLocalizedMessage());
        }
    }

    public void stopUiBlocking() {
        dismiss();
    }


}



