package ly.pp.justpiano3.listener;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import ly.pp.justpiano3.activity.OLFamily;
import ly.pp.justpiano3.adapter.FamilyAdapter;

import java.io.Serializable;

public final class GoToFamilyCenterClick implements OnClickListener {

    private final FamilyAdapter familyAdapter;
    private final String id;
    private final int position;

    public GoToFamilyCenterClick(FamilyAdapter familyAdapter, String id, int position) {
        this.familyAdapter = familyAdapter;
        this.id = id;
        this.position = position;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(familyAdapter.olPlayHallRoom, OLFamily.class);
        intent.putExtra("familyID", id);
        intent.putExtra("position", position);
        intent.putExtra("pageNum", familyAdapter.olPlayHallRoom.familyPageNum);
        intent.putExtra("myFamilyPosition", familyAdapter.olPlayHallRoom.myFamilyPosition.getText().toString());
        intent.putExtra("myFamilyContribution", familyAdapter.olPlayHallRoom.myFamilyContribution.getText().toString());
        intent.putExtra("myFamilyCount", familyAdapter.olPlayHallRoom.myFamilyCount.getText().toString());
        intent.putExtra("myFamilyName", familyAdapter.olPlayHallRoom.myFamilyName.getText().toString());
        intent.putExtra("myFamilyPicArray", familyAdapter.olPlayHallRoom.myFamilyPicArray);
        intent.putExtra("familyList", (Serializable) familyAdapter.olPlayHallRoom.familyList);
        familyAdapter.olPlayHallRoom.startActivity(intent);
        familyAdapter.olPlayHallRoom.finish();
    }
}
