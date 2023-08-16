package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.activity.PianoPlay;

import java.lang.ref.WeakReference;

public final class PianoPlayHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public PianoPlayHandler(PianoPlay pianoPlay) {
        weakReference = new WeakReference<>(pianoPlay);
    }

    @Override
    public void handleMessage(final Message message) {
        final PianoPlay pianoPlay = (PianoPlay) weakReference.get();
        switch (message.what) {
            case 1:
                post(() -> {
                    pianoPlay.gradeList.clear();
                    Bundle data = message.getData();
                    int size = data.size();
                    for (int i = 0; i < size; i++) {
                        Bundle bundle = new Bundle();
                        bundle.putString("U", data.getBundle(String.valueOf(i)).getString("U"));
                        bundle.putString("G", data.getBundle(String.valueOf(i)).getString("G"));
                        bundle.putString("M", data.getBundle(String.valueOf(i)).getString("M"));
                        pianoPlay.gradeList.add(bundle);
                    }
                    pianoPlay.mo2905a(pianoPlay.horizontalListView, pianoPlay.gradeList);
                    if (message.arg1 == 0) {
                        pianoPlay.mo2906a(true);
                    }
                });
                return;
            case 2:
                post(() -> {
                    pianoPlay.gradeList.clear();
                    Bundle data = message.getData();
                    int size = data.size();
                    for (int i = 0; i < size; i++) {
                        Bundle bundle = new Bundle();
                        bundle.putString("G", data.getBundle(String.valueOf(i)).getString("G"));
                        bundle.putString("U", data.getBundle(String.valueOf(i)).getString("U"));
                        bundle.putString("M", data.getBundle(String.valueOf(i)).getString("M"));
                        bundle.putString("S", data.getBundle(String.valueOf(i)).getString("T"));
                        pianoPlay.gradeList.add(bundle);
                    }
                    pianoPlay.mo2905a(pianoPlay.horizontalListView, pianoPlay.gradeList);
                });
                return;
            case 3:
                post(() -> {
                    pianoPlay.gradeList.clear();
                    Bundle data = message.getData();
                    int size = data.size();
                    for (int i = 0; i < size; i++) {
                        Bundle bundle = new Bundle();
                        bundle.putString("I", data.getBundle(String.valueOf(i)).getString("I"));
                        bundle.putString("N", data.getBundle(String.valueOf(i)).getString("N"));
                        bundle.putString("SC", data.getBundle(String.valueOf(i)).getString("SC"));
                        bundle.putString("P", data.getBundle(String.valueOf(i)).getString("P"));
                        bundle.putString("C", data.getBundle(String.valueOf(i)).getString("C"));
                        bundle.putString("G", data.getBundle(String.valueOf(i)).getString("G"));
                        bundle.putString("B", data.getBundle(String.valueOf(i)).getString("B"));
                        bundle.putString("M", data.getBundle(String.valueOf(i)).getString("M"));
                        bundle.putString("T", data.getBundle(String.valueOf(i)).getString("T"));
                        bundle.putString("E", data.getBundle(String.valueOf(i)).getString("E"));
                        bundle.putString("GR", data.getBundle(String.valueOf(i)).getString("GR"));
                        pianoPlay.gradeList.add(bundle);
                    }
                    pianoPlay.mo2907b(pianoPlay.gradeListView, pianoPlay.gradeList);
                });
                return;
            case 4:
                post(() -> pianoPlay.keyboardview.updateTouchNoteNum());
                return;
            case 5:
                post(() -> pianoPlay.mo2906a(true));
                return;
            case 6:
                post(() -> {
                    JPDialog jpdialog = new JPDialog(pianoPlay);
                    jpdialog.setTitle("考级结果");
                    Bundle data = message.getData();
                    int i = data.getInt("R");
                    StringBuilder stringBuffer = new StringBuilder();
                    stringBuffer.append("您的分数:").append(data.getString("S")).append("\n");
                    stringBuffer.append("合格分数:").append(data.getInt("G")).append("\n");
                    stringBuffer.append("获得经验:").append(data.getString("E")).append("\n");
                    switch (i) {
                        case 0:
                            stringBuffer.append("很遗憾,您未通过该首曲目,请再接再厉!\n");
                            break;
                        case 1:
                            stringBuffer.append("恭喜您,您已通过该首曲目,请再接再厉!\n");
                            break;
                        case 2:
                            stringBuffer.append("恭喜您,您已通过该阶段的全部曲目,晋升一级!\n");
                            break;
                    }
                    jpdialog.setMessage(stringBuffer.toString());
                    jpdialog.setCancelableFalse();
                    jpdialog.setFirstButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        pianoPlay.finish();
                    });
                    jpdialog.showDialog();
                });
                return;
            case 7:
                post(() -> {
                    if (message.arg1 == 0) {  //本地模式或联网模式计时完成后开始
                        if (pianoPlay.f4591J != null) {
                            pianoPlay.f4591J.setVisibility(View.GONE);
                        }
                        pianoPlay.f4596O.setVisibility(View.VISIBLE);
                        pianoPlay.f4597P.setVisibility(View.VISIBLE);
                        pianoPlay.startPlayButton.setVisibility(View.VISIBLE);
                        pianoPlay.songName.setText(pianoPlay.songsName);
                        pianoPlay.isShowingSongsInfo = false;
                        pianoPlay.playView.startFirstNoteTouching = true;
                        pianoPlay.songName.setEnabled(true);
                        return;
                    }
                    pianoPlay.songName.setText(String.valueOf(message.arg1));
                    pianoPlay.songName.setEnabled(false);
                });
                return;
            case 8:
                post(() -> {
                    pianoPlay.finishView.setVisibility(View.VISIBLE);
                    pianoPlay.finishSongName.setText(pianoPlay.songsName);
                });
                return;
            case 9:
                post(() -> {
                    JPDialog jpdialog = new JPDialog(pianoPlay);
                    jpdialog.setTitle("挑战结束");
                    jpdialog.setMessage(message.getData().getString("I"));
                    jpdialog.setCancelableFalse();
                    jpdialog.setFirstButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        pianoPlay.finish();
                    });
                    jpdialog.showDialog();
                });
                return;
            case 21:
                post(() -> {
                    Toast.makeText(pianoPlay, "您已掉线,请检查您的网络再重新登录!", Toast.LENGTH_SHORT).show();
                    pianoPlay.setOutline(true);
                    pianoPlay.finish();
                });
                return;
            default:
        }
    }
}
