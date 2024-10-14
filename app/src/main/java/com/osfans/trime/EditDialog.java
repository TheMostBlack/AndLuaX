package com.osfans.trime;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;


/**
 * Created by nirenr on 2019/6/25.
 */

public class EditDialog implements DialogInterface.OnClickListener {
    private final Context mContext;
    private final EditText mEdit;
    private final EditDialogCallback mCallback;
    private AlertDialog dlg;
    private int mMax;

    public EditDialog(Context context, final String title, String def, EditDialogCallback callback) {
        mEdit = new EditText(context) {
            @Override
            protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
                super.onTextChanged(text, start, lengthBefore, lengthAfter);
                if (dlg != null && mMax > 100)
                    dlg.setTitle(title + " " + text.length() + "/" + mMax);
            }
        };
        mContext = context;
        mCallback = callback;
        dlg = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(mEdit)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, this)
                .setCancelable(false)
                .create();
        Window win = dlg.getWindow();
        if (win != null)
            win.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        mEdit.setText(def);
    }

    public void show() {
        dlg.show();
        mEdit.setFocusable(true);
        mEdit.requestFocus();
    }

    public void show(int max) {
        mMax = max;
        mEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
        dlg.show();
        mEdit.setFocusable(true);
        mEdit.requestFocus();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        mCallback.onCallback(mEdit.getText().toString());
    }

    public interface EditDialogCallback {
        public void onCallback(String text);
    }
}
