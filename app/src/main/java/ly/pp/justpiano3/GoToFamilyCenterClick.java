package ly.pp.justpiano3;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.OLFamily;

import java.io.Serializable;

final class GoToFamilyCenterClick implements OnClickListener {

    private final FamilyAdapter fa;
    private final String id;
    private final int position;

    GoToFamilyCenterClick(FamilyAdapter fa, String id, int position) {
        this.fa = fa;
        this.id = id;
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(fa.olPlayHallRoom, OLFamily.class);
        intent.putExtra("familyID", id);
        intent.putExtra("position", position);
        intent.putExtra("pageNum", fa.olPlayHallRoom.familyPageNum);
        intent.putExtra("myFamilyPosition", fa.olPlayHallRoom.myFamilyPosition.getText().toString());
        intent.putExtra("myFamilyContribution", fa.olPlayHallRoom.myFamilyContribution.getText().toString());
        intent.putExtra("myFamilyCount", fa.olPlayHallRoom.myFamilyCount.getText().toString());
        intent.putExtra("myFamilyName", fa.olPlayHallRoom.myFamilyName.getText().toString());
        intent.putExtra("myFamilyPicArray", fa.olPlayHallRoom.myFamilyPicArray);
        intent.putExtra("familyList", (Serializable) fa.olPlayHallRoom.familyList);
        fa.olPlayHallRoom.startActivity(intent);
        fa.olPlayHallRoom.finish();
    }
}
