package ly.pp.justpiano3.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.List;
import java.util.Objects;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.activity.online.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.online.OLPlayRoom;
import ly.pp.justpiano3.activity.online.OLRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.listener.OLSendMailClick;
import ly.pp.justpiano3.utils.JPStack;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.JPDialogBuilder;
import protobuf.dto.OnlineDialogDTO;
import protobuf.dto.OnlineEnterHallDTO;
import protobuf.dto.OnlineUserInfoDialogDTO;

public final class MainGameAdapter extends BaseAdapter {
    public Activity activity;
    private List<Bundle> list;
    private final int type;

    public MainGameAdapter(List<Bundle> list, int type) {
        this.list = list;
        this.type = type;
        activity = JPStack.top();
    }

    private void hallPasswordInputHandle(byte hallId) {
        View inflate = activity.getLayoutInflater().inflate(R.layout.message_send, activity.findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_2);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        inflate.findViewById(R.id.text_1).setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView.setSingleLine(true);
        new JPDialogBuilder(activity).setTitle("输入密码").loadInflate(inflate)
                .setFirstButton("确定", (dialog, which) -> {
                    OnlineEnterHallDTO.Builder builder = OnlineEnterHallDTO.newBuilder();
                    builder.setHallId(hallId);
                    builder.setPassword(String.valueOf(textView.getText()));
                    OnlineUtil.getConnectionService().writeData(OnlineProtocolType.ENTER_HALL, builder.build());
                    dialog.dismiss();
                })
                .setSecondButton("取消", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
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
        TextView fromToTextView;
        RelativeLayout relativeLayout;
        RelativeLayout relativeLayout2;
        Button sendButton;
        int i4;
        switch (type) {
            case 0 -> {
                // 大厅列表
                if (activity == null) {
                    return view;
                }
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_hall_view, null);
                }
                String string = list.get(i).getString("N");
                byte hallId = list.get(i).getByte("I");
                int currentUserCount = list.get(i).getInt("PN");
                int totalUserCount = list.get(i).getInt("TN");
                View hallNameView = view.findViewById(R.id.ol_hall_name);
                hallNameView.setBackgroundResource(R.drawable._none);
                ((TextView) hallNameView).setTextColor((float) currentUserCount / totalUserCount >= 0.8f ? 0xFFCD064B : 0xFFFFBB40);
                ((TextView) hallNameView).setText(string);
                ((TextView) view.findViewById(R.id.ol_hall_pnums)).setText(currentUserCount + "/" + totalUserCount);
                fromToTextView = view.findViewById(R.id.ol_getin_text);
                fromToTextView.setText(currentUserCount < totalUserCount ? "进入" : "已满");
                view.setOnClickListener(v -> {
                    if (list.get(i).getInt("W") > 0) {
                        hallPasswordInputHandle(hallId);
                    } else {
                        OnlineEnterHallDTO.Builder builder = OnlineEnterHallDTO.newBuilder();
                        builder.setHallId(hallId);
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.ENTER_HALL, builder.build());
                    }
                });
            }
            case 1 -> {
                // 好友列表
                if (activity == null) {
                    return view;
                }
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_friend_view, null);
                }
                String userName = list.get(i).getString("F");
                int i3 = list.get(i).getInt("O");
                String gender = list.get(i).getString("S");
                int lv = list.get(i).getInt("LV");
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
                sendButton = view.findViewById(R.id.ol_friend_chat);
                view.findViewById(R.id.ol_friend_room).setVisibility(View.GONE);
                Button button2 = view.findViewById(R.id.ol_friend_game);
                Button dialogButton = view.findViewById(R.id.ol_friend_dialog);
                Button goldSendButton = view.findViewById(R.id.ol_friend_gold_send);
                goldSendButton.setVisibility(View.VISIBLE);
                goldSendButton.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                    builder.setType(2);
                    builder.setName(userName);
                    OnlineUtil.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
                });
                dialogButton.setVisibility(View.VISIBLE);
                dialogButton.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                    builder.setName(userName);
                    OnlineUtil.getConnectionService().writeData(OnlineProtocolType.USER_INFO_DIALOG, builder.build());
                });
                TextView textView2 = view.findViewById(R.id.ol_friend_name);
                ImageView imageView = view.findViewById(R.id.ol_friend_sex);
                imageView.setImageResource(Objects.equals(gender, "f") ? R.drawable.f : R.drawable.m);
                ((TextView) view.findViewById(R.id.ol_friend_level)).setText("LV." + lv);
                textView2.setText(userName);
                if (i3 == 0) {
                    textView2.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    button2.setTextColor(ContextCompat.getColor(activity, R.color.white));
                    sendButton.setTextColor(ContextCompat.getColor(activity, R.color.white));
                } else {
                    textView2.setTextColor(ContextCompat.getColor(activity, R.color.white1));
                    button2.setTextColor(ContextCompat.getColor(activity, R.color.white1));
                    sendButton.setTextColor(ContextCompat.getColor(activity, R.color.white1));
                    button2.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                        builder.setName(userName);
                        builder.setType(0);
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
                    });
                    sendButton.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        if (i3 == 1) {
                            Bundle bundle = new Bundle();
                            Message obtainMessage;
                            if (activity instanceof OLPlayHall olPlayHall) {
                                obtainMessage = olPlayHall.olPlayHallHandler.obtainMessage();
                                obtainMessage.what = 6;
                                bundle.putString("U", userName);
                                obtainMessage.setData(bundle);
                                olPlayHall.olPlayHallHandler.handleMessage(obtainMessage);
                                return;
                            } else if (activity instanceof OLPlayRoom olPlayRoom) {
                                obtainMessage = olPlayRoom.olPlayRoomHandler.obtainMessage();
                                obtainMessage.what = 12;
                                obtainMessage.setData(bundle);
                                bundle.putString("U", userName);
                                olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
                                return;
                            } else if (activity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                                obtainMessage = olPlayKeyboardRoom.olPlayKeyboardRoomHandler.obtainMessage();
                                obtainMessage.what = 12;
                                obtainMessage.setData(bundle);
                                bundle.putString("U", userName);
                                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                                return;
                            } else {
                                return;
                            }
                        }
                        Toast.makeText(activity, "对方不在线,无法进行私聊!", Toast.LENGTH_SHORT).show();
                    });
                }
                view.findViewById(R.id.ol_friend_send).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (activity instanceof OLPlayHall olPlayHall) {
                        olPlayHall.sendMail(userName);
                    } else if (activity instanceof OLPlayHallRoom olPlayHallRoom) {
                        olPlayHallRoom.sendMail(userName, 0);
                    } else if (activity instanceof OLRoomActivity olRoomActivity) {
                        olRoomActivity.sendMail(userName);
                    }
                });
                view.findViewById(R.id.ol_friend_dele).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    Message obtainMessage;
                    if (activity instanceof OLPlayHallRoom olPlayHallRoom) {
                        obtainMessage = olPlayHallRoom.olPlayHallRoomHandler.obtainMessage();
                        obtainMessage.what = 7;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", userName);
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayHall olPlayHall) {
                        obtainMessage = olPlayHall.olPlayHallHandler.obtainMessage();
                        obtainMessage.what = 10;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", userName);
                        olPlayHall.olPlayHallHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayRoom olPlayRoom) {
                        obtainMessage = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage.what = 16;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", userName);
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                        obtainMessage = olPlayKeyboardRoom.olPlayKeyboardRoomHandler.obtainMessage();
                        obtainMessage.what = 16;
                        obtainMessage.arg1 = i;
                        obtainMessage.getData().putString("F", userName);
                        olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                    }
                });
            }
            case 2 -> {
                // 私信列表
                if (activity == null) {
                    return view;
                }
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_mail_view, null);
                }
                if (list.size() > i) {
                    String from = list.get(i).getString("F");
                    String to = list.get(i).getString("T");
                    String mailMessage = list.get(i).getString("M");
                    i4 = 0;
                    if (list.get(i).containsKey("type")) {
                        i4 = list.get(i).getInt("type");
                    }
                    fromToTextView = view.findViewById(R.id.fromTo);
                    if (i4 == 0) {
                        fromToTextView.setText("From:");
                    } else if (i4 == 1) {
                        fromToTextView.setText("To:");
                    }
                    sendButton = view.findViewById(R.id.mail_send);
                    Button buttonX = view.findViewById(R.id.mail_sendX);
                    buttonX.setText("回复");
                    Button mailDeleteButton = view.findViewById(R.id.mail_dele);
                    ((TextView) view.findViewById(R.id.ol_from_user)).setText(from);
                    TextView mailMessageTextView = view.findViewById(R.id.ol_mail_msg);
                    switch (Objects.requireNonNull(mailMessage)) {
                        case "" -> {
                            mailMessageTextView.setText(from + " 请求加你为好友");
                            sendButton.setText("同意");
                        }
                        case "'" -> {
                            mailMessageTextView.setText(from + " 请求与你解除搭档关系");
                            sendButton.setText("解除");
                        }
                        case "''" -> {
                            mailMessageTextView.setText(from + " 请求加入您所在的家族，是否批准?");
                            sendButton.setText("批准");
                        }
                        case "'''" -> {
                            mailMessageTextView.setText(from + " 请求将所在家族的族长转让给您，是否同意?");
                            sendButton.setText("同意");
                        }
                        default -> {
                            mailMessageTextView.setText(mailMessage);
                            sendButton.setText("回复");
                            buttonX.setVisibility(View.GONE);
                        }
                    }
                    ((TextView) view.findViewById(R.id.ol_mail_time)).setText(to);
                    sendButton.setOnClickListener(new OLSendMailClick(this, mailMessage, from));
                    buttonX.setOnClickListener(new OLSendMailClick(this, "0", from));
                    mailDeleteButton.setOnClickListener(v -> ((OLPlayHallRoom) activity).updateMailListShow(i));
                }
            }
            case 3 -> {
                // 大厅用户列表
                if (activity == null) {
                    return view;
                }
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.ol_friend_view, null);
                }
                String userName = list.get(i).getString("U");
                String gender = list.get(i).getString("S");
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
                fromToTextView = view.findViewById(R.id.ol_friend_room);
                switch (i4) {
                    case 0 -> fromToTextView.setText("大厅");
                    case -1 -> fromToTextView.setText("考级");
                    case -2 -> fromToTextView.setText("挑战");
                    default -> fromToTextView.setText(i4 + "房");
                }
                sendButton = view.findViewById(R.id.ol_friend_chat);
                ImageView imageView2 = view.findViewById(R.id.ol_friend_sex);
                imageView2.setImageResource(Objects.equals(gender, "f") ? R.drawable.f : R.drawable.m);
                ((TextView) view.findViewById(R.id.ol_friend_level)).setText("LV." + list.get(i).getInt("LV"));
                sendButton.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    Bundle bundle = new Bundle();
                    Message obtainMessage;
                    if (activity instanceof OLPlayHall olPlayHall) {
                        obtainMessage = olPlayHall.olPlayHallHandler.obtainMessage();
                        obtainMessage.what = 6;
                        bundle.putString("U", userName);
                        obtainMessage.setData(bundle);
                        olPlayHall.olPlayHallHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayRoom olPlayRoom) {
                        obtainMessage = olPlayRoom.olPlayRoomHandler.obtainMessage();
                        obtainMessage.what = 12;
                        obtainMessage.setData(bundle);
                        bundle.putString("U", userName);
                        olPlayRoom.olPlayRoomHandler.handleMessage(obtainMessage);
                    } else if (activity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                        obtainMessage = olPlayKeyboardRoom.olPlayKeyboardRoomHandler.obtainMessage();
                        obtainMessage.what = 12;
                        obtainMessage.setData(bundle);
                        bundle.putString("U", userName);
                        olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(obtainMessage);
                    }
                });
                fromToTextView = view.findViewById(R.id.ol_friend_game);
                if (activity instanceof OLPlayHall) {
                    fromToTextView.setText("找TA");
                    fromToTextView.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                        builder.setType(0);
                        builder.setName(userName);
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
                    });
                } else if (activity instanceof OLRoomActivity) {
                    fromToTextView.setText("邀请");
                    fromToTextView.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        OnlineDialogDTO.Builder builder = OnlineDialogDTO.newBuilder();
                        builder.setType(1);
                        builder.setName(userName);
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.DIALOG, builder.build());
                    });
                }
                ((TextView) view.findViewById(R.id.ol_friend_name)).setText(userName);
                if (Objects.equals(userName, "琴娘")) {
                    view.findViewById(R.id.ol_friend_send).setVisibility(View.INVISIBLE);
                }
                view.findViewById(R.id.ol_friend_send).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (activity instanceof OLPlayHall olPlayHall) {
                        olPlayHall.sendMail(userName);
                    } else if (activity instanceof OLPlayHallRoom olPlayHallRoom) {
                        olPlayHallRoom.sendMail(userName, 0);
                    } else if (activity instanceof OLRoomActivity olRoomActivity) {
                        olRoomActivity.sendMail(userName);
                    }
                });
                sendButton = view.findViewById(R.id.ol_friend_dele);
                sendButton.setText("查看资料");
                sendButton.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (OnlineUtil.getConnectionService() != null && !userName.isEmpty()) {
                        OnlineUserInfoDialogDTO.Builder builder = OnlineUserInfoDialogDTO.newBuilder();
                        builder.setName(userName);
                        OnlineUtil.getConnectionService().writeData(OnlineProtocolType.USER_INFO_DIALOG, builder.build());
                    }
                });
            }
        }
        return view;
    }
}
