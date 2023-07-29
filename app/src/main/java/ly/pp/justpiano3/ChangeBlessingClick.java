package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import protobuf.dto.OnlineSendMailDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

final class ChangeBlessingClick implements OnClickListener {

    private final OLPlayHallRoom olPlayHallRoom;
    private final TextView f5458b;
    private final int f5459c;
    private final String f5460d;

    ChangeBlessingClick(OLPlayHallRoom olPlayHallRoom, TextView textView, int i, String str) {
        this.olPlayHallRoom = olPlayHallRoom;
        f5458b = textView;
        f5459c = i;
        f5460d = str;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        String valueOf = String.valueOf(f5458b.getText());
        if (valueOf.isEmpty() || valueOf.equals("'")) {
            Toast.makeText(olPlayHallRoom, "请输入内容!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > 500) {
            Toast.makeText(olPlayHallRoom, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else if (f5459c == 0 && !f5460d.isEmpty()) {
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setMessage(valueOf);
            builder.setName(f5460d);
            olPlayHallRoom.connectionService.writeData(35, builder.build());
            Toast.makeText(olPlayHallRoom, "发送成功!", Toast.LENGTH_SHORT).show();
            olPlayHallRoom.mailList.clear();
            String format = SimpleDateFormat.getDateInstance(2, Locale.CHINESE).format(new Date());
            Bundle bundle = new Bundle();
            bundle.putString("F", f5460d);
            bundle.putString("M", valueOf);
            bundle.putString("T", format);
            bundle.putInt("type", 1);
            olPlayHallRoom.mailList.add(bundle);
            format = olPlayHallRoom.sharedPreferences.getString("mailsString", "[]");
            try {
                JSONArray jSONArray = new JSONArray(format);
                int length = jSONArray.length();
                for (int i2 = 0; i2 < length; i2++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i2);
                    Bundle bundle2 = new Bundle();
                    if (jSONObject2.has("type")) {
                        bundle2.putInt("type", jSONObject2.getInt("type"));
                    }
                    bundle2.putString("F", jSONObject2.getString("F"));
                    bundle2.putString("M", jSONObject2.getString("M"));
                    bundle2.putString("T", jSONObject2.getString("T"));
                    olPlayHallRoom.mailList.add(bundle2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            olPlayHallRoom.mo2848c();
            olPlayHallRoom.mo2849c(olPlayHallRoom.mailListView, olPlayHallRoom.mailList);
        } else if (f5459c == 1) {
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(4);
            builder.setDeclaration(valueOf);
            olPlayHallRoom.coupleBlessView.setText("祝语:\n" + valueOf);
            olPlayHallRoom.connectionService.writeData(31, builder.build());
        }
    }
}
