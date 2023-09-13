package ly.pp.justpiano3.view;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.service.ConnectionService;

public final class JPProgressBar extends ProgressDialog {

    private final boolean cancelWillOutline;
    private JPApplication jpApplication;
    private ConnectionService connectionService;
    private String text;

    public JPProgressBar(Context context) {
        super(context);
        cancelWillOutline = false;
    }

    public JPProgressBar(Context context, JPApplication jPApplication) {
        super(context);
        cancelWillOutline = true;
        jpApplication = jPApplication;
        connectionService = jPApplication.getConnectionService();
    }

    public void setText(String text) {
        this.text = text;
        final TextView textView = findViewById(R.id.loading_text);
        if (!StringUtil.isNullOrEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        textView.invalidate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (cancelWillOutline) {
            try {
                if (connectionService != null) {
                    connectionService.outLine();
                }
                if (jpApplication.isBindService()) {
                    jpApplication.unbindService(jpApplication.getServiceConnection());
                    jpApplication.setBindService(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void show() {
        super.show();
        setContentView(R.layout.prgdialog);
        Window window = getWindow();
        LayoutParams attributes = window.getAttributes();
        attributes.dimAmount = 0.5f;
        window.setAttributes(attributes);
        final ImageView imageView = findViewById(R.id.loading_img);
        final TextView textView = findViewById(R.id.loading_text);
        if (!StringUtil.isNullOrEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        imageView.setImageResource(R.drawable.animation);
        imageView.post(() -> {
            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.setOneShot(false);
            animationDrawable.start();
        });
    }
}
