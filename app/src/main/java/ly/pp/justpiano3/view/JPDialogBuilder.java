package ly.pp.justpiano3.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.UnitConvertUtil;
import ly.pp.justpiano3.utils.WindowUtil;

public final class JPDialogBuilder {
    private EditText editText;
    private GoldConvertView goldConvertView;
    private RadioGroup radioGroup;
    private View inflate;
    private int width = 360;
    private final Context context;
    private OnClickListener firstButtonOnClickListener;
    private String title;
    private String message;
    private String positiveText;
    private String negativeText;
    private boolean positiveButtonDisabled;
    private View view;
    private boolean cancelable = true;
    private boolean checkMessageUrl;
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
        firstButtonOnClickListener = onClickListener;
        return this;
    }

    public JPDialogBuilder setFirstButtonDisabled(boolean disabled) {
        positiveButtonDisabled = disabled;
        return this;
    }

    public JPDialogBuilder setCheckMessageUrl(boolean checkMessageUrl) {
        this.checkMessageUrl = checkMessageUrl;
        return this;
    }

    public JPDialogBuilder setCancelableFalse() {
        cancelable = false;
        return this;
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
            if (firstButtonOnClickListener != null) {
                inflate.findViewById(R.id.positiveButton).setOnClickListener(v -> firstButtonOnClickListener.onClick(jpDialog, DialogInterface.BUTTON_POSITIVE));
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
            if (checkMessageUrl) {
                messageTextView.setAutoLinkMask(Linkify.WEB_URLS);
                messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
                messageTextView.setLinksClickable(true);
                messageTextView.setLinkTextColor(Color.YELLOW);
                ClickableSpan clickableSpan = new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View textView) {
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
            } else {
                messageTextView.setText(message);
            }
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

    public JPDialogBuilder setVisibleEditText(boolean visibleEditText, String hint) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.jpdialog, null);
        if (visibleEditText) {
            editText = (inflate.findViewById(R.id.Etext));
            editText.setVisibility(View.VISIBLE);
            editText.setHint(hint);
        }
        return this;
    }

    public JPDialogBuilder setVisibleRadioGroup(boolean visibleRadioGroup) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.jpdialog, null);
        if (visibleRadioGroup) {
            radioGroup = (inflate.findViewById(R.id.Rgroup));
            radioGroup.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public JPDialogBuilder addRadioButton(RadioButton radioButton) {
        radioGroup.addView(radioButton);
        return this;
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
                if (GlobalSetting.getAllFullScreenShow()) {
                    WindowUtil.fullScreenHandle(window);
                } else {
                    WindowUtil.exitFullScreenHandle(window);
                }
                dialog.show();
                if (GlobalSetting.getAllFullScreenShow()) {
                    WindowUtil.fullScreenHandle(window);
                } else {
                    WindowUtil.exitFullScreenHandle(window);
                }
                WindowManager.LayoutParams layoutParams = window.getAttributes();
                layoutParams.width = UnitConvertUtil.dp2px(dialog.getContext(), this.width);
                window.setAttributes(layoutParams);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JPDialogBuilder setVisibleGoldConvertView(boolean visible) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflate = layoutInflater.inflate(R.layout.jpdialog, null);
        if (visible) {
            goldConvertView = (inflate.findViewById(R.id.gold_convert));
            goldConvertView.setVisibility(View.VISIBLE);
        }
        return this;
    }

    public static class JPDialog extends Dialog {

        JPDialog(Context context) {
            super(context, R.style.Dialog);
        }
    }
}