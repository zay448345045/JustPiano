package ly.pp.justpiano3.adapter;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import ly.pp.justpiano3.listener.ChangeRecommandSongClick;
import ly.pp.justpiano3.JPStack;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.OLPlayHall;
import ly.pp.justpiano3.activity.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.OLPlayRoom;
import ly.pp.justpiano3.constant.Consts;

import java.util.List;

public final class ChattingAdapter extends BaseAdapter {
    public Activity activity;
    private final List<Bundle> msgList;
    private final LayoutInflater layoutInflater;
    private final boolean showTime;

    public ChattingAdapter(List<Bundle> list, LayoutInflater layoutInflater, boolean showTime) {
        msgList = list;
        this.layoutInflater = layoutInflater;
        this.showTime = showTime;
        if (JPStack.top() instanceof OLPlayRoom) {
            activity = JPStack.top();
        } else if (JPStack.top() instanceof OLPlayHall) {
            activity = JPStack.top();
        } else if (JPStack.top() instanceof OLPlayKeyboardRoom) {
            activity = JPStack.top();
        }
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
        TextView textView;
        if (view == null) {
            view = layoutInflater.inflate(R.layout.ol_msg_view, null);
        }
        view.setKeepScreenOn(true);
        Bundle bundle = msgList.get(i);
        String string = bundle.getString("U");
        String string2 = bundle.getString("M");
        int i2 = bundle.getInt("T");
        TextView textView2;
        if (i2 == 0) {
            String str1 = bundle.getString("I");
            if (!str1.isEmpty()) {
                str1 = "songs/" + str1 + ".pm";
                ((OLPlayRoom) activity).setdiao(bundle.getInt("D"));
                textView = view.findViewById(R.id.ol_user_text);
                textView.setText((showTime ? bundle.getString("TIME") : "") + "[荐]" + string);
                textView2 = view.findViewById(R.id.ol_msg_text);
                String[] a = ((OLPlayRoom) activity).mo2864a(str1);
                if (a[0] != null && a[1] != null) {
                    textView2.setText(string2 + a[0] + "[难度:" + a[1]);
                    textView2.setTextColor(0xffff0000);
                    if (((OLPlayRoom) activity).getPlayerKind().equals("H")) {
                        textView2.append("(点击选取)");
                        textView2.setOnClickListener(new ChangeRecommandSongClick(this, bundle.getString("I")));
                    }
                }
            }
            textView = null;
        } else if (i2 == 1) {
            int id = bundle.getInt("V");
            if (!string2.startsWith("//")) {
                textView = view.findViewById(R.id.ol_user_text);
                textView.setText((showTime ? bundle.getString("TIME") : "") + "[公]" + string);
                textView2 = view.findViewById(R.id.ol_user_mao);
                TextView textView3 = view.findViewById(R.id.ol_msg_text);
                if (JPStack.top() instanceof OLPlayRoom || JPStack.top() instanceof OLPlayKeyboardRoom) {
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
                    textView = view.findViewById(R.id.ol_user_text);
                    textView.setText((showTime ? bundle.getString("TIME") : "") + "[公]" + string);
                    ((ImageView) view.findViewById(R.id.ol_express_image)).setImageResource(Consts.expressions[Integer.parseInt(string2.substring(2))]);
                } catch (Exception e) {
                    textView = view.findViewById(R.id.ol_user_text);
                    textView.setText((showTime ? bundle.getString("TIME") : "") + "[公]" + string);
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
        } else if (i2 == 2) {
            textView = view.findViewById(R.id.ol_user_text);
            textView.setText((showTime ? bundle.getString("TIME") : "") + "[私]" + string);
            textView.setTextColor(0xff00ff00);
            textView2 = view.findViewById(R.id.ol_msg_text);
            ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ff00);
            textView2.setText(string2);
            textView2.setTextColor(0xff00ff00);
        } else if (i2 == 3) {
            textView = view.findViewById(R.id.ol_user_text);
            textView.setText((showTime ? bundle.getString("TIME") : "") + "[系统消息]" + string);
            textView.setTextColor(0xff00ffff);
            textView2 = view.findViewById(R.id.ol_msg_text);
            ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ffff);
            textView2.setText(string2);
            textView2.setTextColor(0xff00ffff);
        } else {
            if (i2 == 18) {
                textView = view.findViewById(R.id.ol_user_text);
                textView.setText((showTime ? bundle.getString("TIME") : "") + "[全服消息]" + string);
                textView.setTextColor(0xff00ffff);
                textView2 = view.findViewById(R.id.ol_msg_text);
                ((TextView) view.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ffff);
                textView2.setText(string2);
                textView2.setTextColor(0xff00ffff);
            }
            textView = null;
        }
        if (i2 != 3 && textView != null) {
            textView.setOnClickListener(v -> {
                if (activity instanceof OLPlayRoom) {
                    if (string != null) {
                        ((OLPlayRoom) activity).mo2867c(string);
                    }
                } else if (activity instanceof OLPlayHall) {
                    if (string != null) {
                        ((OLPlayHall) activity).mo2832b(string);
                    }
                } else if (activity instanceof OLPlayKeyboardRoom) {
                    if (string != null) {
                        ((OLPlayKeyboardRoom) activity).mo2867c(string);
                    }
                }
            });
        }
        return view;
    }
}
