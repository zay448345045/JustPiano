package ly.pp.justpiano3;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public final class MainGameAdapter extends BaseAdapter {
    Activity activity;
    ConnectionService connectionService;
    private List<Bundle> list;
    private JPApplication jpapplication;
    private int type;

    public MainGameAdapter(List<Bundle> list, JPApplication jPApplication, int i, Activity act) {
        this.list = list;
        jpapplication = jPApplication;
        connectionService = jPApplication.getConnectionService();
        type = i;
        if (act instanceof OLPlayHall) {
            JPStack.create();
            activity = JPStack.top();
        } else if (act instanceof OLPlayRoom) {
            JPStack.create();
            activity = JPStack.top();
        } else if (act instanceof OLPlayHallRoom) {
            JPStack.create();
            activity = JPStack.top();
        }
    }

    private static void m3978a(MainGameAdapter mainGameAdapter, byte b) {
        View inflate = mainGameAdapter.activity.getLayoutInflater().inflate(R.layout.message_send, mainGameAdapter.activity.findViewById(R.id.dialog));
        TextView textView = inflate.findViewById(R.id.text_2);
        TextView textView2 = inflate.findViewById(R.id.title_1);
        inflate.findViewById(R.id.text_1).setVisibility(View.GONE);
        textView2.setVisibility(View.GONE);
        textView.setSingleLine(true);
        new JPDialog(mainGameAdapter.activity).setTitle("输入密码").loadInflate(inflate).setFirstButton("确定", new RoomPasswordClick(mainGameAdapter, textView, b)).setSecondButton("取消", new DialogDismissClick()).showDialog();
    }

    final void updateList(List<Bundle> list) {
        this.list = list;
    }

    @Override
    public final int getCount() {
        return list.size();
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    @Override
    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
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
                if (i2 < i3) {
                    z = false;
                }
                ((TextView) view.findViewById(R.id.ol_hall_name)).setText(string);
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
                        connectionService.writeData((byte) 29, (byte) 0, b, "", null);
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
                    textView2.setTextColor(jpapplication.getResources().getColor(R.color.white));
                    button2.setTextColor(jpapplication.getResources().getColor(R.color.white));
                    button.setTextColor(jpapplication.getResources().getColor(R.color.white));
                } else {
                    textView2.setTextColor(jpapplication.getResources().getColor(R.color.white1));
                    button2.setTextColor(jpapplication.getResources().getColor(R.color.white1));
                    button.setTextColor(jpapplication.getResources().getColor(R.color.white1));
                    button2.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("F", string2);
                            jSONObject.put("T", 0);
                            connectionService.writeData((byte) 37, (byte) 0, (byte) 0, jSONObject.toString(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                            } else {
                                return;
                            }
                        }
                        Toast.makeText(jpapplication, "对方不在线,无法进行私聊!", Toast.LENGTH_SHORT).show();
                    });
                }
                view.findViewById(R.id.ol_friend_send).setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (activity instanceof OLPlayHall) {
                        ((OLPlayHall) activity).mo2830a(string2);
                    } else if (activity instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) activity).sendMail(string2, 0);
                    } else if (activity instanceof OLPlayRoom) {
                        ((OLPlayRoom) activity).mo2865b(string2);
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
                    }
                });
                textView = view.findViewById(R.id.ol_friend_game);
                if (activity instanceof OLPlayHall) {
                    textView.setText("找TA");
                    textView.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("F", string7);
                            jSONObject.put("T", 0);
                            connectionService.writeData((byte) 37, (byte) 0, (byte) 0, jSONObject.toString(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                } else if (activity instanceof OLPlayRoom) {
                    textView.setText("邀请");
                    textView.setOnClickListener(v -> {
                        relativeLayout2.setVisibility(View.GONE);
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("F", string7);
                            jSONObject.put("T", 1);
                            connectionService.writeData((byte) 37, (byte) 0, (byte) 0, jSONObject.toString(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });
                }
                ((TextView) view.findViewById(R.id.ol_friend_name)).setText(string7);
                view.findViewById(R.id.ol_friend_send).setVisibility(View.INVISIBLE);
                button = view.findViewById(R.id.ol_friend_dele);
                button.setText("查看资料");
                button.setOnClickListener(v -> {
                    relativeLayout2.setVisibility(View.GONE);
                    if (connectionService != null && !string7.isEmpty()) {
                        JSONObject jSONObject = new JSONObject();
                        try {
                            jSONObject.put("C", 0);
                            jSONObject.put("F", string7);
                            connectionService.writeData((byte) 2, (byte) 0, (byte) 0, jSONObject.toString(), null);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
        return view;
    }
}
