package ly.pp.justpiano3.handler.android;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import ly.pp.justpiano3.activity.PianoPlay;
import ly.pp.justpiano3.view.JPDialogBuilder;

public final class PianoPlayHandler extends Handler {
    private final WeakReference<Activity> weakReference;

    public PianoPlayHandler(PianoPlay pianoPlay) {
        weakReference = new WeakReference<>(pianoPlay);
    }

    @Override
    public void handleMessage(final Message message) {
        final PianoPlay pianoPlay = (PianoPlay) weakReference.get();
        switch (message.what) {
            case 1 -> {
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
                    pianoPlay.updateMiniScore(pianoPlay.horizontalListView, pianoPlay.gradeList);
                    if (message.arg1 == 0) {
                        pianoPlay.playStartHandle(true);
                    }
                });
                return;
            }
            case 2 -> {
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
                    pianoPlay.updateMiniScore(pianoPlay.horizontalListView, pianoPlay.gradeList);
                });
                return;
            }
            case 3 -> {
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
                    pianoPlay.bindAdapter(pianoPlay.gradeListView, pianoPlay.gradeList);
                });
                return;
            }
            case 4 -> {
                post(() -> pianoPlay.playKeyBoardView.updateTouchNoteNum());
                return;
            }
            case 5 -> {
                post(() -> pianoPlay.playStartHandle(true));
                return;
            }
            case 6 -> {
                post(() -> {
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(pianoPlay);
                    jpDialogBuilder.setTitle("考级结果");
                    Bundle data = message.getData();
                    int i = data.getInt("R");
                    StringBuilder stringBuffer = new StringBuilder();
                    stringBuffer.append("您的分数:").append(data.getString("S")).append("\n");
                    stringBuffer.append("合格分数:").append(data.getInt("G")).append("\n");
                    stringBuffer.append("获得经验:").append(data.getString("E")).append("\n");
                    switch (i) {
                        case 0 -> stringBuffer.append("很遗憾,您未通过该首曲目,请再接再厉!\n");
                        case 1 -> stringBuffer.append("恭喜您,您已通过该首曲目,请再接再厉!\n");
                        case 2 ->
                                stringBuffer.append("恭喜您,您已通过该阶段的全部曲目,晋升一级!\n");
                    }
                    jpDialogBuilder.setMessage(stringBuffer.toString());
                    jpDialogBuilder.setCancelableFalse();
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        pianoPlay.finish();
                    });
                    jpDialogBuilder.buildAndShowDialog();
                });
                return;
            }
            case 7 -> {
                post(() -> {
                    if (message.arg1 == 0) {  //本地模式或联网模式计时完成后开始
                        if (pianoPlay.pausedPlay != null) {
                            pianoPlay.pausedPlay.setVisibility(View.GONE);
                        }
                        pianoPlay.rightHandDegreeTextView.setVisibility(View.VISIBLE);
                        pianoPlay.highScoreTextView.setVisibility(View.VISIBLE);
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
            }
            case 8 -> {
                post(() -> {
                    pianoPlay.finishView.setVisibility(View.VISIBLE);
                    pianoPlay.finishSongName.setText(pianoPlay.songsName);
                });
                return;
            }
            case 9 -> {
                post(() -> {
                    JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(pianoPlay);
                    jpDialogBuilder.setTitle("挑战结束");
                    jpDialogBuilder.setMessage(message.getData().getString("I"));
                    jpDialogBuilder.setCancelableFalse();
                    jpDialogBuilder.setFirstButton("确定", (dialog, which) -> {
                        dialog.dismiss();
                        pianoPlay.finish();
                    });
                    jpDialogBuilder.buildAndShowDialog();
                });
                return;
            }
            case 21 -> {
                post(() -> {
                    Toast.makeText(pianoPlay, "您已掉线，请检查您的网络再重新登录", Toast.LENGTH_SHORT).show();
                    pianoPlay.setOnline(false);
                    pianoPlay.finish();
                });
                return;
            }
            default -> {
            }
        }
    }
}
