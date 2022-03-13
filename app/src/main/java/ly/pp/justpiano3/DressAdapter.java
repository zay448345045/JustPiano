package ly.pp.justpiano3;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public final class DressAdapter extends BaseAdapter {
    private final List<Bitmap> list;
    private final LayoutInflater layoutInflater;
    private final OLPlayDressRoom olPlayDressRoom;
    private final int type;  // 0,1,2,3,4表示头发、眼睛，衣服、裤子、鞋子

    DressAdapter(List<Bitmap> arrayList, OLPlayDressRoom oLPlayDressRoom, int type) {
        this.olPlayDressRoom = oLPlayDressRoom;
        layoutInflater = oLPlayDressRoom.getLayoutInflater();
        list = arrayList;
        this.type = type;
    }

    @Override
    public final int getCount() {
        return list.size();
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_dres_view, null);
        }
        ((ImageView) view.findViewById(R.id.ol_dress_img)).setImageBitmap(list.get(i));
        switch (type) {
            case 0:
                if (olPlayDressRoom.hairUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    ((TextView) view.findViewById(R.id.ol_dress_price)).setText(olPlayDressRoom.sex.equals("f")
                            ? String.valueOf(Consts.fHair[i]) : String.valueOf(Consts.mHair[i]));
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.VISIBLE);
                    if (olPlayDressRoom.hairTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
                break;
            case 1:
                if (olPlayDressRoom.eyeUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    ((TextView) view.findViewById(R.id.ol_dress_price)).setText(olPlayDressRoom.sex.equals("f")
                            ? String.valueOf(Consts.fEye[i]) : String.valueOf(Consts.mEye[i]));
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.VISIBLE);
                    if (olPlayDressRoom.eyeTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
                break;
            case 2:
                if (olPlayDressRoom.jacketUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    ((TextView) view.findViewById(R.id.ol_dress_price)).setText(olPlayDressRoom.sex.equals("f")
                            ? String.valueOf(Consts.fJacket[i]) : String.valueOf(Consts.mJacket[i]));
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.VISIBLE);
                    if (olPlayDressRoom.jacketTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
                break;
            case 3:
                if (olPlayDressRoom.trousersUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    ((TextView) view.findViewById(R.id.ol_dress_price)).setText(olPlayDressRoom.sex.equals("f")
                            ? String.valueOf(Consts.fTrousers[i]) : String.valueOf(Consts.mTrousers[i]));
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.VISIBLE);
                    if (olPlayDressRoom.trousersTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
                break;
            case 4:
                if (olPlayDressRoom.shoesUnlock.contains(i) || i == 0) {
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.INVISIBLE);
                    view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_msg);
                } else {
                    ((TextView) view.findViewById(R.id.ol_dress_price)).setText(olPlayDressRoom.sex.equals("f")
                            ? String.valueOf(Consts.fShoes[i]) : String.valueOf(Consts.mShoes[i]));
                    view.findViewById(R.id.ol_dress_price).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.ol_dress_gold_image).setVisibility(View.VISIBLE);
                    if (olPlayDressRoom.shoesTry.contains(i)) {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar_red);
                    } else {
                        view.findViewById(R.id.ol_dress_img).setBackgroundResource(R.drawable.filled_bar);
                    }
                }
                break;
        }
        return view;
    }
}
