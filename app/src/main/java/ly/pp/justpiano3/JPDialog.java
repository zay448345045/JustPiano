package ly.pp.justpiano3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

public final class JPDialog {
    private EditText editText;
    private RadioGroup radioGroup;
    private View inflate;
    private final Context context;
    private OnClickListener listener2;
    private String title;
    private String message;
    private String positiveText;
    private String negativeText;
    private boolean positiveButtonDisabled;
    private View view;
    private boolean cancelable = true;
    private OnClickListener listener;

    public JPDialog(Context context) {
        this.context = context;
    }

    public final JPDialog loadInflate(View view) {
        this.view = view;
        return this;
    }

    public final JPDialog setMessage(String str) {
        message = str;
        return this;
    }

    public final JPDialog setFirstButton(String str, OnClickListener onClickListener) {
        positiveText = str;
        listener2 = onClickListener;
        return this;
    }

    public final JPDialog setFirstButtonDisabled(boolean disabled) {
        positiveButtonDisabled = disabled;
        return this;
    }

    final void setCancelableFalse() {
        cancelable = false;
    }

    final JDialog createJDialog() {
        if (inflate == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflate = layoutInflater.inflate(R.layout.mydialog, null);
        }
        JDialog jDialog = new JDialog(context);
        jDialog.addContentView(inflate, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ((TextView) inflate.findViewById(R.id.title)).setText(title);
        if (positiveText != null) {
            ((Button) inflate.findViewById(R.id.positiveButton)).setText(positiveText);
            inflate.findViewById(R.id.positiveButton).setEnabled(!positiveButtonDisabled);
            if (listener2 != null) {
                inflate.findViewById(R.id.positiveButton).setOnClickListener(v -> listener2.onClick(jDialog, -1));
            }
        } else {
            inflate.findViewById(R.id.positiveButton).setVisibility(View.GONE);
        }
        if (negativeText != null) {
            ((Button) inflate.findViewById(R.id.negativeButton)).setText(negativeText);
            if (listener != null) {
                inflate.findViewById(R.id.negativeButton).setOnClickListener(v -> listener.onClick(jDialog, -2));
            }
        } else {
            inflate.findViewById(R.id.negativeButton).setVisibility(View.GONE);
        }
        if (message != null) {
            ((TextView) inflate.findViewById(R.id.message)).setText(message);
        }
        if (view != null) {
            ((LinearLayout) inflate.findViewById(R.id.content)).removeAllViews();
            ((LinearLayout) inflate.findViewById(R.id.content)).addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        jDialog.setContentView(inflate);
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(cancelable);
        cancelable = true;
        return jDialog;
    }

    public final JPDialog setTitle(String str) {
        title = str;
        return this;
    }

    public void setVisibleEditText(boolean visibleEditText) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.mydialog, null);
        if (visibleEditText) {
            editText = (inflate.findViewById(R.id.Etext));
            editText.setVisibility(View.VISIBLE);
        }
    }

    public void setVisibleRadioGroup(boolean visibleRadioGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.mydialog, null);
        if (visibleRadioGroup) {
            radioGroup = (inflate.findViewById(R.id.Rgroup));
            radioGroup.setVisibility(View.VISIBLE);
        }
    }

    public void addRadioButton(RadioButton radioButton) {
        radioGroup.addView(radioButton);
    }

    public int getRadioGroupCheckedId() {
        if (radioGroup != null) {
            int id = radioGroup.getCheckedRadioButtonId();
            if (id == -1) {
                return id;
            }
            View v = inflate.findViewById(id);
            return (int) v.getTag();
        } else {
            return -1;
        }
    }

    public String getEditTextString() {
        return editText.getText().toString();
    }

    public JPDialog setSecondButton(String str, OnClickListener onClickListener) {
        negativeText = str;
        listener = onClickListener;
        return this;
    }

    public void showDialog() {
        try {
            JDialog dia = createJDialog();
            if (!dia.isShowing()) {
                Window window = dia.getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                focusNotAle(window);
                dia.show();
                hideNavigationBar(window);
                clearFocusNotAle(window);
            }
        } catch (Exception ignore) {
        }
    }

    static class JDialog extends Dialog {

        JDialog(Context context) {
            super(context, R.style.Dialog);
        }

        @Override
        public final void setCancelable(boolean z) {
            super.setCancelable(z);
        }
    }

    public void hideNavigationBar(Window window) {
 //     window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    //布局位于状态栏下方
 //                 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                    //全屏
                    View.SYSTEM_UI_FLAG_FULLSCREEN |
                    //隐藏导航栏
 //                 View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            window.getDecorView().setSystemUiVisibility(uiOptions);
        });
    }

    public void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
}