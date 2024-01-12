package ly.pp.justpiano3.adapter;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.Consts;

public final class MiniScoreAdapter extends BaseAdapter {
    private List<Bundle> list;
    private final LayoutInflater layoutInflater;
    private final int roomMode;

    public MiniScoreAdapter(List<Bundle> list, LayoutInflater layoutInflater, int roomMode) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        this.roomMode = roomMode;
    }

    public void changeList(List<Bundle> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        try {
            view = layoutInflater.inflate(R.layout.ol_play_score_view, null);
            String score = (String) list.get((byte) i).get("M");
            int hand = Integer.parseInt((String) Objects.requireNonNull(list.get((byte) i).get("G")));
            String userName = (String) list.get((byte) i).get("U");
            if (TextUtils.isEmpty(userName)) {
                view.setVisibility(View.GONE);
            } else {
                TextView userNameTextView = view.findViewById(R.id.ol_user_text);
                if (roomMode == 1 && hand > 0) {
                    userNameTextView.setBackgroundResource(Consts.originalRoomModeResource[hand - 1]);
                } else if (roomMode == 2 && hand > 0) {
                    userNameTextView.setBackgroundResource(Consts.teamRoomModeResource[hand - 1]);
                } else if (roomMode == 0) {
                    ((TextView) view.findViewById(R.id.ol_state_text)).setText(hand == 0 ? "右     " : "左     ");
                    userNameTextView.setBackgroundResource(Consts.originalRoomModeResource[0]);
                }
                userNameTextView.setText(userName);
                ((TextView) view.findViewById(R.id.ol_score_text)).setText(score);
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.setVisibility(View.GONE);
        }
        return view;
    }
}
