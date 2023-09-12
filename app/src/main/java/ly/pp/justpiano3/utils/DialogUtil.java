package ly.pp.justpiano3.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.view.GoldConvertView;
import ly.pp.justpiano3.view.JPDialog;
import protobuf.dto.OnlineDialogDTO;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
                DialogUtil.setShowDialog(false);
            });
            jpDialog.setFirstButton("确定", ((dialog, which) -> {
                dialog.dismiss();
                DialogUtil.setShowDialog(false);
                OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                builder.setType(3);
                builder.setName(userName);
                builder.setGold(jpDialog.getGoldConvertView().getActualValue().intValue());
                jpApplication.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
            }));
        }
    }

    /**
     * 根据用户信息设置服装图片
     */
    public static void setUserDressImageBitmap(Context context, User user, ImageView imageView, ImageView imageView2, ImageView imageView3, ImageView imageView4, ImageView imageView4e, ImageView imageView5) throws IOException {
        imageView.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + user.getSex() + "_m0.png")));
        if (user.getTrousers() <= 0) {
            imageView2.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png")));
        } else {
            imageView2.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + user.getSex() + "_t" + (user.getTrousers() - 1) + ".png")));
        }
        if (user.getJacket() <= 0) {
            imageView3.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png")));
        } else {
            imageView3.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + user.getSex() + "_j" + (user.getJacket() - 1) + ".png")));
        }
        if (user.getHair() <= 0) {
            imageView4.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png")));
        } else {
            imageView4.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + user.getSex() + "_h" + (user.getHair() - 1) + ".png")));
        }
        if (user.getEye() <= 0) {
            imageView4e.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png")));
        } else {
            imageView4e.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + user.getSex() + "_e" + (user.getEye() - 1) + ".png")));
        }
        if (user.getShoes() <= 0) {
            imageView5.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png")));
        } else {
            imageView5.setImageBitmap(BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/" + user.getSex() + "_s" + (user.getShoes() - 1) + ".png")));
        }
    }
}
