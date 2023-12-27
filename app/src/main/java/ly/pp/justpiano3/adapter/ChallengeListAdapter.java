package ly.pp.justpiano3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;

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
        switch (i) {
            case 0 -> {
                nameText.setTextColor(0xFFFFD700);
                scoreText.setTextColor(0xFFFFD700);
                timeText.setTextColor(0xFFFFD700);
                positionText.setTextColor(0xFFFFD700);
            }
            case 1 -> {
                nameText.setTextColor(0xFFC0C0C0);
                scoreText.setTextColor(0xFFC0C0C0);
                timeText.setTextColor(0xFFC0C0C0);
                positionText.setTextColor(0xFFC0C0C0);
            }
            case 2 -> {
                nameText.setTextColor(0xFFD2B48C);
                scoreText.setTextColor(0xFFD2B48C);
                timeText.setTextColor(0xFFD2B48C);
                positionText.setTextColor(0xFFD2B48C);
            }
            default -> {
                nameText.setTextColor(0xFFFFFFFF);
                scoreText.setTextColor(0xFFFFFFFF);
                timeText.setTextColor(0xFFFFFFFF);
                positionText.setTextColor(0xFFFFFFFF);
            }
        }
        return view;
    }
}
