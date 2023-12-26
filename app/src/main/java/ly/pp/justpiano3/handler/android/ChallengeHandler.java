package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLChallenge;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.adapter.ChallengeListAdapter;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class ChallengeHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public ChallengeHandler(OLChallenge challenge) {
        weakReference = new WeakReference<>(challenge);
    }

    @Override
    public void handleMessage(Message message) {
        OLChallenge challenge = (OLChallenge) weakReference.get();
        try {
            switch (message.what) {
                case 1:
                    post(() -> {
                        challenge.scoreList.clear();
                        Bundle data = message.getData();
                        int size = data.size() - 4;
                        for (int i = 0; i < size; i++) {
                            Map<String, String> hashMap = new HashMap<>();
                            hashMap.put("S", data.getBundle(String.valueOf(i)).getString("S"));
                            hashMap.put("N", data.getBundle(String.valueOf(i)).getString("N"));
                            hashMap.put("T", data.getBundle(String.valueOf(i)).getString("T"));
                            hashMap.put("P", String.valueOf(i + 1));
                            challenge.scoreList.add(hashMap);
                        }
                        challenge.scoreListView.setAdapter(new ChallengeListAdapter(challenge.scoreList, challenge.layoutinflater));
                        StringBuilder sb = new StringBuilder();
                        sb.append("用户名称:").append(OLBaseActivity.getKitiName())
                                .append("\n最高分:").append(data.getInt("S"))
                                .append("\n今日名次:").append(data.getString("P"))
                                .append("\n昨日名次:").append(data.getString("Z"))
                                .append("\n剩余挑战次数:").append(data.getString("T"));
                        challenge.remainTimes = Integer.parseInt(data.getString("T"));
                        challenge.info.setText(sb);
                        challenge.jpprogressBar.dismiss();
                    });
                    return;
                case 2:
                    post(() -> {
                        Bundle data = message.getData();
                        int i = data.getInt("R");
                        String string = data.getString("I");
                        String string2 = data.getString("P");
                        String str = "提示";
                        String str2 = switch (i) {
                            case 0 -> "确定";
                            case 1 -> "开始挑战";
                            default -> null;
                        };
                        JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(challenge);
                        jpDialogBuilder.setTitle(str);
                        jpDialogBuilder.setMessage(string);
                        jpDialogBuilder.setFirstButton(str2, (dialog, which) -> {
                            dialog.dismiss();
                            if (i == 1) {
                                Intent intent = new Intent(challenge, PianoPlay.class);
                                intent.putExtra("head", 4);
                                intent.putExtra("songBytes", string2);
                                intent.putExtra("times", challenge.remainTimes);
                                intent.putExtra("hand", 0);
                                intent.putExtra("name", "");
                                Bundle b = new Bundle();
                                b.putByte("hallID", challenge.hallID);
                                b.putString("hallName", challenge.hallName);
                                intent.putExtra("bundle", b);
                                intent.putExtra("bundleHall", b);
                                challenge.startActivity(intent);
                                challenge.finish();
                            }
                        });
                        if (i == 1) {
                            jpDialogBuilder.setSecondButton("取消", (dialog, which) -> dialog.dismiss());
                        }
                        jpDialogBuilder.buildAndShowDialog();
                    });
                    return;
                case 5:
                    post(() -> challenge.showDrawPrizeDialog(message.getData()));
                default:
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
