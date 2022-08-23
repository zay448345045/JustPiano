package ly.pp.justpiano3;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.protobuf.dto.OnlineFamilyDTO;
import ly.pp.justpiano3.protobuf.dto.OnlineSendMailDTO;

final class ChangeDeclarationClick implements OnClickListener {

    private final OLFamily family;
    private final TextView f5458b;
    private final int f5459c;
    private final String f5460d;

    ChangeDeclarationClick(OLFamily family, TextView textView, int i, String str) {
        this.family = family;
        f5458b = textView;
        f5459c = i;
        f5460d = str;
    }

    @Override
    public final void onClick(DialogInterface dialogInterface, int i) {
        String valueOf = String.valueOf(f5458b.getText());
        if (StringUtil.isNullOrEmpty(valueOf) || "'".equals(valueOf)) {
            Toast.makeText(family, "请输入内容!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > 500) {
            Toast.makeText(family, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else if (f5459c == 0) {
            dialogInterface.dismiss();
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setMessage(valueOf);
            builder.setName(f5460d);
            family.sendMsg(35, builder.build());
        } else if (f5459c == 1) {
            dialogInterface.dismiss();
            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
            builder.setType(9);
            builder.setMessage(valueOf);
            family.sendMsg(18, builder.build());
            family.declaration.setText("家族宣言:\n" + valueOf);
        }
    }
}
