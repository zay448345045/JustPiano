package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

final class RoomPasswordClick implements OnClickListener {
    private final MainGameAdapter mainGameAdapter;
    private final TextView textView;
    private final byte f5274c;

    RoomPasswordClick(MainGameAdapter mainGameAdapter, TextView textView, byte b) {
        this.mainGameAdapter = mainGameAdapter;
        this.textView = textView;
        f5274c = b;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(textView.getText());
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("I", f5274c);
            jSONObject.put("P", valueOf);
            mainGameAdapter.connectionService.writeData((byte) 29, (byte) 0, f5274c, jSONObject.toString(), null);
            dialogInterface.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
