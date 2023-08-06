package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import ly.pp.justpiano3.activity.MelodySelect;

public final class DoNotShowDialogClick implements OnClickListener {
    private final MelodySelect melodySelect;
    private final int f5160b;

    public DoNotShowDialogClick(MelodySelect melodySelect, int i) {
        this.melodySelect = melodySelect;
        f5160b = i;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        Editor edit = melodySelect.sharedPreferences.edit();
        switch (f5160b) {
            case 0:
                edit.putBoolean("record_dialog", false);
                break;
            case 1:
                edit.putBoolean("play_dialog", false);
                break;
            case 2:
                edit.putBoolean("hand_dialog", false);
                break;
        }
        edit.apply();
    }
}
