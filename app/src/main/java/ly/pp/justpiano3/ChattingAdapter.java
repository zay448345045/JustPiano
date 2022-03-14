package ly.pp.justpiano3;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public final class ChattingAdapter extends BaseAdapter {
    Activity activity;
    private final List<Bundle> msgList;
    private final LayoutInflater layoutInflater;
    private boolean showTime;

    ChattingAdapter(List<Bundle> list, LayoutInflater layoutInflater, boolean showTime) {
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
    public final int getCount() {
        return msgList.size();
    }

    @Override
    public final Object getItem(int i) {
        return i;
    }

    public final long getItemId(int i) {
        return i;
    }

    @Override
    public final View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView;
        View inflate = layoutInflater.inflate(R.layout.ol_msg_view, null);
        inflate.setKeepScreenOn(true);
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
                textView = inflate.findViewById(R.id.ol_user_text);
                textView.setText((showTime ? bundle.getString("TIME") : "") + "[荐]" + string);
                textView2 = inflate.findViewById(R.id.ol_msg_text);
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
                textView = inflate.findViewById(R.id.ol_user_text);
                textView.setText((showTime ? bundle.getString("TIME") : "") + "[公]" + string);
                textView2 = inflate.findViewById(R.id.ol_user_mao);
                TextView textView3 = inflate.findViewById(R.id.ol_msg_text);
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
                }
                textView3.setText(string2);
            } else {
                try {
                    textView = inflate.findViewById(R.id.ol_user_text);
                    textView.setText((showTime ? bundle.getString("TIME") : "") + "[公]" + string);
                    ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(Consts.expressions[Integer.parseInt(string2.substring(2))]);
                } catch (Exception e) {
                    textView = inflate.findViewById(R.id.ol_user_text);
                    textView.setText((showTime ? bundle.getString("TIME") : "") + "[公]" + string);
                    switch (string2) {
                        case "//dalao0":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.dalao0);
                            break;
                        case "//7yxg":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.qiyexingguo);
                            break;
                        case "//family":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.family);
                            break;
                        case "//redheart":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_1);
                            break;
                        case "//greenheart":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_2);
                            break;
                        case "//purpleheart":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.couple_3);
                            break;
                        case "//crawl":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.pazou);
                            break;
                        case "//greencircle":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.greenlight);
                            break;
                        case "//jpgq":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.icon);
                            break;
                        case "//soundstop":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.stop);
                            break;
                        case "//greensquare":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.button_down);
                            break;
                        case "//blacksquare":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.button_up);
                            break;
                        case "//rightarrow":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.for_arrow);
                            break;
                        case "//leftarrow":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.back_arrow);
                            break;
                        case "//music":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.music);
                            break;
                        case "//bluecircle":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.redlight);
                            break;
                        case "//jpgq1":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.jpgq1);
                            break;
                        case "//huaji":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.huaji);
                            break;
                        case "//bugaoxing":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.bugaoxing);
                            break;
                        case "//yingyingying":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.yingyingying);
                            break;
                        case "//...":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.shenglve);
                            break;
                        case "//momotou":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.momotou);
                            break;
                        case "//heguozhi":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.heguozhi);
                            break;
                        case "//hen6":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.hen6);
                            break;
                        case "//buyaozheyang":
                            ((ImageView) inflate.findViewById(R.id.ol_express_image)).setImageResource(R.drawable.buyaozheyang);
                            break;
                        default:
                            textView.setTextColor(0xffffffff);
                            ((TextView) inflate.findViewById(R.id.ol_user_mao)).setTextColor(0xffffffff);
                            ((TextView) inflate.findViewById(R.id.ol_msg_text)).setTextColor(0xffffffff);
                            ((TextView) inflate.findViewById(R.id.ol_msg_text)).setText(string2);
                    }
                }
            }
        } else if (i2 == 2) {
            textView = inflate.findViewById(R.id.ol_user_text);
            textView.setText((showTime ? bundle.getString("TIME") : "") + "[私]" + string);
            textView.setTextColor(0xff00ff00);
            textView2 = inflate.findViewById(R.id.ol_msg_text);
            ((TextView) inflate.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ff00);
            textView2.setText(string2);
            textView2.setTextColor(0xff00ff00);
        } else if (i2 == 3) {
            textView = inflate.findViewById(R.id.ol_user_text);
            textView.setText((showTime ? bundle.getString("TIME") : "") + "[系统消息]" + string);
            textView.setTextColor(0xff00ffff);
            textView2 = inflate.findViewById(R.id.ol_msg_text);
            ((TextView) inflate.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ffff);
            textView2.setText(string2);
            textView2.setTextColor(0xff00ffff);
        } else {
            if (i2 == 18) {
                textView = inflate.findViewById(R.id.ol_user_text);
                textView.setText((showTime ? bundle.getString("TIME") : "") + "[全服消息]" + string);
                textView.setTextColor(0xff00ffff);
                textView2 = inflate.findViewById(R.id.ol_msg_text);
                ((TextView) inflate.findViewById(R.id.ol_user_mao)).setTextColor(0xff00ffff);
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
        return inflate;
    }
}
