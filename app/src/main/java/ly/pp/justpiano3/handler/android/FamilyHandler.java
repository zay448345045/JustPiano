package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import ly.pp.justpiano3.activity.online.OLFamily;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.enums.FamilyPositionEnum;
import protobuf.dto.OnlineFamilyDTO;

public final class FamilyHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public FamilyHandler(OLFamily family) {
        super(Looper.getMainLooper());
        weakReference = new WeakReference<>(family);
    }

    @Override
    public void handleMessage(@NonNull Message message) {
        OLFamily olFamily = (OLFamily) weakReference.get();
        switch (message.what) {
            case 1 -> post(() -> {
                olFamily.userList.clear();
                Bundle bundle = message.getData();
                int size = bundle.size() - 6;
                if (size == 0) {
                    Map<String, String> familyUserMap = new HashMap<>();
                    familyUserMap.put("C", "");
                    familyUserMap.put("N", "(入族后可显示族员)");
                    familyUserMap.put("L", "");
                    familyUserMap.put("O", "");
                    familyUserMap.put("P", "");
                    familyUserMap.put("S", "");
                    familyUserMap.put("D", "");
                    olFamily.userList.add(familyUserMap);
                } else {
                    for (int i = 0; i < size; i++) {
                        Map<String, String> familyUserMap = new HashMap<>();
                        familyUserMap.put("C", bundle.getBundle(String.valueOf(i)).getString("C"));
                        familyUserMap.put("N", bundle.getBundle(String.valueOf(i)).getString("N"));
                        familyUserMap.put("L", bundle.getBundle(String.valueOf(i)).getString("L"));
                        familyUserMap.put("O", bundle.getBundle(String.valueOf(i)).getString("O"));
                        familyUserMap.put("P", bundle.getBundle(String.valueOf(i)).getString("P"));
                        familyUserMap.put("S", bundle.getBundle(String.valueOf(i)).getString("S"));
                        familyUserMap.put("D", bundle.getBundle(String.valueOf(i)).getString("D"));
                        olFamily.userList.add(familyUserMap);
                    }
                }
                olFamily.bindFamilyUserListViewAdapter(olFamily.userListView, olFamily.userList);
                olFamily.declarationTextView.setText("家族宣言:\n" + bundle.getString("D"));
                olFamily.infoTextView.setText("家族名称:" + bundle.getString("N")
                        + "\n家族成立日期:" + bundle.getString("T")
                        + "\n族长:" + bundle.getString("Z")
                        + "\n家族总贡献:" + bundle.getString("C"));
                switch (Objects.requireNonNull(bundle.getString("P"))) {
                    case "族长" -> olFamily.familyPositionEnum = FamilyPositionEnum.LEADER;
                    case "副族长" -> olFamily.familyPositionEnum = FamilyPositionEnum.VICE_LEADER;
                    case "族员" -> olFamily.familyPositionEnum = FamilyPositionEnum.MEMBER;
                    default -> olFamily.familyPositionEnum = FamilyPositionEnum.NOT_IN_FAMILY;
                }
                olFamily.positionHandle();
            });
            case 5 -> post(() -> {
                olFamily.jpprogressBar.dismiss();
                String info = message.getData().getString("I");
                Toast.makeText(olFamily, info, Toast.LENGTH_SHORT).show();
                if (olFamily.userInfoPopupWindow != null && olFamily.userInfoPopupWindow.isShowing()) {
                    olFamily.userInfoPopupWindow.dismiss();
                }
                OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                if (Objects.equals(info, "您所在的家族已解散")) {
                    builder.setType(0);
                    olFamily.sendMsg(OnlineProtocolType.FAMILY, builder.build());
                    Intent intent = new Intent(olFamily, OLPlayHallRoom.class);
                    intent.putExtra("HEAD", 16);
                    olFamily.startActivity(intent);
                    olFamily.finish();
                } else {
                    builder.setType(1);
                    builder.setFamilyId(Integer.parseInt(olFamily.familyID));
                    olFamily.sendMsg(OnlineProtocolType.FAMILY, builder.build());
                }
            });
            case 8 -> post(() -> {
                olFamily.jpprogressBar.dismiss();
                olFamily.loadManageFamilyPopupWindow(message.getData());
            });
            case 23 -> post(() -> olFamily.showInfoDialog(message.getData()));
            default -> {
            }
        }
    }
}
