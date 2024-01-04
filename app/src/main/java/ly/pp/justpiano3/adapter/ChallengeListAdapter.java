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
        String score = list.get(i).get("S");
        String time = list.get(i).get("T");
        String position = list.get(i).get("P");
        TextView positionText = view.findViewById(R.id.ol_challenge_position);
        TextView nameText = view.findViewById(R.id.ol_challenge_user);
        TextView scoreText = view.findViewById(R.id.ol_challenge_score);
        TextView timeText = view.findViewById(R.id.ol_challenge_time);
        nameText.setText(name);
        scoreText.setText(score);
        timeText.setText(time);
        positionText.setText(position);
        if (i >= 0 && i < Consts.challengePositionColor.length) {
            nameText.setTextColor(Consts.challengePositionColor[i]);
            scoreText.setTextColor(Consts.challengePositionColor[i]);
            timeText.setTextColor(Consts.challengePositionColor[i]);
            positionText.setTextColor(Consts.challengePositionColor[i]);
        } else {
            nameText.setTextColor(Color.WHITE);
            scoreText.setTextColor(Color.WHITE);
            timeText.setTextColor(Color.WHITE);
            positionText.setTextColor(Color.WHITE);
        }
        return view;
    }
}
