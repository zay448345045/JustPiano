package ly.pp.justpiano3.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.R;

public final class JPProgressBar extends Dialog {

    private final ImageView imageView;
    private final TextView textView;
    private String text;

    public JPProgressBar(Context context) {
        super(context, R.style.Dialog);
        setContentView(R.layout.loading_view);
        imageView = findViewById(R.id.loading_img);
        textView = findViewById(R.id.loading_text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (!StringUtil.isNullOrEmpty(text)) {
            textView.setText(text);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        textView.postInvalidate();
    }

    @Override
    public void show() {
        super.show();
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
