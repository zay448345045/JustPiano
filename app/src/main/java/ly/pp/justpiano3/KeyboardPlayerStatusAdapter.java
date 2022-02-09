package ly.pp.justpiano3;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public final class KeyboardPlayerStatusAdapter extends BaseAdapter {
    private final OLPlayKeyboardRoom olPlayKeyboardRoom;

    KeyboardPlayerStatusAdapter(OLPlayKeyboardRoom olPlayKeyboardRoom) {
        this.olPlayKeyboardRoom = olPlayKeyboardRoom;
    }

    @Override
    public final int getCount() {
        return olPlayKeyboardRoom.olKeyboardStates.length;
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = olPlayKeyboardRoom.getLayoutInflater().inflate(R.layout.ol_keyboard_state_view, null);
        }
        ImageView keyboardMute = view.findViewById(R.id.keyboard_mute);
        keyboardMute.setBackgroundResource(olPlayKeyboardRoom.olKeyboardStates[i].isMuted() ? R.color.black : R.color.brown);
        keyboardMute.setOnClickListener(v -> {
            olPlayKeyboardRoom.olKeyboardStates[i].setMuted(!olPlayKeyboardRoom.olKeyboardStates[i].isMuted());
            v.setBackgroundResource(olPlayKeyboardRoom.olKeyboardStates[i].isMuted() ? R.color.black : R.color.brown);
            notifyDataSetChanged();
        });
        ImageView keyboardOn = view.findViewById(R.id.midi_keyboard_on);
        keyboardOn.setVisibility(olPlayKeyboardRoom.olKeyboardStates[i].isMidiKeyboardOn() ? View.VISIBLE : View.INVISIBLE);
        ProgressBar speedProgressBar = view.findViewById(R.id.midi_note_progress);
        TextView speed = view.findViewById(R.id.midi_note_speed);
        int speedValue = olPlayKeyboardRoom.olKeyboardStates[i].getSpeed();
        speedProgressBar.setProgress(speedValue);
        speed.setText(String.format("%.2f", speedValue / 24f) + "kb/s");
        return view;
    }
}
