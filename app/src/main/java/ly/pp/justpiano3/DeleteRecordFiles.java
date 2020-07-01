package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

final class DeleteRecordFiles implements OnClickListener {
    private final RecordFiles recordfiles;
    private final int f5798b;
    private final String f5799c;

    DeleteRecordFiles(RecordFiles rs, int i, String str) {
        recordfiles = rs;
        f5798b = i;
        f5799c = str;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        recordfiles.remove(f5798b, f5799c);
    }
}
