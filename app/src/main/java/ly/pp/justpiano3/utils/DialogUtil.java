package ly.pp.justpiano3.utils;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.JPDialog;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.view.GoldConvertView;
import protobuf.dto.OnlineDialogDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author as
 */
public class DialogUtil {

    /**
     * 赠送音符消息接收-对话框处理
     */
    public static void handleGoldSend(JPApplication jpApplication, JPDialog jpDialog, int messageType, String userName, String handlingFee) {
        if (messageType == 2) {
            jpDialog.setVisibleGoldConvertView(true);
            jpDialog.getGoldConvertView().setGoldValueConvertRule(new GoldConvertView.GoldValueConvertRule() {
                @Override
                public BigDecimal convertToShow(BigDecimal actualValue) {
                    return actualValue;
                }

                @Override
                public BigDecimal convertToActual(BigDecimal showValue) {
                    return showValue;
                }
            }, new GoldConvertView.GoldValueConvertRule() {
                @Override
                public BigDecimal convertToShow(BigDecimal actualValue) {
                    return actualValue.multiply(BigDecimal.ONE.subtract(new BigDecimal(handlingFee))).setScale(0, RoundingMode.DOWN);
                }

                @Override
                public BigDecimal convertToActual(BigDecimal showValue) {
                    return showValue.divide(BigDecimal.ONE.subtract(new BigDecimal(handlingFee)), 0, RoundingMode.UP);
                }
            });
            jpDialog.setSecondButton("取消", (dialog, which) -> {
                dialog.dismiss();
                jpApplication.setIsShowDialog(false);
            });
            jpDialog.setFirstButton("确定", ((dialog, which) -> {
                dialog.dismiss();
                jpApplication.setIsShowDialog(false);
                OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                builder.setType(3);
                builder.setName(userName);
                builder.setGold(jpDialog.getGoldConvertView().getActualValue().intValue());
                jpApplication.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
            }));
        }
    }
}
