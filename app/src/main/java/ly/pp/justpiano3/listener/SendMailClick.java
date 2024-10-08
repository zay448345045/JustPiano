package ly.pp.justpiano3.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.utils.OnlineUtil;
import protobuf.dto.OnlineSendMailDTO;

public final class SendMailClick implements OnClickListener {
    private final Context context;
    private final TextView textView;
    private final String userName;

    public SendMailClick(Context context, TextView textView, String userName) {
        this.context = context;
        this.textView = textView;
        this.userName = userName;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        String text = String.valueOf(textView.getText());
        if (text.isEmpty() || "'".equals(text) || "''".equals(text) || "'''".equals(text)) {
            Toast.makeText(context, "请输入内容!", Toast.LENGTH_SHORT).show();
        } else if (text.length() > Consts.MAX_MESSAGE_COUNT) {
            Toast.makeText(context, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else {
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setName(userName);
            builder.setMessage(text);
            if (!userName.isEmpty()) {
                OnlineUtil.getConnectionService().writeData(OnlineProtocolType.SEND_MAIL, builder.build());
            }
            dialogInterface.dismiss();
            Toast.makeText(context, "发送成功!", Toast.LENGTH_SHORT).show();
        }
    }
}
