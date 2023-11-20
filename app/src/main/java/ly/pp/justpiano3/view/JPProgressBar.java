package ly.pp.justpiano3.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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

    public void addClickableLink(String keyword, Runnable runnable) {
        // 创建一个SpannableString对象，用于设置超链接
        SpannableString spannableString = new SpannableString(text);
        // 设置超链接的点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View view) {
                runnable.run();
            }
        };
        // 设置超链接的样式和范围
        int startIndex = text.indexOf(keyword);
        int endIndex = startIndex + keyword.length();
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将TextView设置为可点击，才能响应超链接的点击事件
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setLinkTextColor(Color.YELLOW);
        // 将SpannableString设置给TextView
        textView.setText(spannableString);
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
