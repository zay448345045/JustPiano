package ly.pp.justpiano3.listener;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;

import ly.pp.justpiano3.activity.OLFamily;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import protobuf.dto.OnlineFamilyDTO;
import protobuf.dto.OnlineSendMailDTO;

public final class ChangeDeclarationClick implements OnClickListener {

    private final OLFamily family;
    private final TextView f5458b;
    private final int f5459c;
    private final String f5460d;

    public ChangeDeclarationClick(OLFamily family, TextView textView, int i, String str) {
        this.family = family;
        f5458b = textView;
        f5459c = i;
        f5460d = str;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f5458b.getText());
        if (TextUtils.isEmpty(valueOf) || "'".equals(valueOf)) {
            Toast.makeText(family, "请输入内容!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > Consts.MAX_MESSAGE_COUNT) {
            Toast.makeText(family, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else if (f5459c == 0) {
            dialogInterface.dismiss();
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setMessage(valueOf);
            builder.setName(f5460d);
            family.sendMsg(OnlineProtocolType.SEND_MAIL, builder.build());
        } else if (f5459c == 1) {
            dialogInterface.dismiss();
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(9);
            builder.setMessage(valueOf);
            family.sendMsg(OnlineProtocolType.FAMILY, builder.build());
            family.declaration.setText("家族宣言:\n" + valueOf);
        }
    }
}
