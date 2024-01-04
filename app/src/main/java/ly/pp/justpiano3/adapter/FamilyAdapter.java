package ly.pp.justpiano3.adapter;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLFamily;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.utils.ImageLoadUtil;
import ly.pp.justpiano3.utils.ThreadPoolUtil;

public final class FamilyAdapter extends BaseAdapter {

    private final OLPlayHallRoom olPlayHallRoom;
    private List<Map<String, Object>> list;
    private final LayoutInflater layoutInflater;

    public FamilyAdapter(List<Map<String, Object>> list, LayoutInflater layoutInflater, OLPlayHallRoom olPlayHallRoom) {
        this.list = list;
        this.layoutInflater = layoutInflater;
        this.olPlayHallRoom = olPlayHallRoom;
    }

    public void upDateList(List<Map<String, Object>> list) {
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
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_family_view, null);
        }
        if (list.size() == 0) {
            return view;
        }
        String name = (String) list.get(i).get("N");
        if (name == null) {
            return view;
        }
        String contribution = (String) list.get(i).get("C");
        String id = (String) list.get(i).get("I");
        String count = list.get(i).get("U") + "/" + list.get(i).get("T");
        TextView positionText = view.findViewById(R.id.ol_family_position);
        TextView nameText = view.findViewById(R.id.ol_family_name);
        TextView contributionText = view.findViewById(R.id.ol_family_contribution);
        TextView countText = view.findViewById(R.id.ol_family_count);
        countText.setText(count);
        nameText.setText(name);
        contributionText.setText(contribution);
        String position = (String) list.get(i).get("P");
        positionText.setText(position);
        ImageView img = view.findViewById(R.id.ol_family_pic);
        if (position == null || position.equals("0")) {
            img.setImageResource(R.drawable.null_pic);
        }
        view.setOnClickListener(v -> {
            Intent intent = new Intent(olPlayHallRoom, OLFamily.class);
            intent.putExtra("familyID", id);
            intent.putExtra("position", i);
            intent.putExtra("pageNum", olPlayHallRoom.familyPageNum);
            intent.putExtra("myFamilyPosition", olPlayHallRoom.myFamilyPosition.getText().toString());
            intent.putExtra("myFamilyContribution", olPlayHallRoom.myFamilyContribution.getText().toString());
            intent.putExtra("myFamilyCount", olPlayHallRoom.myFamilyCount.getText().toString());
            intent.putExtra("myFamilyName", olPlayHallRoom.myFamilyName.getText().toString());
            intent.putExtra("myFamilyPicArray", olPlayHallRoom.myFamilyPicArray);
            intent.putExtra("familyList", (Serializable) olPlayHallRoom.familyList);
            olPlayHallRoom.startActivity(intent);
            olPlayHallRoom.finish();
        });
        byte[] pic = ((byte[]) list.get(i).get("J"));
        if (pic == null || pic.length <= 1) {
            ImageLoadUtil.familyBitmapCacheMap.put(id, null);
            img.setImageResource(R.drawable.family);
        } else {
            Bitmap familyBitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            img.setImageBitmap(familyBitmap);
            ImageLoadUtil.familyBitmapCacheMap.put(id, familyBitmap);
            ThreadPoolUtil.execute(() -> {
                File file1 = new File(olPlayHallRoom.getFilesDir(), id + ".webp");
                try {
                    if (!file1.exists()) {
                        file1.createNewFile();
                    }
                    OutputStream outputStream1 = new FileOutputStream(file1);
                    outputStream1.write(pic);
                    outputStream1.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if (i >= 0 && i < Consts.challengePositionColor.length) {
            nameText.setTextColor(Consts.challengePositionColor[i]);
            contributionText.setTextColor(Consts.challengePositionColor[i]);
            countText.setTextColor(Consts.challengePositionColor[i]);
            positionText.setTextColor(Consts.challengePositionColor[i]);
        } else {
            nameText.setTextColor(Color.WHITE);
            contributionText.setTextColor(Color.WHITE);
            countText.setTextColor(Color.WHITE);
            positionText.setTextColor(Color.WHITE);
        }
        return view;
    }
}
