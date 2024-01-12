package ly.pp.justpiano3.adapter;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLFamily;
import ly.pp.justpiano3.enums.FamilyPositionEnum;

public final class FamilyUserAdapter extends BaseAdapter {
    private final List<Map<String, String>> list;
    private final LayoutInflater layoutInflater;
    private final OLFamily olFamily;

    public FamilyUserAdapter(List<Map<String, String>> list, LayoutInflater layoutInflater, OLFamily olFamily) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        this.olFamily = olFamily;
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
            view = layoutInflater.inflate(R.layout.ol_c_family_view, null);
        }
        final String userName = list.get(i).get("N");
        if (userName == null) {
            return view;
        }
        TextView positionTextView = view.findViewById(R.id.ol_family_position);
        TextView userNameTextView = view.findViewById(R.id.ol_family_name);
        TextView contributionTextView = view.findViewById(R.id.ol_family_contribution);
        TextView lvTextView = view.findViewById(R.id.ol_family_count);
        TextView dateTextView = view.findViewById(R.id.ol_family_date);
        ((ImageView) view.findViewById(R.id.ol_family_pic)).setImageResource(
                Objects.equals(list.get(i).get("S"), "m") ? R.drawable.m : R.drawable.f);
        lvTextView.setText("Lv." + list.get(i).get("L"));
        userNameTextView.setText(userName);
        dateTextView.setText(list.get(i).get("D"));
        contributionTextView.setText(list.get(i).get("C"));
        FamilyPositionEnum userPosition = FamilyPositionEnum.ofCode(list.get(i).get("P"), FamilyPositionEnum.NOT_IN_FAMILY);
        switch (userPosition) {
            case LEADER -> positionTextView.setText("族长");
            case VICE_LEADER -> positionTextView.setText("副族长");
            case MEMBER -> positionTextView.setText("族员");
            default -> positionTextView.setText("");
        }
        if (Objects.equals(list.get(i).get("O"), "0")) {
            userNameTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            positionTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            contributionTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            lvTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            dateTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
        } else {
            userNameTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            positionTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            contributionTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            lvTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            dateTextView.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
        }
        LinearLayout linearLayout = view.findViewById(R.id.ol_family_people);
        linearLayout.setOnClickListener(v -> {
            PopupWindow userInfoPopupWindow = olFamily.loadUserInfoPopupWindow(userName, userPosition);
            int[] iArr = new int[2];
            linearLayout.getLocationOnScreen(iArr);
            userInfoPopupWindow.showAtLocation(linearLayout, Gravity.TOP | Gravity.START,
                    iArr[0] + linearLayout.getWidth() / 2, iArr[1]);
        });
        return view;
    }
}
