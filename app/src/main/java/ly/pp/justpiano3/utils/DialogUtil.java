package ly.pp.justpiano3.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.view.GoldConvertView;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineDialogDTO;

/**
 * @author as
 */
public class DialogUtil {

    /**
     * 是否已经显示对话框，防止对话框重复显示
     */
    private static boolean isShowDialog;

    public static boolean isShowDialog() {
        return isShowDialog;
    }

    public static void setShowDialog(boolean isShowDialog) {
        DialogUtil.isShowDialog = isShowDialog;
    }

    /**
     * 赠送音符消息接收-对话框处理
     */
    public static void handleGoldSend(JPApplication jpApplication, JPDialogBuilder jpDialogBuilder, int messageType, String userName, String handlingFee) {
        if (messageType == 2 && !StringUtil.isNullOrEmpty(handlingFee)) {
            jpDialogBuilder.setVisibleGoldConvertView(true);
            jpDialogBuilder.getGoldConvertView().setGoldValueConvertRule(new GoldConvertView.GoldValueConvertRule() {
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
            jpDialogBuilder.setSecondButton("取消", (dialog, which) -> {
                dialog.dismiss();
                DialogUtil.setShowDialog(false);
            });
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                dialog.dismiss();
                DialogUtil.setShowDialog(false);
                OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                builder.setType(3);
                builder.setName(userName);
                builder.setGold(jpDialogBuilder.getGoldConvertView().getActualValue().intValue());
                jpApplication.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
            });
        }
    }
}
