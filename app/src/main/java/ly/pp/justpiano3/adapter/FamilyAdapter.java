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
        String familyId = (String) list.get(i).get("I");
        String count = list.get(i).get("U") + "/" + list.get(i).get("T");
        TextView positionTextView = view.findViewById(R.id.ol_family_position);
        TextView nameTextView = view.findViewById(R.id.ol_family_name);
        TextView contributionTextView = view.findViewById(R.id.ol_family_contribution);
        TextView countTextView = view.findViewById(R.id.ol_family_count);
        countTextView.setText(count);
        nameTextView.setText(name);
        contributionTextView.setText(contribution);
        String position = (String) list.get(i).get("P");
        positionTextView.setText(position);
        ImageView img = view.findViewById(R.id.ol_family_pic);
        if (position == null || position.equals("0")) {
            img.setImageResource(R.drawable.null_pic);
        }
        view.setOnClickListener(v -> {
            Intent intent = new Intent(olPlayHallRoom, OLFamily.class);
            intent.putExtra("familyID", familyId);
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
        if (pic == null || pic.length == 0) {
            ImageLoadUtil.familyBitmapCacheMap.put(familyId, null);
            img.setImageResource(R.drawable.family);
        } else {
            Bitmap familyBitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            img.setImageBitmap(familyBitmap);
            ImageLoadUtil.familyBitmapCacheMap.put(familyId, familyBitmap);
            ThreadPoolUtil.execute(() -> {
                File file = new File(olPlayHallRoom.getFilesDir(), familyId + ".webp");
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    OutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(pic);
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        if (i >= 0 && i < Consts.positionColor.length) {
            nameTextView.setTextColor(Consts.positionColor[i]);
            contributionTextView.setTextColor(Consts.positionColor[i]);
            countTextView.setTextColor(Consts.positionColor[i]);
            positionTextView.setTextColor(Consts.positionColor[i]);
        } else {
            nameTextView.setTextColor(Color.WHITE);
            contributionTextView.setTextColor(Color.WHITE);
            countTextView.setTextColor(Color.WHITE);
            positionTextView.setTextColor(Color.WHITE);
        }
        return view;
    }
}
