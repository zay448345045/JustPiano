package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

final class ShoesClick implements OnItemClickListener {
    private final OLPlayDressRoom olplaydressroom;

    ShoesClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (i == olplaydressroom.shoesNow) {
            olplaydressroom.shoesImage.setImageBitmap(olplaydressroom.none);
            olplaydressroom.shoesNow = -1;
            return;
        }
        olplaydressroom.shoesImage.setImageBitmap(olplaydressroom.shoesArray.get(i));
        olplaydressroom.shoesNow = i;
    }
}
