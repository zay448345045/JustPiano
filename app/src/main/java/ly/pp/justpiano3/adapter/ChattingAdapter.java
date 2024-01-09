package ly.pp.justpiano3.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.activity.online.OLPlayRoom;
import ly.pp.justpiano3.activity.online.OLRoomActivity;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.GlobalSetting;
import ly.pp.justpiano3.utils.DateUtil;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.ViewUtil;

public final class ChattingAdapter extends BaseAdapter {
    public Activity activity;
    private final List<Bundle> msgList;
    private final LayoutInflater layoutInflater;

    public ChattingAdapter(List<Bundle> list, LayoutInflater layoutInflater) {
        msgList = list;
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
        long start = System.currentTimeMillis();
        Bundle bundle = msgList.get(i);
        int type = bundle.getInt("T");
        view = layoutInflater.inflate(R.layout.ol_msg_view, null);
        TextView userNameTextView = view.findViewById(R.id.ol_user_text);
        TextView msgTextView = view.findViewById(R.id.ol_msg_text);
        String userName = bundle.getString("U");
        switch (type) {
            case OnlineProtocolType.MsgType.SONG_RECOMMEND_MESSAGE ->
                    handleRecommendationMessage(userNameTextView, msgTextView, bundle);
            case OnlineProtocolType.MsgType.PUBLIC_MESSAGE ->
                    handlePublicMessage(view, userNameTextView, msgTextView, bundle);
            case OnlineProtocolType.MsgType.PRIVATE_MESSAGE ->
                    handlePrivateMessage(userNameTextView, msgTextView, bundle);
            case OnlineProtocolType.MsgType.SYSTEM_MESSAGE ->
                    handleSystemMessage(userNameTextView, msgTextView, bundle);
            case OnlineProtocolType.MsgType.ALL_SERVER_MESSAGE ->
                    handleServerMessage(userNameTextView, msgTextView, bundle);
            case OnlineProtocolType.MsgType.STREAM_MESSAGE ->
                    handleStreamMessage(userNameTextView, msgTextView, bundle);
        }
        userNameTextView.setText(userNameTextView.getText() + ": ");
        // 投递通知到房间内处理
        if (type != OnlineProtocolType.MsgType.SYSTEM_MESSAGE && userNameTextView != null) {
            userNameTextView.setOnClickListener(v -> {
                if (activity instanceof OLRoomActivity olRoomActivity && userName != null) {
                    olRoomActivity.setPrivateChatUserName(userName);
                } else if (activity instanceof OLPlayHall olPlayHall && userName != null) {
                    olPlayHall.setPrivateChatUserName(userName);
                }
            });
        }
        // 处理文本框字体大小
        ViewUtil.setTextViewFontSize(GlobalSetting.getChatTextSize(), userNameTextView, msgTextView);
        Log.d("ChattingAdapter", "展示消息列表延迟:" + (System.currentTimeMillis() - start));
        return view;
    }

    private void handleRecommendationMessage(TextView userNameTextView, TextView textView2, Bundle bundle) {
        String info = bundle.getString("I");
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        if (!TextUtils.isEmpty(info) && activity instanceof OLPlayRoom olPlayRoom) {
            olPlayRoom.setTune(bundle.getInt("D"));
            userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[荐]" + userName);
            textView2.setText(message);
            textView2.setTextColor(Color.RED);
            if (olPlayRoom.getPlayerKind().equals("H")) {
                textView2.append(" (点击选取)");
                textView2.setOnClickListener(v -> {
                    Message obtainMessage = olPlayRoom.getHandler().obtainMessage();
                    Bundle songPathBundle = new Bundle();
                    songPathBundle.putString("S", bundle.getString("I"));
                    obtainMessage.setData(songPathBundle);
                    obtainMessage.what = 1;
                    olPlayRoom.getHandler().sendMessage(obtainMessage);
                });
            }
        }
    }

