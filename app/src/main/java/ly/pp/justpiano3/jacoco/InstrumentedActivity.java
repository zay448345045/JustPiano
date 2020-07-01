package ly.pp.justpiano3.jacoco;

import android.util.Log;

import ly.pp.justpiano3.JustPiano;


public class InstrumentedActivity extends JustPiano {
    public static String TAG = "InstrumentedActivity";

    private FinishListener mListener;

    public void setFinishListener(FinishListener listener) {
        mListener = listener;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG , "Activity onDestroy()");
        super.finish();
        if (mListener != null) {
            mListener.onActivityFinished();
        }
    }

}