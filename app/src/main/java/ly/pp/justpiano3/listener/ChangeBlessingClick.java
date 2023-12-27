package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.utils.OnlineUtil;
import protobuf.dto.OnlineSendMailDTO;
import protobuf.dto.OnlineSetUserInfoDTO;

public final class ChangeBlessingClick implements OnClickListener {

    private final OLPlayHallRoom olPlayHallRoom;
    private final TextView textView;
    private final int type;
    private final String userName;

    public ChangeBlessingClick(OLPlayHallRoom olPlayHallRoom, TextView textView, int i, String str) {
        this.olPlayHallRoom = olPlayHallRoom;
        this.textView = textView;
        type = i;
        userName = str;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        String valueOf = String.valueOf(textView.getText());
        if (valueOf.isEmpty() || valueOf.equals("'")) {
            Toast.makeText(olPlayHallRoom, "请输入内容!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > Consts.MAX_MESSAGE_COUNT) {
            Toast.makeText(olPlayHallRoom, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else if (type == 0 && !userName.isEmpty()) {
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setMessage(valueOf);
            builder.setName(userName);
            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.SEND_MAIL, builder.build());
            Toast.makeText(olPlayHallRoom, "发送成功!", Toast.LENGTH_SHORT).show();
            olPlayHallRoom.mailList.clear();
            String format = SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CHINESE).format(new Date());
            Bundle bundle = new Bundle();
            bundle.putString("F", userName);
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
            olPlayHallRoom.saveMailToLocal();
            olPlayHallRoom.updateMailListShow(olPlayHallRoom.mailListView, olPlayHallRoom.mailList);
        } else if (type == 1) {
            OnlineSetUserInfoDTO.Builder builder = OnlineSetUserInfoDTO.newBuilder();
            builder.setType(4);
            builder.setDeclaration(valueOf);
            olPlayHallRoom.coupleBlessView.setText("祝语:\n" + valueOf);
            OnlineUtil.getConnectionService().writeData(OnlineProtocolType.SET_USER_INFO, builder.build());
        }
    }
}
