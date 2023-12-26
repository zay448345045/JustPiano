package ly.pp.justpiano3.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLPlayDressRoom;

public final class DressAdapter extends BaseAdapter {
    private final List<Bitmap> list;
    private final LayoutInflater layoutInflater;
    private final OLPlayDressRoom olPlayDressRoom;
    private final int type;  // 0,1,2,3,4表示头发、眼睛，衣服、裤子、鞋子

    public DressAdapter(List<Bitmap> arrayList, OLPlayDressRoom oLPlayDressRoom, int type) {
        this.olPlayDressRoom = oLPlayDressRoom;
        layoutInflater = oLPlayDressRoom.getLayoutInflater();
        list = arrayList;
        this.type = type;
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
            view = layoutInflater.inflate(R.layout.ol_dressroom_cost_view, null);
        }
        ((ImageView) view.findViewById(R.id.ol_dress_img)).setImageBitmap(list.get(i));
        boolean isFemale = "f".equals(olPlayDressRoom.sex);
        switch (type) {
            case 0 -> {
                if (olPlayDressRoom.hairUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    int[] arrays = isFemale ? OLPlayDressRoom.fHair : OLPlayDressRoom.mHair;
                    setPrice(i, view, arrays);
                    if (olPlayDressRoom.hairTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
            }
            case 1 -> {
                if (olPlayDressRoom.eyeUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    int[] arrays = isFemale ? OLPlayDressRoom.fEye : OLPlayDressRoom.mEye;
                    setPrice(i, view, arrays);
                    if (olPlayDressRoom.eyeTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
            }
            case 2 -> {
                if (olPlayDressRoom.jacketUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    int[] arrays = isFemale ? OLPlayDressRoom.fJacket : OLPlayDressRoom.mJacket;
                    setPrice(i, view, arrays);
                    if (olPlayDressRoom.jacketTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
            }
            case 3 -> {
                if (olPlayDressRoom.trousersUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    int[] arrays = isFemale ? OLPlayDressRoom.fTrousers : OLPlayDressRoom.mTrousers;
                    setPrice(i, view, arrays);
                    if (olPlayDressRoom.trousersTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
            }
            case 4 -> {
                if (olPlayDressRoom.shoesUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    int[] arrays = isFemale ? OLPlayDressRoom.fShoes : OLPlayDressRoom.mShoes;
                    setPrice(i, view, arrays);
                    if (olPlayDressRoom.shoesTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
            }
        }
        return view;
    }

    private void setPrice(int index, View view, int[] price) {
        if (price.length - 1 >= index) {
            ((TextView) view.findViewById(R.id.ol_dress_price)).setText(String.valueOf(price[index]));
        } else {
            ((TextView) view.findViewById(R.id.ol_dress_price)).setText(R.string.finish_infinity);
        }
        view.findViewById(R.id.ol_dress_price).setVisibility(View.VISIBLE);
        view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.VISIBLE);
    }
}
