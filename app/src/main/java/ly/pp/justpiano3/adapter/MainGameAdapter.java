package ly.pp.justpiano3.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.core.content.ContextCompat;
import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.*;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.listener.HallPasswordClick;
import ly.pp.justpiano3.listener.OLSendMailClick;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.view.JPDialog;
import protobuf.dto.OnlineDialogDTO;
import protobuf.dto.OnlineEnterHallDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

import java.util.List;

public final class MainGameAdapter extends BaseAdapter {
    public Activity activity;
    public ConnectionService connectionService;
    private List<Bundle> list;
    private final JPApplication jpApplication;
    private final int type;

    public MainGameAdapter(List<Bundle> list, JPApplication jpApplication, int i, Activity act) {
        this.list = list;
        this.jpApplication = jpApplication;
        connectionService = jpApplication.getConnectionService();
        type = i;
        activity = JPStack.top();
    }

    private static void m3978a(MainGameAdapter mainGameAdapter, byte b) {
        View inflate = mainGameAdapter.activity.getLayoutInflater().inflate(R.layout.message_send, mainGameAdapter.activity.findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_2);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        inflate.findViewById(R.id.text_1).setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView.setSingleLine(true);
        new JPDialog(mainGameAdapter.activity).setTitle("输入密码").loadInflate(inflate).setFirstButton("确定", new HallPasswordClick(mainGameAdapter, textView, b)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    public void updateList(List<Bundle> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        boolean z = true;
        int i2;
        int i3;
        TextView textView;
        RelativeLayout relativeLayout;
        RelativeLayout relativeLayout2;
        Button button;
        int i4;
        switch (type) {
            case 0:
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_hall_view, null);
                }
                view.setKeepScreenOn(true);
                CharSequence string = list.get(i).getString("N");
                byte b = list.get(i).getByte("I");
                i2 = list.get(i).getInt("PN");
                i3 = list.get(i).getInt("TN");
                View hallNameView = view.findViewById(R.id.ol_hall_name);
                hallNameView.setBackgroundResource(R.drawable._none);
                if ((float) i2 / i3 >= 0.8f) {
                    ((TextView) hallNameView).setTextColor(0xFFCD064B);
                } else {
                    ((TextView) hallNameView).setTextColor(0xFFFFBB40);
                }
                if (i2 < i3) {
                    z = false;
                }
                ((TextView) hallNameView).setText(string);
                ((TextView) view.findViewById(R.id.ol_hall_pnums)).setText(i2 + "/" + i3);
                textView = view.findViewById(R.id.ol_getin_text);
                if (z) {
                    textView.setText("已满");
                } else {
                    textView.setText("进入");
                }
                view.setOnClickListener(v -> {
                    if (list.get(i).getInt("W") > 0) {
                        MainGameAdapter.m3978a(MainGameAdapter.this, b);
                    } else {
                        OnlineEnterHallDTO.Builder builder = OnlineEnterHallDTO.newBuilder();
                        builder.setHallId(b);
                        connectionService.writeData(OnlineProtocolType.ENTER_HALL, builder.build());
                    }
                });
                break;
            case 1:
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_friend_view, null);
                }
                view.setKeepScreenOn(true);
                String string2 = list.get(i).getString("F");
                i3 = list.get(i).getInt("O");
                String string3 = list.get(i).getString("S");
                int i5 = list.get(i).getInt("LV");
                relativeLayout = view.findViewById(R.id.ol_friend_bar);
                relativeLayout2 = view.findViewById(R.id.ol_friend_drop);
                relativeLayout2.setVisibility(View.GONE);
                relativeLayout.setOnClickListener(v -> {
                    int visibility = relativeLayout2.getVisibility();
                    if (visibility == View.GONE) {
                        relativeLayout2.setVisibility(View.VISIBLE);
                    } else if (visibility == View.VISIBLE) {
                        relativeLayout2.setVisibility(View.GONE);
                    }
                });
                button = view.findViewById(R.id.ol_friend_chat);
                view.findViewById(R.id.ol_friend_room).setVisibility(View.GONE);
                Button button2 = view.findViewById(R.id.ol_friend_game);
                Button dialogButton = view.findViewById(R.id.ol_friend_dialog);
                Button goldSendButton = view.findViewById(R.id.ol_friend_gold_send);
                goldSendButton.setVisibility(View.VISIBLE);
                goldSendButton.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                    builder.setType(2);
                    builder.setName(string2);
                    connectionService.writeData(OnlineProtocolType.DIALOG, builder.build());
                });
                dialogButton.setVisibility(View.VISIBLE);
                dialogButton.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                    builder.setName(string2);
                    connectionService.writeData(OnlineProtocolType.USER_INFO_DIALOG, builder.build());
                });
                TextView textView2 = view.findViewById(R.id.ol_friend_name);
                ImageView imageView = view.findViewById(R.id.ol_friend_sex);
                if (string3.equals("f")) {
                    imageView.setImageResource(R.drawable.f);
                } else {
                    imageView.setImageResource(R.drawable.m);
                }
                ((TextView) view.findViewById(R.id.ol_friend_level)).setText("LV." + i5);
                textView2.setText(string2);
                if (i3 == 0) {
                    textView2.setTextColor(ContextCompat.getColor(jpApplication, R.color.white));
                    button2.setTextColor(ContextCompat.getColor(jpApplication, R.color.white));
                    button.setTextColor(ContextCompat.getColor(jpApplication, R.color.white));
                } else {
                    textView2.setTextColor(ContextCompat.getColor(jpApplication, R.color.white1));
                    button2.setTextColor(ContextCompat.getColor(jpApplication, R.color.white1));
                    button.setTextColor(ContextCompat.getColor(jpApplication, R.color.white1));
                    button2.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                        builder.setName(string2);
                        builder.setType(0);
                        connectionService.writeData(OnlineProtocolType.DIALOG, builder.build());
                    });
                    button.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        if (i3 == 1) {
                            Bundle bundle = new Bundle();
                            Message obtainMessage;
                            if (activity instanceof OLPlayHall) {
                                obtainMessage = ((OLPlayHall) activity).olPlayHallHandler.obtainMessage();
                                obtainMessage.what = 6;
                                bundle.putString("U", string2);
                                obtainMessage.setData(bundle);
                                ((OLPlayHall) activity).olPlayHallHandler.handleMessage(obtainMessage);
                                return;
                            } else if (activity instanceof OLPlayRoom) {
                                obtainMessage = ((OLPlayRoom) activity).olPlayRoomHandler.obtainMessage();
                                obtainMessage.what = 12;
                                obtainMessage.setData(bundle);
                                bundle.putString("U", string2);
                                ((OLPlayRoom) activity).olPlayRoomHandler.handleMessage(obtainMessage);
                                return;
                            } else if (activity instanceof OLPlayKeyboardRoom) {
                                obtainMessage = ((OLPlayKeyboardRoom) activity).olPlayKeyboardRoomHandler.obtainMessage();
                                obtainMessage.what = 12;
                                obtainMessage.setData(bundle);
                                bundle.putString("U", string2);
                                ((OLPlayKeyboardRoom) activity).olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                                return;
                            } else {
                                return;
                            }
                        }
                        Toast.makeText(jpApplication, "对方不在线,无法进行私聊!", Toast.LENGTH_SHORT).show();
                    });
                }
                view.findViewById(R.id.ol_friend_send).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (activity instanceof OLPlayHall) {
                        ((OLPlayHall) activity).sendMail(string2);
                    } else if (activity instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) activity).sendMail(string2, 0);
                    } else if (activity instanceof OLPlayRoomActivity) {
                        ((OLPlayRoomActivity) activity).sendMail(string2);
                    }
                });
                view.findViewById(R.id.ol_friend_dele).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    Message obtainMessage;
                    if (activity instanceof OLPlayHallRoom) {
                        obtainMessage = ((OLPlayHallRoom) activity).olPlayHallRoomHandler.obtainMessage();
                        obtainMessage.what = 7;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", string2);
                        ((OLPlayHallRoom) activity).olPlayHallRoomHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayHall) {
                        obtainMessage = ((OLPlayHall) activity).olPlayHallHandler.obtainMessage();
                        obtainMessage.what = 10;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", string2);
                        ((OLPlayHall) activity).olPlayHallHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayRoom) {
                        obtainMessage = ((OLPlayRoom) activity).olPlayRoomHandler.obtainMessage();
                        obtainMessage.what = 16;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", string2);
                        ((OLPlayRoom) activity).olPlayRoomHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayKeyboardRoom) {
                        obtainMessage = ((OLPlayKeyboardRoom) activity).olPlayKeyboardRoomHandler.obtainMessage();
                        obtainMessage.what = 16;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", string2);
                        ((OLPlayKeyboardRoom) activity).olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                    }
                });
                break;
            case 2:
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_mail_view, null);
                }
                view.setKeepScreenOn(true);
                if (list.size() > i) {
                    String string4 = list.get(i).getString("F");
                    CharSequence string5 = list.get(i).getString("T");
                    String string6 = list.get(i).getString("M");
                    i4 = 0;
                    if (list.get(i).containsKey("type")) {
                        i4 = list.get(i).getInt("type");
                    }
                    textView = view.findViewById(R.id.fromTo);
                    if (i4 == 0) {
                        textView.setText("From:");
                    } else if (i4 == 1) {
                        textView.setText("To:");
                    }
                    button = view.findViewById(R.id.mail_send);
                    Button buttonX = view.findViewById(R.id.mail_sendX);
                    buttonX.setText("回复");
                    Button button3 = view.findViewById(R.id.mail_dele);
                    ((TextView) view.findViewById(R.id.ol_from_user)).setText(string4);
                    TextView textView3 = view.findViewById(R.id.ol_mail_msg);
                    switch (string6) {
                        case "":
                            textView3.setText(string4 + " 请求加你为好友");
                            button.setText("同意");
                            break;
                        case "'":
                            textView3.setText(string4 + " 请求与你解除搭档关系");
                            button.setText("解除");
                            break;
                        case "''":
                            textView3.setText(string4 + " 请求加入您所在的家族，是否批准?");
                            button.setText("批准");
                            break;
                        default:
                            textView3.setText(string6);
                            button.setText("回复");
                            buttonX.setVisibility(View.GONE);
                            break;
                    }
                    ((TextView) view.findViewById(R.id.ol_mail_time)).setText(string5);
                    button.setOnClickListener(new OLSendMailClick(this, string6, string4));
                    buttonX.setOnClickListener(new OLSendMailClick(this, "0", string4));
                    button3.setOnClickListener(v -> ((OLPlayHallRoom) activity).mo2840a(i));
                    break;
                }
                break;
            case 3:
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_friend_view, null);
                }
                view.setKeepScreenOn(true);
                String string7 = list.get(i).getString("U");
                String string8 = list.get(i).getString("S");
                i2 = list.get(i).getInt("LV");
                i4 = list.get(i).getInt("R");
                relativeLayout = view.findViewById(R.id.ol_friend_bar);
                relativeLayout2 = view.findViewById(R.id.ol_friend_drop);
                relativeLayout2.setVisibility(View.GONE);
                relativeLayout.setOnClickListener(v -> {
                    int visibility = relativeLayout2.getVisibility();
                    if (visibility == View.GONE) {
                        relativeLayout2.setVisibility(View.VISIBLE);
                    } else if (visibility == View.VISIBLE) {
                        relativeLayout2.setVisibility(View.GONE);
                    }
                });
                textView = view.findViewById(R.id.ol_friend_room);
                switch (i4) {
                    case 0:
                        textView.setText("大厅");
                        break;
                    case -1:
                        textView.setText("考级");
                        break;
                    case -2:
                        textView.setText("挑战");
                        break;
                    default:
                        textView.setText(i4 + "房");
                        break;
                }
                button = view.findViewById(R.id.ol_friend_chat);
                ImageView imageView2 = view.findViewById(R.id.ol_friend_sex);
                if (string8.equals("f")) {
                    imageView2.setImageResource(R.drawable.f);
                } else {
                    imageView2.setImageResource(R.drawable.m);
                }
                ((TextView) view.findViewById(R.id.ol_friend_level)).setText("LV." + i2);
                button.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    Message obtainMessage;
                    if (activity instanceof OLPlayHall) {
                        obtainMessage = ((OLPlayHall) activity).olPlayHallHandler.obtainMessage();
                        obtainMessage.what = 6;
                        bundle.putString("U", string7);
                        obtainMessage.setData(bundle);
                        ((OLPlayHall) activity).olPlayHallHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayRoom) {
                        obtainMessage = ((OLPlayRoom) activity).olPlayRoomHandler.obtainMessage();
                        obtainMessage.what = 12;
                        obtainMessage.setData(bundle);
                        bundle.putString("U", string7);
                        ((OLPlayRoom) activity).olPlayRoomHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayKeyboardRoom) {
                        obtainMessage = ((OLPlayKeyboardRoom) activity).olPlayKeyboardRoomHandler.obtainMessage();
                        obtainMessage.what = 12;
                        obtainMessage.setData(bundle);
                        bundle.putString("U", string7);
                        ((OLPlayKeyboardRoom) activity).olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                    }
                });
                textView = view.findViewById(R.id.ol_friend_game);
                if (activity instanceof OLPlayHall) {
                    textView.setText("找TA");
                    textView.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                        builder.setType(0);
                        builder.setName(string7);
                        connectionService.writeData(OnlineProtocolType.DIALOG, builder.build());
                    });
                } else if (activity instanceof OLPlayRoomActivity) {
                    textView.setText("邀请");
                    textView.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                        builder.setType(1);
                        builder.setName(string7);
                        connectionService.writeData(OnlineProtocolType.DIALOG, builder.build());
                    });
                }
                ((TextView) view.findViewById(R.id.ol_friend_name)).setText(string7);
                if (string7.equals("琴娘")) {
                    view.findViewById(R.id.ol_friend_send).setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.ol_friend_send).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (activity instanceof OLPlayHall) {
                        ((OLPlayHall) activity).sendMail(string7);
                    } else if (activity instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) activity).sendMail(string7, 0);
                    } else if (activity instanceof OLPlayRoomActivity) {
                        ((OLPlayRoomActivity) activity).sendMail(string7);
                    }
                });
                button = view.findViewById(R.id.ol_friend_dele);
                button.setText("查看资料");
                button.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (connectionService != null && !string7.isEmpty()) {
                        OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                        builder.setName(string7);
                        connectionService.writeData(OnlineProtocolType.USER_INFO_DIALOG, builder.build());
                    }
                });
                break;
        }
        return view;
    }
}
