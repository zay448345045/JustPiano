package ly.pp.justpiano3.listener;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.constant.OnlineProtocolType;
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
        String valueOf = String.valueOf(textView.getText());
        if (valueOf.isEmpty() || valueOf.equals("'")) {
            Toast.makeText(context, "请输入信件内容!", Toast.LENGTH_SHORT).show();
        } else if (valueOf.length() > 500) {
            Toast.makeText(context, "确定在五百字之内!", Toast.LENGTH_SHORT).show();
        } else {
            OnlineSendMailDTO.Builder builder = OnlineSendMailDTO.newBuilder();
            builder.setName(userName);
            builder.setMessage(valueOf);
            if (!userName.isEmpty()) {
                JPApplication jpApplication = (JPApplication) context.getApplicationContext();
                jpApplication.getConnectionService().writeData(OnlineProtocolType.SEND_MAIL, builder.build());
            }
            dialogInterface.dismiss();
            Toast.makeText(context, "发送成功!", Toast.LENGTH_SHORT).show();
        }
    }
}
