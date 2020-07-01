package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

final class TrousersClick implements OnItemClickListener {

    private final OLPlayDressRoom olplaydressroom;

    TrousersClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (i == olplaydressroom.trousersNow) {
            olplaydressroom.trousersImage.setImageBitmap(olplaydressroom.none);
            olplaydressroom.trousersNow = -1;
            return;
        }
        olplaydressroom.trousersImage.setImageBitmap(olplaydressroom.trousersArray.get(i));
        olplaydressroom.trousersNow = i;
    }
}
