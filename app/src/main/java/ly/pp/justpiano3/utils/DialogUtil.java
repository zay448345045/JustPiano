package ly.pp.justpiano3.utils;

import android.content.Context;
import android.graphics.Bitmap;
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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author as
 */
public class DialogUtil {

    private static final Map<String, Bitmap> bitmapCacheMap = new ConcurrentHashMap<>(256);
    private static final StringBuilder stringBuilderCache = new StringBuilder();
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
    public static void setUserDressImageBitmap(Context context, User user, ImageView bodyImageView, ImageView trousersImageView, ImageView jacketImageView,
                                               ImageView hairImageView, ImageView eyeImageView, ImageView shoesImageView) throws IOException {
        setUserDressImageBitmap(context, user.getSex(), user.getTrousers(), user.getJacket(), user.getHair(), user.getEye(), user.getShoes(),
                bodyImageView, trousersImageView, jacketImageView, hairImageView, eyeImageView, shoesImageView);
    }

    public static void setUserDressImageBitmap(Context context, String gender, int trousers, int jacket, int hair, int eye, int shoes,
                                               ImageView bodyImageView, ImageView trousersImageView, ImageView jacketImageView,
                                               ImageView hairImageView, ImageView eyeImageView, ImageView shoesImageView) throws IOException {
        if (!bitmapCacheMap.containsKey("mod/_none.png")) {
            bitmapCacheMap.put("mod/_none.png", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/_none.png")));
        }
        if (!bitmapCacheMap.containsKey("mod/f_m0.png")) {
            bitmapCacheMap.put("mod/f_m0.png", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/f_m0.png")));
        }
        if (!bitmapCacheMap.containsKey("mod/m_m0.png")) {
            bitmapCacheMap.put("mod/m_m0.png", BitmapFactory.decodeStream(context.getResources().getAssets().open("mod/m_m0.png")));
        }
        Bitmap noneModBitmap = bitmapCacheMap.get("mod/_none.png");
        bodyImageView.setImageBitmap(bitmapCacheMap.get(gender.equals("f") ? "mod/f_m0.png" : "mod/m_m0.png"));

        if (trousers <= 0) {
            trousersImageView.setImageBitmap(noneModBitmap);
        } else {
            stringBuilderCache.setLength(0);
            stringBuilderCache.append("mod/").append(gender).append('_').append('t').append(trousers - 1).append(".png");
            String trousersString = stringBuilderCache.toString();
            if (!bitmapCacheMap.containsKey(trousersString)) {
                bitmapCacheMap.put(trousersString, BitmapFactory.decodeStream(context.getResources().getAssets().open(trousersString)));
            }
            trousersImageView.setImageBitmap(bitmapCacheMap.get(trousersString));
        }

        if (jacket <= 0) {
            jacketImageView.setImageBitmap(noneModBitmap);
        } else {
            stringBuilderCache.setLength(0);
            stringBuilderCache.append("mod/").append(gender).append('_').append('j').append(jacket - 1).append(".png");
            String jacketString = stringBuilderCache.toString();
            if (!bitmapCacheMap.containsKey(jacketString)) {
                bitmapCacheMap.put(jacketString, BitmapFactory.decodeStream(context.getResources().getAssets().open(jacketString)));
            }
            jacketImageView.setImageBitmap(bitmapCacheMap.get(jacketString));
        }

        if (hair <= 0) {
            hairImageView.setImageBitmap(noneModBitmap);
        } else {
            stringBuilderCache.setLength(0);
            stringBuilderCache.append("mod/").append(gender).append('_').append('h').append(hair - 1).append(".png");
            String hairString = stringBuilderCache.toString();
            if (!bitmapCacheMap.containsKey(hairString)) {
                bitmapCacheMap.put(hairString, BitmapFactory.decodeStream(context.getResources().getAssets().open(hairString)));
            }
            hairImageView.setImageBitmap(bitmapCacheMap.get(hairString));
        }

        if (eye <= 0) {
            eyeImageView.setImageBitmap(noneModBitmap);
        } else {
            stringBuilderCache.setLength(0);
            stringBuilderCache.append("mod/").append(gender).append('_').append('e').append(eye - 1).append(".png");
            String eyeString = stringBuilderCache.toString();
            if (!bitmapCacheMap.containsKey(eyeString)) {
                bitmapCacheMap.put(eyeString, BitmapFactory.decodeStream(context.getResources().getAssets().open(eyeString)));
            }
            eyeImageView.setImageBitmap(bitmapCacheMap.get(eyeString));
        }

        if (shoes <= 0) {
            shoesImageView.setImageBitmap(noneModBitmap);
        } else {
            stringBuilderCache.setLength(0);
            stringBuilderCache.append("mod/").append(gender).append('_').append('s').append(shoes - 1).append(".png");
            String shoesString = stringBuilderCache.toString();
            if (!bitmapCacheMap.containsKey(shoesString)) {
                bitmapCacheMap.put(shoesString, BitmapFactory.decodeStream(context.getResources().getAssets().open(shoesString)));
            }
            shoesImageView.setImageBitmap(bitmapCacheMap.get(shoesString));
        }
    }
}
