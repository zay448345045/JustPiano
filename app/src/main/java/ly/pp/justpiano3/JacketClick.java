package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

final class JacketClick implements OnItemClickListener {

    private final OLPlayDressRoom olplaydressroom;

    JacketClick(OLPlayDressRoom oLPlayDressRoom) {
        olplaydressroom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (i == olplaydressroom.jacketNow) {
            olplaydressroom.jacketImage.setImageBitmap(olplaydressroom.none);
            olplaydressroom.jacketNow = -1;
            return;
        }
        olplaydressroom.jacketImage.setImageBitmap(olplaydressroom.jacketArray.get(i));
        olplaydressroom.jacketNow = i;
    }
}