    private void handlePublicMessage(View view, TextView textView, TextView textView3, Bundle bundle) {
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        int id = bundle.getInt("V");
        if (message != null && !message.startsWith("//")) {
            textView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + userName);
            if (activity instanceof OLRoomActivity) {
                switch (id) {
                    case 1 -> {
                        textView.setTextColor(0xffFFFACD);
                        textView3.setTextColor(0xffFFFACD);
                    }
                    case 2 -> {
                        textView.setTextColor(Color.CYAN);
                        textView3.setTextColor(Color.CYAN);
                    }
                    case 3 -> {
                        textView.setTextColor(0xffFF6666);
                        textView3.setTextColor(0xffFF6666);
                    }
                    case 4 -> {
                        textView.setTextColor(0xffFFA500);
                        textView3.setTextColor(0xffFFA500);
                    }
                    case 5 -> {
                        textView.setTextColor(0xffBA55D3);
                        textView3.setTextColor(0xffBA55D3);
                    }
                    case 6 -> {
                        textView.setTextColor(0xfffa60ea);
                        textView3.setTextColor(0xfffa60ea);
                    }
                    case 7 -> {
                        textView.setTextColor(0xffFFD700);
                        textView3.setTextColor(0xffFFD700);
                    }
                    case 8 -> {
                        textView.setTextColor(0xffb7ff72);
                        textView3.setTextColor(0xffb7ff72);
                    }
                    case 9 -> {
                        textView.setTextColor(Color.BLACK);
                        textView3.setTextColor(Color.BLACK);
                    }
                    default -> {
                        if (Objects.equals(userName, "琴娘")) {
                            textView.setTextColor(0xffffd8ec);
                            textView3.setTextColor(0xffffd8ec);
                        } else {
                            textView.setTextColor(Color.WHITE);
                            textView3.setTextColor(Color.WHITE);
                        }
                    }
                }
            } else if (activity instanceof OLPlayHall) {
                textView.setTextColor(0xffFFFACD);
                textView3.setTextColor(0xffFFFACD);
            }
            textView3.setText(message);
        } else {
            try {
                textView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + userName);
                ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(Consts.expressions[Integer.parseInt(message.substring(2))]);
            } catch (Exception e) {
                textView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + userName);
                switch (Objects.requireNonNull(message)) {
                    case "//dalao0" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.dalao0);
                    case "//7yxg" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.qiyexingguo);
                    case "//family" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.family);
                    case "//redheart" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_1);
                    case "//greenheart" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_2);
                    case "//purpleheart" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_3);
                    case "//crawl" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.pazou);
                    case "//greencircle" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.greenlight);
                    case "//jpgq" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.icon);
                    case "//soundstop" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.stop);
                    case "//greensquare" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.button_down);
                    case "//blacksquare" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.button_up);
                    case "//rightarrow" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.for_arrow);
                    case "//leftarrow" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.back_arrow);
                    case "//music" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.music);
                    case "//bluecircle" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.redlight);
                    case "//jpgq1" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.jpgq1);
                    case "//huaji" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.huaji);
                    case "//bugaoxing" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.bugaoxing);
                    case "//yingyingying" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.yingyingying);
                    case "//..." ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.shenglve);
                    case "//momotou" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.momotou);
                    case "//heguozhi" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.heguozhi);
                    case "//hen6" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.hen6);
                    case "//buyaozheyang" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.buyaozheyang);
                    case "//chigua" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.chigua);
                    case "//waku" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.waku);
                    case "//sleep" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.sleep);
                    case "//bailan" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.bailan);
                    case "//duili" ->
                            ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.duili);
                    default -> {
                        textView.setTextColor(Color.WHITE);
                        ((TextView) view.findViewById(R.id.ol_msg_text)).setTextColor(Color.WHITE);
                        ((TextView) view.findViewById(R.id.ol_msg_text)).setText(message);
                    }
                }
            }
        }
    }

    private void handlePrivateMessage(TextView textView, TextView textView2, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        textView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[私]" + string);
        textView.setTextColor(Color.GREEN);
        textView2.setText(string2);
        textView2.setTextColor(Color.GREEN);
    }

    private void handleSystemMessage(TextView textView, TextView textView2, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        textView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[系统消息]" + string);
        textView.setTextColor(Color.YELLOW);
        textView2.setText(string2);
        textView2.setTextColor(Color.YELLOW);
    }

    private void handleServerMessage(TextView textView, TextView textView2, Bundle bundle) {
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        textView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[全服消息]" + string);
        textView.setTextColor(Color.CYAN);
        textView2.setText(string2);
        textView2.setTextColor(Color.CYAN);
    }

    private void handleStreamMessage(TextView userText, TextView msgText, Bundle bundle) {
        String sendUserName = bundle.getString("U");
        String message = bundle.getString("M");
        boolean streamStatus = bundle.getBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS, false);
        if (streamStatus) {
            message += DateUtil.second() % 2 == 0 ? "🔶" : "🔸";
        }
        userText.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + sendUserName);
        msgText.setText(message);
        // 设置标签颜色
        userText.setTextColor(0xffffd8ec);
        msgText.setTextColor(0xffffd8ec);
    }
}
