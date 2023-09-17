package ly.pp.justpiano3.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.activity.OLPlayRoomActivity;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.listener.ChangeRecommandSongClick;
import ly.pp.justpiano3.utils.JPStack;

public final class ChattingAdapter extends BaseAdapter {
    public Activity activity;
    private final List<Bundle> msgList;
    private final LayoutInflater layoutInflater;
    public JPApplication jpapplication;

    public ChattingAdapter(JPApplication jpapplication, List<Bundle> list, LayoutInflater layoutInflater) {
        msgList = list;
        this.jpapplication = jpapplication;
        this.layoutInflater = layoutInflater;
        activity = JPStack.top();
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Bundle bundle = msgList.get(i);
        int i2 = bundle.getInt("T");
        view = layoutInflater.inflate(R.layout.ol_msg_view, null);

        TextView userText = view.findViewById(R.id.ol_user_text);
        TextView msgMaoHao = view.findViewById(R.id.ol_user_mao);
        TextView msgText = view.findViewById(R.id.ol_msg_text);
        String string = bundle.getString("U");
        switch (i2) {
            case 0: // 曲谱推荐
                handleRecommendationMessage(view, userText, msgText, bundle, i);
                break;
            case 1: // 公共消息
                handlePublicMessage(view, userText, msgText, bundle);
                break;
            case 2: // 私聊消息
                handlePrivateMessage(view, userText, msgText, bundle);
                break;
            case 3: // 系统消息
                handleSystemMessage(view, userText, msgText, bundle);
                break;
            case 18: // 全服消息
                handleServerMessage(view, userText, msgText, bundle);
                break;
        }
        // 投递通知到房间内处理
        if (i2 != 3 && userText != null) {
            userText.setOnClickListener(v -> {
                if (activity instanceof OLPlayRoomActivity && string != null) {
                    ((OLPlayRoomActivity) activity).setPrivateChatUserName(string);
                } else if (activity instanceof OLPlayHall && string != null) {
                    ((OLPlayHall) activity).setPrivateChatUserName(string);
                }
            });
        }
        // 处理文本框字体大小
        setMsgFontSize(userText, msgMaoHao, msgText);
        return view;
    }

    /**
     * 设置MsgView的文本字体大小
     */
    private void setMsgFontSize(TextView... views) {
        if (views != null) {
            for (TextView view : views) {
                view.setTextSize(GlobalSetting.INSTANCE.getChatTextSize());
            }
        }
    }

    private void handleRecommendationMessage(View view, TextView textView, TextView textView2, Bundle bundle, int i) {
        String str1 = bundle.getString("I");
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        if (!str1.isEmpty()) {
            str1 = "songs/" + str1 + ".pm";
            ((OLPlayRoom) activity).setTune(bundle.getInt("D"));
            textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[荐]" + string);
            String[] a = ((OLPlayRoom) activity).querySongNameAndDiffByPath(str1);
            if (a[0] != null && a[1] != null) {
                textView2.setText(string2 + a[0] + "[难度:" + a[1] + "]");
                textView2.setTextColor(0xffff0000);
                if (((OLPlayRoom) activity).getPlayerKind().equals("H")) {
                    textView2.append(" (点击选取)");
                    textView2.setOnClickListener(new ChangeRecommandSongClick(this, bundle.getString("I")));
                }
            }
        }
    }

