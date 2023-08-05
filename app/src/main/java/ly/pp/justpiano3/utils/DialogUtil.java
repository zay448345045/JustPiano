package ly.pp.justpiano3.utils;

import android.content.Context;
import android.widget.Toast;
import ly.pp.justpiano3.DialogDismissClick;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.JPDialog;
import protobuf.dto.OnlineDialogDTO;

/**
 * @author as
 */
public class DialogUtil {

    /**
     * 赠送音符消息接收-对话框处理
     */
    public static void handleGoldSend(Context context, JPApplication jpApplication, JPDialog jpDialog, int messageType, String userName, String message) {
        if (messageType == 2) {
            jpDialog.setVisibleEditText(true);
            jpDialog.setSecondButton("取消", new DialogDismissClick());
            jpDialog.setFirstButton("确定", ((dialog, which) -> {
                int goldNum;
                try {
                    goldNum = Integer.parseInt(jpDialog.getEditTextString());
                    if (goldNum <= 0) {
                        throw new RuntimeException();
                    }
                } catch (Exception e) {
                    Toast.makeText(context, "请填写合法的正数!", Toast.LENGTH_SHORT).show();
                    return;
                }
                dialog.dismiss();
                jpApplication.setIsShowDialog(false);
                OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                builder.setType(3);
                builder.setName(userName);
                builder.setGold(goldNum);
                jpApplication.getConnectionService().writeData(37, builder.build());
            }));
        }
    }
}
