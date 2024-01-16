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
        if (type != OnlineProtocolType.MsgType.SYSTEM_MESSAGE) {
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

    private void handleRecommendationMessage(TextView userNameTextView, TextView msgTextView, Bundle bundle) {
        String info = bundle.getString("I");
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        if (!TextUtils.isEmpty(info) && activity instanceof OLPlayRoom olPlayRoom) {
            olPlayRoom.setTune(bundle.getInt("D"));
            userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[荐]" + userName);
            msgTextView.setText(message);
            msgTextView.setTextColor(Color.RED);
            if (olPlayRoom.getPlayerKind().equals("H")) {
                msgTextView.append(" (点击选取)");
                msgTextView.setOnClickListener(v -> {
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

    private void handlePublicMessage(View view, TextView userNameTextView, TextView msgTextView, Bundle bundle) {
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        int id = bundle.getInt("V");
        if (message != null && !message.startsWith("//")) {
            userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + userName);
            if (activity instanceof OLRoomActivity) {
                switch (id) {
                    case 1 -> {
                        userNameTextView.setTextColor(0xffFFFACD);
                        msgTextView.setTextColor(0xffFFFACD);
                    }
                    case 2 -> {
                        userNameTextView.setTextColor(Color.CYAN);
                        msgTextView.setTextColor(Color.CYAN);
                    }
                    case 3 -> {
                        userNameTextView.setTextColor(0xffFF6666);
                        msgTextView.setTextColor(0xffFF6666);
                    }
                    case 4 -> {
                        userNameTextView.setTextColor(0xffFFA500);
                        msgTextView.setTextColor(0xffFFA500);
                    }
                    case 5 -> {
                        userNameTextView.setTextColor(0xffBA55D3);
                        msgTextView.setTextColor(0xffBA55D3);
                    }
                    case 6 -> {
                        userNameTextView.setTextColor(0xfffa60ea);
                        msgTextView.setTextColor(0xfffa60ea);
                    }
                    case 7 -> {
                        userNameTextView.setTextColor(0xffFFD700);
                        msgTextView.setTextColor(0xffFFD700);
                    }
                    case 8 -> {
                        userNameTextView.setTextColor(0xffb7ff72);
                        msgTextView.setTextColor(0xffb7ff72);
                    }
                    case 9 -> {
                        userNameTextView.setTextColor(Color.BLACK);
                        msgTextView.setTextColor(Color.BLACK);
                    }
                    default -> {
                        if (Objects.equals(userName, "琴娘")) {
                            userNameTextView.setTextColor(0xffffd8ec);
                            msgTextView.setTextColor(0xffffd8ec);
                        } else {
                            userNameTextView.setTextColor(Color.WHITE);
                            msgTextView.setTextColor(Color.WHITE);
                        }
                    }
                }
            } else if (activity instanceof OLPlayHall) {
                userNameTextView.setTextColor(0xffFFFACD);
                msgTextView.setTextColor(0xffFFFACD);
            }
            msgTextView.setText(message);
        } else if (message != null) {
            ImageView expressImageView = view.findViewById(R.id.ol_express_image);
            userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + userName);
            try {
                expressImageView.setImageResource(Consts.expressions[Integer.parseInt(message.substring(2))]);
            } catch (Exception e) {
                picExpressHandle(userNameTextView, msgTextView, message, expressImageView);
            }
        }
    }

    private static void picExpressHandle(TextView userNameTextView, TextView msgTextView, String message, ImageView expressImageView) {
        switch (message) {
            case "//dalao0" ->
                    expressImageView.setImageResource(R.drawable.dalao0);
            case "//7yxg" ->
                    expressImageView.setImageResource(R.drawable.qiyexingguo);
            case "//family" ->
                    expressImageView.setImageResource(R.drawable.family);
            case "//redheart" ->
                    expressImageView.setImageResource(R.drawable.couple_1);
            case "//greenheart" ->
                    expressImageView.setImageResource(R.drawable.couple_2);
            case "//purpleheart" ->
                    expressImageView.setImageResource(R.drawable.couple_3);
            case "//crawl" ->
                    expressImageView.setImageResource(R.drawable.pazou);
            case "//greencircle" ->
                    expressImageView.setImageResource(R.drawable.greenlight);
            case "//jpgq" ->
                    expressImageView.setImageResource(R.drawable.icon);
            case "//soundstop" ->
                    expressImageView.setImageResource(R.drawable.stop);
            case "//greensquare" ->
                    expressImageView.setImageResource(R.drawable.button_down);
            case "//blacksquare" ->
                    expressImageView.setImageResource(R.drawable.button_up);
            case "//rightarrow" ->
                    expressImageView.setImageResource(R.drawable.for_arrow);
            case "//leftarrow" ->
                    expressImageView.setImageResource(R.drawable.back_arrow);
            case "//music" ->
                    expressImageView.setImageResource(R.drawable.music);
            case "//bluecircle" ->
                    expressImageView.setImageResource(R.drawable.redlight);
            case "//jpgq1" ->
                    expressImageView.setImageResource(R.drawable.jpgq1);
            case "//huaji" ->
                    expressImageView.setImageResource(R.drawable.huaji);
            case "//bugaoxing" ->
                    expressImageView.setImageResource(R.drawable.bugaoxing);
            case "//yingyingying" ->
                    expressImageView.setImageResource(R.drawable.yingyingying);
            case "//..." ->
                    expressImageView.setImageResource(R.drawable.shenglve);
            case "//momotou" ->
                    expressImageView.setImageResource(R.drawable.momotou);
            case "//heguozhi" ->
                    expressImageView.setImageResource(R.drawable.heguozhi);
            case "//hen6" ->
                    expressImageView.setImageResource(R.drawable.hen6);
            case "//buyaozheyang" ->
                    expressImageView.setImageResource(R.drawable.buyaozheyang);
            case "//chigua" ->
                    expressImageView.setImageResource(R.drawable.chigua);
            case "//waku" ->
                    expressImageView.setImageResource(R.drawable.waku);
            case "//sleep" ->
                    expressImageView.setImageResource(R.drawable.sleep);
            case "//bailan" ->
                    expressImageView.setImageResource(R.drawable.bailan);
            case "//duili" ->
                    expressImageView.setImageResource(R.drawable.duili);
            default -> {
                userNameTextView.setTextColor(Color.WHITE);
                msgTextView.setTextColor(Color.WHITE);
                msgTextView.setText(message);
            }
        }
    }

    private void handlePrivateMessage(TextView userNameTextView, TextView msgTextView, Bundle bundle) {
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[私]" + userName);
        userNameTextView.setTextColor(Color.GREEN);
        msgTextView.setText(message);
        msgTextView.setTextColor(Color.GREEN);
    }

    private void handleSystemMessage(TextView userNameTextView, TextView msgTextView, Bundle bundle) {
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[系统消息]" + userName);
        userNameTextView.setTextColor(Color.YELLOW);
        msgTextView.setText(message);
        msgTextView.setTextColor(Color.YELLOW);
    }

    private void handleServerMessage(TextView userNameTextView, TextView msgTextView, Bundle bundle) {
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[全服消息]" + userName);
        userNameTextView.setTextColor(Color.CYAN);
        msgTextView.setText(message);
        msgTextView.setTextColor(Color.CYAN);
    }

    private void handleStreamMessage(TextView userNameTextView, TextView msgTextView, Bundle bundle) {
        String userName = bundle.getString("U");
        String message = bundle.getString("M");
        if (bundle.getBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS, false)) {
            message += DateUtil.second() % 2 == 0 ? "🔶" : "🔸";
        }
        userNameTextView.setText((GlobalSetting.getShowChatTime() ? bundle.getString("TIME") : "") + "[公]" + userName);
        userNameTextView.setTextColor(0xffffd8ec);
        msgTextView.setText(message);
        msgTextView.setTextColor(0xffffd8ec);
    }
}
