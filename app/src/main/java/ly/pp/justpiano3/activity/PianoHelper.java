package ly.pp.justpiano3.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ViewFlipper;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.MainMode;
import ly.pp.justpiano3.constant.Consts;

public class PianoHelper extends Activity implements OnGestureListener {
    private GestureDetector gestureDetector = null;
    private ViewFlipper viewFlipper = null;

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.setClass(this, MainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.piano_helper);
        viewFlipper = findViewById(R.id.viewflipper);
        gestureDetector = new GestureDetector(this, this);
        for (int imageResource : Consts.helpPic) {
            ImageView imageView = new ImageView(this);
            imageView.setImageResource(imageResource);
            imageView.setScaleType(ScaleType.FIT_XY);
            viewFlipper.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }
        viewFlipper.setAutoStart(false);
        viewFlipper.setFlipInterval(30000);
        if (viewFlipper.isAutoStart() && !viewFlipper.isFlipping()) {
            viewFlipper.startFlipping();
        }
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        Animation loadAnimation;
        Animation loadAnimation2;
        if (motionEvent2.getX() - motionEvent.getX() > 120) {
            loadAnimation = AnimationUtils.loadAnimation(this, R.anim.push_right_in);
            loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.push_right_out);
            viewFlipper.setInAnimation(loadAnimation);
            viewFlipper.setOutAnimation(loadAnimation2);
            viewFlipper.showPrevious();
        } else if (motionEvent2.getX() - motionEvent.getX() < -120) {
            loadAnimation = AnimationUtils.loadAnimation(this, R.anim.push_left_in);
            loadAnimation2 = AnimationUtils.loadAnimation(this, R.anim.push_left_out);
            viewFlipper.setInAnimation(loadAnimation);
            viewFlipper.setOutAnimation(loadAnimation2);
            viewFlipper.showNext();
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        viewFlipper.stopFlipping();
        viewFlipper.setAutoStart(false);
        return gestureDetector.onTouchEvent(motionEvent);
    }
}
