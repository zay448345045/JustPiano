package ly.pp.justpiano3.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHallRoom;
import ly.pp.justpiano3.listener.GoToFamilyCenterClick;
import ly.pp.justpiano3.thread.ThreadPoolUtil;
import ly.pp.justpiano3.utils.ImageLoadUtil;

public final class FamilyAdapter extends BaseAdapter {
    public OLPlayHallRoom olPlayHallRoom;
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
        view.setOnClickListener(new GoToFamilyCenterClick(this, id, i));
        byte[] pic = ((byte[]) list.get(i).get("J"));
        if (pic == null || pic.length <= 1) {
            ImageLoadUtil.familyBitmapCacheMap.put(id, null);
            img.setImageResource(R.drawable.family);
        } else {
            Bitmap familyBitmap = BitmapFactory.decodeByteArray(pic, 0, pic.length);
            img.setImageBitmap(familyBitmap);
            ImageLoadUtil.familyBitmapCacheMap.put(id, familyBitmap);
            ThreadPoolUtil.execute(() -> {
                File file1 = new File(olPlayHallRoom.getFilesDir(), id + ".jpg");
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
        switch (i) {
            case 0:
                nameText.setTextColor(0xFFFFD700);
                contributionText.setTextColor(0xFFFFD700);
                countText.setTextColor(0xFFFFD700);
                positionText.setTextColor(0xFFFFD700);
                break;
            case 1:
                nameText.setTextColor(0xFFC0C0C0);
                contributionText.setTextColor(0xFFC0C0C0);
                countText.setTextColor(0xFFC0C0C0);
                positionText.setTextColor(0xFFC0C0C0);
                break;
            case 2:
                nameText.setTextColor(0xFFD2B48C);
                contributionText.setTextColor(0xFFD2B48C);
                countText.setTextColor(0xFFD2B48C);
                positionText.setTextColor(0xFFD2B48C);
                break;
            default:
                nameText.setTextColor(0xFFFFFFFF);
                contributionText.setTextColor(0xFFFFFFFF);
                countText.setTextColor(0xFFFFFFFF);
                positionText.setTextColor(0xFFFFFFFF);
        }
        return view;
    }
}
