package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.HashMap;

import ly.pp.justpiano3.activity.OLFamily;
import ly.pp.justpiano3.activity.OLPlayHallRoom;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.enums.FamilyPositionEnum;
import protobuf.dto.OnlineFamilyDTO;

public final class FamilyHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public FamilyHandler(OLFamily family) {
        weakReference = new WeakReference<>(family);
    }

    @Override
    public void handleMessage(final Message message) {
        final OLFamily family = (OLFamily) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                    post(() -> {
                        family.peopleList.clear();
                        Bundle data = message.getData();
                        int size = data.size() - 6;
                        if (size == 0) {
                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("C", "");
                            hashMap.put("N", "(入族后可显示族员)");
                            hashMap.put("L", "");
                            hashMap.put("O", "");
                            hashMap.put("P", "");
                            hashMap.put("S", "");
                            hashMap.put("D", "");
                            family.peopleList.add(hashMap);
                        } else {
                            for (int i = 0; i < size; i++) {
                                HashMap<String, String> hashMap = new HashMap<>();
                                hashMap.put("C", data.getBundle(String.valueOf(i)).getString("C"));
                                hashMap.put("N", data.getBundle(String.valueOf(i)).getString("N"));
                                hashMap.put("L", data.getBundle(String.valueOf(i)).getString("L"));
                                hashMap.put("O", data.getBundle(String.valueOf(i)).getString("O"));
                                hashMap.put("P", data.getBundle(String.valueOf(i)).getString("P"));
                                hashMap.put("S", data.getBundle(String.valueOf(i)).getString("S"));
                                hashMap.put("D", data.getBundle(String.valueOf(i)).getString("D"));
                                family.peopleList.add(hashMap);
                            }
                        }
                        family.bindFamilyPeopleListViewAdapter(family.peopleListView, family.peopleList);
                        family.declaration.setText("家族宣言:\n" + data.getString("D"));
                        family.info.setText("家族名称:" + data.getString("N")
                                + "\n家族成立日期:" + data.getString("T")
                                + "\n族长:" + data.getString("Z")
                                + "\n家族总贡献:" + data.getString("C"));
                        switch (data.getString("P")) {
                            case "族长":
                                family.position = FamilyPositionEnum.LEADER;
                                break;
                            case "副族长":
                                family.position = FamilyPositionEnum.VICE_LEADER;
                                break;
                            case "族员":
                                family.position = FamilyPositionEnum.MEMBER;
                                break;
                            default:
                                family.position = FamilyPositionEnum.NOT_IN_FAMILY;
                                break;
                        }
                        family.positionHandle();
                    });
                    return;
                case 5:
                    post(() -> {
                        family.jpprogressBar.dismiss();
                        String info = message.getData().getString("I");
                        Toast.makeText(family, info, Toast.LENGTH_SHORT).show();
                        if (family.infoWindow != null && family.infoWindow.isShowing()) {
                            family.infoWindow.dismiss();
                        }
                        if (info.equals("您所在的家族已解散")) {
                            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                            builder.setType(0);
                            family.sendMsg(OnlineProtocolType.FAMILY, builder.build());
                            Intent intent = new Intent(family, OLPlayHallRoom.class);
                            intent.putExtra("HEAD", 16);
                            family.startActivity(intent);
                            family.finish();
                        } else {
                            OnlineFamilyDTO.Builder builder = OnlineFamilyDTO.newBuilder();
                            builder.setType(1);
                            builder.setFamilyId(Integer.parseInt(family.familyID));
                            family.sendMsg(OnlineProtocolType.FAMILY, builder.build());
                        }
                    });
                    return;
                case 8:
                    post(() -> {
                        family.jpprogressBar.dismiss();
                        family.loadManageFamilyPopupWindow(message.getData());
                    });
                    return;
                case 23:
                    post(() -> family.showInfoDialog(message.getData()));
                    return;
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
