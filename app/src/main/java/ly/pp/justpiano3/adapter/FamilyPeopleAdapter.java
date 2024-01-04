package ly.pp.justpiano3.adapter;

import android.text.TextUtils;
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

public final class FamilyPeopleAdapter extends BaseAdapter {
    private final List<Map<String, String>> list;
    private final LayoutInflater layoutInflater;
    private final OLFamily olFamily;

    public FamilyPeopleAdapter(List<Map<String, String>> list, LayoutInflater layoutInflater, OLFamily olFamily) {
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
        final String name = list.get(i).get("N");
        if (name == null) {
            return view;
        }
        String loginDate = list.get(i).get("D");
        String contribution = list.get(i).get("C");
        String lv = list.get(i).get("L");
        if (!TextUtils.isEmpty(lv)) {
            lv = "Lv." + lv;
        }
        TextView positionText = view.findViewById(R.id.ol_family_position);
        TextView nameText = view.findViewById(R.id.ol_family_name);
        TextView contributionText = view.findViewById(R.id.ol_family_contribution);
        TextView lvText = view.findViewById(R.id.ol_family_count);
        TextView dateText = view.findViewById(R.id.ol_family_date);
        ImageView sex = view.findViewById(R.id.ol_family_pic);
        if (Objects.equals(list.get(i).get("S"), "m")) {
            sex.setImageResource(R.drawable.m);
        } else if (Objects.equals(list.get(i).get("S"), "f")) {
            sex.setImageResource(R.drawable.f);
        } else {
            sex.setImageResource(R.drawable.null_pic);
        }
        lvText.setText(lv);
        nameText.setText(name);
        dateText.setText(loginDate);
        contributionText.setText(contribution);

        FamilyPositionEnum userPosition = FamilyPositionEnum.ofCode(list.get(i).get("P"), FamilyPositionEnum.NOT_IN_FAMILY);
        switch (userPosition) {
            case LEADER -> positionText.setText("族长");
            case VICE_LEADER -> positionText.setText("副族长");
            case MEMBER -> positionText.setText("族员");
            default -> positionText.setText("");
        }
        if (Objects.equals(list.get(i).get("O"), "0")) {
            nameText.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            positionText.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            contributionText.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            lvText.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
            dateText.setTextColor(ContextCompat.getColor(olFamily, R.color.white));
        } else {
            nameText.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            positionText.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            contributionText.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            lvText.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
            dateText.setTextColor(ContextCompat.getColor(olFamily, R.color.white1));
        }

        final LinearLayout linearLayout = view.findViewById(R.id.ol_family_people);
        linearLayout.setOnClickListener(v -> {
            PopupWindow popupWindow = olFamily.loadInfoPopupWindow(name, userPosition);
            int[] iArr = new int[2];
            linearLayout.getLocationOnScreen(iArr);
            popupWindow.showAtLocation(linearLayout, Gravity.TOP | Gravity.START, iArr[0] + linearLayout.getWidth(), iArr[1]);
        });
        return view;
    }
}
