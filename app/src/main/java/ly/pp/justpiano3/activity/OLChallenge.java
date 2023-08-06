package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import com.google.protobuf.MessageLite;
import ly.pp.justpiano3.*;
import ly.pp.justpiano3.adapter.ChallengeListAdapter;
import ly.pp.justpiano3.handler.android.ChallengeHandler;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.listener.GetPrizeClick;
import ly.pp.justpiano3.service.ConnectionService;
import ly.pp.justpiano3.view.DrawPrizeView;
import protobuf.dto.OnlineChallengeDTO;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class OLChallenge extends BaseActivity implements OnClickListener {
    public JPApplication jpapplication;
    public ConnectionService cs;
    public byte hallID;
    public String hallName;
    public int remainTimes;
    public JPProgressBar jpprogressBar;
    public ChallengeHandler challengeHandler;
    public TextView info;
    public Button start;
    public Button drawPrize;
    public Button viewChallenge;
    public ListView scoreListView;
    public List<HashMap> scoreList = new ArrayList<>();
    private LayoutInflater layoutinflater;

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
        builder.setType(0);
        sendMsg(16, builder.build());
        Intent intent = new Intent(this, OLPlayHall.class);
        intent.putExtra("hallName", hallName);
        intent.putExtra("hallID", hallID);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.startchallenge:
                jpprogressBar.show();
                OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
                builder.setType(2);
                sendMsg(16, builder.build());
                return;
            case R.id.drawPrize:
                builder = OnlineChallengeDTO.newBuilder();
                builder.setType(5);
                sendMsg(16, builder.build());
                return;
            case R.id.viewChallenge:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://justpiano.fun/pages/challenge.html"));
                startActivity(intent);
                return;
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        challengeHandler = new ChallengeHandler(this);
        JPStack.push(this);
        jpprogressBar = new JPProgressBar(this);
        jpprogressBar.show();
        layoutinflater = LayoutInflater.from(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Bundle b = getIntent().getExtras();
        hallID = b.getByte("hallID");
        hallName = b.getString("hallName");
        jpapplication = (JPApplication) getApplication();
        setContentView(R.layout.challenge);
        cs = jpapplication.getConnectionService();
        OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
        builder.setType(1);
        sendMsg(16, builder.build());
        jpapplication.setBackGround(this, "ground", findViewById(R.id.layout));
        TextView title = findViewById(R.id.challengetitle);
        title.setText("每日挑战 (" + DateFormat.getDateInstance().format(new Date()) + ")");
        info = findViewById(R.id.infoview);
        start = findViewById(R.id.startchallenge);
        start.setOnClickListener(this);
        drawPrize = findViewById(R.id.drawPrize);
        drawPrize.setOnClickListener(this);
        viewChallenge = findViewById(R.id.viewChallenge);
        viewChallenge.setOnClickListener(this);
        scoreListView = findViewById(R.id.challenge_score_view);
        scoreListView.setCacheColorHint(0);
    }

    public final void showDrawPrizeDialog(Bundle b) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_draw_prize, findViewById(R.id.dialog));
        TextView t = inflate.findViewById(R.id.ol_prize_result);
        ImageView iv = inflate.findViewById(R.id.ol_prize_pointer);
        DrawPrizeView dp = inflate.findViewById(R.id.ol_draw_prize_pan);
        TextView color = inflate.findViewById(R.id.ol_prize_color);
        color.setVisibility(View.GONE);
        String result = b.getString("N");
        t.setText(result);
        int prizeNum = b.getInt("P");
        if (prizeNum != -1) {
            int prizeType = prizeNum / 100;
            dp.luckyStart(prizeType);
//            if (prizeType == 0) {
//                color.setVisibility(View.VISIBLE);
//                int kuang = prizeNum + 7;
//                if (prizeNum > 17) {
//                    kuang = 2 + (prizeNum - 18) * 5 / 82;
//                }
//                color.setBackgroundResource(Consts.kuang[kuang]);
//            }
            try {
                new JPDialog(this).setTitle("抽取奖励").loadInflate(inflate).setFirstButton("确认领取", new GetPrizeClick(this)).setSecondButton("放弃领取", new DialogDismissClick()).showDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            iv.setVisibility(View.GONE);
            dp.setVisibility(View.GONE);
            try {
                new JPDialog(this).loadInflate(inflate).setTitle("提示").setSecondButton("确定", new DialogDismissClick()).showDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public final void sendMsg(int type, MessageLite msg) {
        if (cs != null) {
            cs.writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开", Toast.LENGTH_SHORT).show();
        }
    }


    public final void mo2907b(ListView listView, List<HashMap> list) {
        listView.setAdapter(new ChallengeListAdapter(list, layoutinflater));
    }

    @Override
    protected void onDestroy() {
        JPStack.pop(this);
        super.onDestroy();
    }
}
