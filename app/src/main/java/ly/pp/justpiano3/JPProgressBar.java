package ly.pp.justpiano3;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;

public final class JPProgressBar extends ProgressDialog {

    private final boolean cancelWillOutline;
    private JPApplication jpApplication;
    private ConnectionService connectionService;

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (cancelWillOutline) {
            try {
                if (connectionService != null) {
                    connectionService.outLine();
                }
                if (jpApplication.getIsBindService()) {
                    jpApplication.unbindService(jpApplication.getServiceConnection());
                    jpApplication.setIsBindService(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override
    public void onStart() {
        super.onStart();
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
        imageView.setImageResource(R.drawable.animation);
        imageView.post(() -> {
            AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getDrawable();
            animationDrawable.setOneShot(false);
            animationDrawable.start();
        });
    }
}
