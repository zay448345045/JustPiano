package ly.pp.justpiano3;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

final class HairClick implements OnItemClickListener {
    private final OLPlayDressRoom olPlayDressRoom;

    HairClick(OLPlayDressRoom oLPlayDressRoom) {
        olPlayDressRoom = oLPlayDressRoom;
    }

    @Override
    public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
        if (i == olPlayDressRoom.hairNow) {
            olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.none);
            olPlayDressRoom.hairNow = -1;
            return;
        }
        olPlayDressRoom.hairImage.setImageBitmap(olPlayDressRoom.hairArray.get(i));
        olPlayDressRoom.hairNow = i;
    }
}
