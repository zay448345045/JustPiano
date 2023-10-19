package ly.pp.justpiano3.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.*;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.*;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.utils.UnitConvertUtil;

public final class JPDialogBuilder {
    private EditText editText;
    private GoldConvertView goldConvertView;
    private RadioGroup radioGroup;
    private View inflate;
    private int width = 360;
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

    public JPDialogBuilder(Context context) {
        this.context = context;
    }

    public JPDialogBuilder loadInflate(View view) {
        this.view = view;
        return this;
    }

    public JPDialogBuilder setWidth(int width) {
        this.width = width;
        return this;
    }

    public JPDialogBuilder setMessage(String str) {
        message = str;
        return this;
    }

    public JPDialogBuilder setFirstButton(String str, OnClickListener onClickListener) {
        positiveText = str;
        listener2 = onClickListener;
        return this;
    }

    public JPDialogBuilder setFirstButtonDisabled(boolean disabled) {
        positiveButtonDisabled = disabled;
        return this;
    }

    public void setCancelableFalse() {
        cancelable = false;
    }

    public JPDialog createJPDialog() {
        if (inflate == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflate = layoutInflater.inflate(R.layout.jpdialog, null);
        }
        JPDialog jpDialog = new JPDialog(context);
        jpDialog.addContentView(inflate, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
        ((TextView) inflate.findViewById(R.id.title)).setText(title);
        if (positiveText != null) {
            ((Button) inflate.findViewById(R.id.positiveButton)).setText(positiveText);
            inflate.findViewById(R.id.positiveButton).setEnabled(!positiveButtonDisabled);
            if (listener2 != null) {
                inflate.findViewById(R.id.positiveButton).setOnClickListener(v -> listener2.onClick(jpDialog, DialogInterface.BUTTON_POSITIVE));
            }
        } else {
            inflate.findViewById(R.id.positiveButton).setVisibility(View.GONE);
        }
        if (negativeText != null) {
            ((Button) inflate.findViewById(R.id.negativeButton)).setText(negativeText);
            if (listener != null) {
                inflate.findViewById(R.id.negativeButton).setOnClickListener(v -> listener.onClick(jpDialog, DialogInterface.BUTTON_NEGATIVE));
            }
        } else {
            inflate.findViewById(R.id.negativeButton).setVisibility(View.GONE);
        }
        if (message != null) {
            TextView messageTextView = inflate.findViewById(R.id.message);
            messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
            messageTextView.setLinksClickable(true);
            messageTextView.setLinkTextColor(Color.YELLOW);
            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View textView) {
                    // 获取点击的URL链接
                    TextView tv = (TextView) textView;
                    Spanned spanned = (Spanned) tv.getText();
                    int start = spanned.getSpanStart(this);
                    int end = spanned.getSpanEnd(this);
                    CharSequence url = spanned.subSequence(start, end);
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url.toString())));
                }
            };
            SpannableString spannableString = new SpannableString(message);
            URLSpan[] urlSpans = spannableString.getSpans(0, spannableString.length(), URLSpan.class);
            for (URLSpan urlSpan : urlSpans) {
                int start = spannableString.getSpanStart(urlSpan);
                int end = spannableString.getSpanEnd(urlSpan);
                spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            messageTextView.setText(spannableString);
        }
        if (view != null) {
            ((LinearLayout) inflate.findViewById(R.id.content)).removeAllViews();
            ((LinearLayout) inflate.findViewById(R.id.content)).addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
        jpDialog.setContentView(inflate);
        jpDialog.setCanceledOnTouchOutside(false);
        jpDialog.setCancelable(cancelable);
        cancelable = true;
        return jpDialog;
    }

    public JPDialogBuilder setTitle(String str) {
        title = str;
        return this;
    }

    public void setVisibleEditText(boolean visibleEditText, String hint) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.jpdialog, null);
        if (visibleEditText) {
            editText = (inflate.findViewById(R.id.Etext));
            editText.setVisibility(View.VISIBLE);
            editText.setHint(hint);
        }
    }

    public void setVisibleRadioGroup(boolean visibleRadioGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.jpdialog, null);
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

    public GoldConvertView getGoldConvertView() {
        return goldConvertView;
    }

    public JPDialogBuilder setSecondButton(String str, OnClickListener onClickListener) {
        negativeText = str;
        listener = onClickListener;
        return this;
    }

    public void buildAndShowDialog() {
        try {
            JPDialog dialog = createJPDialog();
            if (!dialog.isShowing()) {
                Window window = dialog.getWindow();
                Context context = dialog.getContext();
                window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                focusNotAle(window);
                dialog.show();
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = UnitConvertUtil.dp2px(context, this.width);
                window.setAttributes(layoutParams);
                hideNavigationBar(window);
                clearFocusNotAle(window);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisibleGoldConvertView(boolean visible) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.jpdialog, null);
        if (visible) {
            goldConvertView = (inflate.findViewById(R.id.gold_convert));
            goldConvertView.setVisibility(View.VISIBLE);
        }
    }

    public static class JPDialog extends Dialog {

        JPDialog(Context context) {
            super(context, R.style.Dialog);
        }
    }

    public void hideNavigationBar(Window window) {
        // window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        window.getDecorView().setOnSystemUiVisibilityChangeListener(visibility -> {
            try {
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void focusNotAle(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public void clearFocusNotAle(Window window) {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
}