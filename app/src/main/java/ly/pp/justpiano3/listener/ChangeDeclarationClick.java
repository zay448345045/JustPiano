package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import ly.pp.justpiano3.activity.online.OLFamily;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineSendMailDTO;

public final class ChangeDeclarationClick implements OnClickListener {

    private final OLFamily family;
    private final TextView textView;
    private final int type;
    private final String userName;

    public ChangeDeclarationClick(OLFamily family, TextView textView, int i, String str) {
        this.family = family;
        this.textView = textView;
        type = i;
        userName = str;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(textView.getText());
        if (TextUtils.isEmpty(valueOf) || "'".equals(valueOf)) {
            Toast.makeText(family, "请输入内容!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > Consts.MAX_MESSAGE_COUNT) {
            Toast.makeText(family, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else if (type == 0) {
            dialogInterface.dismiss();
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setMessage(valueOf);
            builder.setName(userName);
            family.sendMsg(OnlineProtocolType.SEND_MAIL, builder.build());
        } else if (type == 1) {
            dialogInterface.dismiss();
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(9);
            builder.setMessage(valueOf);
            family.sendMsg(OnlineProtocolType.FAMILY, builder.build());
            family.declarationTextView.setText("家族宣言:\n" + valueOf);
        }
    }
}
