package ly.pp.justpiano3.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.Consts;

public final class ChallengeListAdapter extends BaseAdapter {
    private final List<Map<String, String>> list;
    private final LayoutInflater layoutInflater;

    public ChallengeListAdapter(List<Map<String, String>> list, LayoutInflater layoutInflater) {
        this.list = list;
        this.layoutInflater = layoutInflater;
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_challenge_ranking_view, null);
        }
        String name = list.get(i).get("N");
        if (name == null) {
            return view;
        }
        TextView positionTextView = view.findViewById(R.id.ol_challenge_position);
        TextView userNameTextView = view.findViewById(R.id.ol_challenge_user);
        TextView scoreTextView = view.findViewById(R.id.ol_challenge_score);
        TextView timeTextView = view.findViewById(R.id.ol_challenge_time);
        userNameTextView.setText(name);
        scoreTextView.setText(list.get(i).get("S"));
        timeTextView.setText(list.get(i).get("T"));
        positionTextView.setText(list.get(i).get("P"));
        if (i >= 0 && i < Consts.positionColor.length) {
            positionTextView.setTextColor(Consts.positionColor[i]);
            userNameTextView.setTextColor(Consts.positionColor[i]);
            scoreTextView.setTextColor(Consts.positionColor[i]);
            timeTextView.setTextColor(Consts.positionColor[i]);
        } else {
            positionTextView.setTextColor(Color.WHITE);
            userNameTextView.setTextColor(Color.WHITE);
            scoreTextView.setTextColor(Color.WHITE);
            timeTextView.setTextColor(Color.WHITE);
        }
        return view;
    }
}