    private void handlePublicMessage(View view, TextView textView, TextView textView3, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        int id = bundle.getInt("V");
        if (!string2.startsWith("//")) {
            textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + string);
            TextView textView2 = view.findViewById(R.id.ol_user_mao);
            if (JPStack.top() instanceof OLPlayRoomActivity) {
                switch (id) {
                    case 1:
                        textView.setTextColor(0xffFFFACD);
                        textView2.setTextColor(0xffFFFACD);
                        textView3.setTextColor(0xffFFFACD);
                        break;
                    case 2:
                        textView.setTextColor(0xff00ffff);
                        textView2.setTextColor(0xff00ffff);
                        textView3.setTextColor(0xff00ffff);
                        break;
                    case 3:
                        textView.setTextColor(0xffFF6666);
                        textView2.setTextColor(0xffFF6666);
                        textView3.setTextColor(0xffFF6666);
                        break;
                    case 4:
                        textView.setTextColor(0xffFFA500);
                        textView2.setTextColor(0xffFFA500);
                        textView3.setTextColor(0xffFFA500);
                        break;
                    case 5:
                        textView.setTextColor(0xffBA55D3);
                        textView2.setTextColor(0xffBA55D3);
                        textView3.setTextColor(0xffBA55D3);
                        break;
                    case 6:
                        textView.setTextColor(0xfffa60ea);
                        textView2.setTextColor(0xfffa60ea);
                        textView3.setTextColor(0xfffa60ea);
                        break;
                    case 7:
                        textView.setTextColor(0xffFFD700);
                        textView2.setTextColor(0xffFFD700);
                        textView3.setTextColor(0xffFFD700);
                        break;
                    case 8:
                        textView.setTextColor(0xffb7ff72);
                        textView2.setTextColor(0xffb7ff72);
                        textView3.setTextColor(0xffb7ff72);
                        break;
                    case 9:
                        textView.setTextColor(0xff000000);
                        textView2.setTextColor(0xff000000);
                        textView3.setTextColor(0xff000000);
                        break;
                    default:
                        if (string.equals("琴娘")) {
                            textView.setTextColor(0xffffd8ec);
                            textView2.setTextColor(0xffffd8ec);
                            textView3.setTextColor(0xffffd8ec);
                        } else {
                            textView.setTextColor(0xffffffff);
                            textView2.setTextColor(0xffffffff);
                            textView3.setTextColor(0xffffffff);
                        }
                        break;
                }
            } else if (JPStack.top() instanceof OLPlayHall) {
                textView.setTextColor(0xffFFFACD);
                textView2.setTextColor(0xffFFFACD);
                textView3.setTextColor(0xffFFFACD);
            }
            textView3.setText(string2);
        } else {
            try {
                textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + string);
                ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(Consts.expressions[Integer.parseInt(string2.substring(2))]);
            } catch (Exception e) {
                textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + string);
                switch (string2) {
                    case "//dalao0":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.dalao0);
                        break;
                    case "//7yxg":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.qiyexingguo);
                        break;
                    case "//family":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.family);
                        break;
                    case "//redheart":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_1);
                        break;
                    case "//greenheart":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_2);
                        break;
                    case "//purpleheart":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_3);
                        break;
                    case "//crawl":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.pazou);
                        break;
                    case "//greencircle":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.greenlight);
                        break;
                    case "//jpgq":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.icon);
                        break;
                    case "//soundstop":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.stop);
                        break;
                    case "//greensquare":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.button_down);
                        break;
                    case "//blacksquare":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.button_up);
                        break;
                    case "//rightarrow":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.for_arrow);
                        break;
                    case "//leftarrow":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.back_arrow);
                        break;
                    case "//music":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.music);
                        break;
                    case "//bluecircle":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.redlight);
                        break;
                    case "//jpgq1":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.jpgq1);
                        break;
                    case "//huaji":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.huaji);
                        break;
                    case "//bugaoxing":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.bugaoxing);
                        break;
                    case "//yingyingying":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.yingyingying);
                        break;
                    case "//...":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.shenglve);
                        break;
                    case "//momotou":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.momotou);
                        break;
                    case "//heguozhi":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.heguozhi);
                        break;
                    case "//hen6":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.hen6);
                        break;
                    case "//buyaozheyang":
                        ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.buyaozheyang);
                        break;
                    default:
                        textView.setTextColor(0xffffffff);
                        ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xffffffff);
                        ((TextView) view.findViewById(R.id.ol_msg_text)).setTextColor(0xffffffff);
                        ((TextView) view.findViewById(R.id.ol_msg_text)).setText(string2);
                }
            }
        }
    }

    private void handlePrivateMessage(View view, TextView textView, TextView textView2, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[私]" + string);
        textView.setTextColor(0xff00ff00);
        ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ff00);
        textView2.setText(string2);
        textView2.setTextColor(0xff00ff00);
    }

    private void handleSystemMessage(View view, TextView textView, TextView textView2, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[系统消息]" + string);
        textView.setTextColor(0xffffff00);
        ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xffffff00);
        textView2.setText(string2);
        textView2.setTextColor(0xffffff00);
    }

    private void handleServerMessage(View view, TextView textView, TextView textView2, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        textView.setText((GlobalSetting.INSTANCE.getShowChatTime() ? bundle.getString("TIME") : "") + "[全服消息]" + string);
        textView.setTextColor(0xff00ffff);
        ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ffff);
        textView2.setText(string2);
        textView2.setTextColor(0xff00ffff);
    }

}
