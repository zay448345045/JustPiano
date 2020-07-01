package ly.pp.justpiano3;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class JPDialog {
    EditText editText;
    private Context context;
    private OnClickListener listener2;
    private String title;
    private String message;
    private String positiveText;
    private String negativeText;
    private View view;
    private boolean visible;
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

    final void setCancelableFalse() {
        cancelable = false;
    }

    final JDialog createJDialog() {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JDialog jDialog = new JDialog(context);
        View inflate = layoutInflater.inflate(R.layout.mydialog, null);
        jDialog.addContentView(inflate, new LayoutParams(-1, -2));
        ((TextView) inflate.findViewById(R.id.title)).setText(title);
        if (positiveText != null) {
            ((Button) inflate.findViewById(R.id.positiveButton)).setText(positiveText);
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
        } else if (view != null) {
            ((LinearLayout) inflate.findViewById(R.id.content)).removeAllViews();
            ((LinearLayout) inflate.findViewById(R.id.content)).addView(view, new LayoutParams(-2, -2));
        }
        jDialog.setContentView(inflate);
        jDialog.setCanceledOnTouchOutside(false);
        jDialog.setCancelable(cancelable);
        cancelable = true;
        if (visible) {
            editText = (inflate.findViewById(R.id.Etext));
            (inflate.findViewById(R.id.Etext)).setVisibility(View.VISIBLE);
        }
        return jDialog;
    }

    public final JPDialog setTitle(String str) {
        title = str;
        return this;
    }

    final void setEditAndMessageVisable() {
        visible = true;
    }

    public final JPDialog setSecondButton(String str, OnClickListener onClickListener) {
        negativeText = str;
        listener = onClickListener;
        return this;
    }

    public final void showDialog() {
        try{
            JDialog dia = createJDialog();
            if (!dia.isShowing()) {
                dia.show();
            }
        }catch (Exception ignore){
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
}
