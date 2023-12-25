package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.protobuf.MessageLite;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ly.pp.justpiano3.JPApplication;
import ly.pp.justpiano3.R;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.handler.android.ChallengeHandler;
import ly.pp.justpiano3.utils.ColorUtil;
import ly.pp.justpiano3.utils.OnlineUtil;
import ly.pp.justpiano3.view.DrawPrizeView;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;
import protobuf.dto.OnlineChallengeDTO;

public final class OLChallenge extends OLBaseActivity implements OnClickListener {
    public JPApplication jpApplication;
    public byte hallID;
    public String hallName;
    public int remainTimes;
    public JPProgressBar jpprogressBar;
    public ChallengeHandler challengeHandler;
    public TextView info;
    public ListView scoreListView;
    public List<Map<String, String>> scoreList = new ArrayList<>();
    public LayoutInflater layoutinflater;

    @Override
    public void onBackPressed() {
        if (jpprogressBar != null && jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
        builder.setType(0);
        sendMsg(OnlineProtocolType.CHALLENGE, builder.build());
        Intent intent = new Intent(this, OLPlayHall.class);
        intent.putExtra("hallName", hallName);
        intent.putExtra("hallID", hallID);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.startchallenge) {
            jpprogressBar.show();
            OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
            builder.setType(2);
            sendMsg(OnlineProtocolType.CHALLENGE, builder.build());
        } else if (id == R.id.drawPrize) {
            OnlineChallengeDTO.Builder builder;
            builder = OnlineChallengeDTO.newBuilder();
            builder.setType(5);
            sendMsg(OnlineProtocolType.CHALLENGE, builder.build());
        } else if (id == R.id.viewChallenge) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://" + OnlineUtil.WEBSITE_URL + "/pages/challenge.html"));
            startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        challengeHandler = new ChallengeHandler(this);
        jpprogressBar = new JPProgressBar(this);
        jpprogressBar.show();
        layoutinflater = LayoutInflater.from(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Bundle bundle = getIntent().getExtras();
        hallID = bundle.getByte("hallID");
        hallName = bundle.getString("hallName");
        jpApplication = (JPApplication) getApplication();
        setContentView(R.layout.ol_challenge);
        OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
        builder.setType(1);
        sendMsg(OnlineProtocolType.CHALLENGE, builder.build());
        TextView title = findViewById(R.id.challengetitle);
        title.setText("每日挑战 (" + DateFormat.getDateInstance().format(new Date()) + ")");
        info = findViewById(R.id.infoview);
        findViewById(R.id.startchallenge).setOnClickListener(this);
        findViewById(R.id.drawPrize).setOnClickListener(this);
        findViewById(R.id.viewChallenge).setOnClickListener(this);
        scoreListView = findViewById(R.id.challenge_score_view);
        scoreListView.setCacheColorHint(Color.TRANSPARENT);
    }

    public void showDrawPrizeDialog(Bundle bundle) {
        View inflate = getLayoutInflater().inflate(R.layout.ol_challenge_draw_prize, findViewById(R.id.dialog));
        TextView prizeResultTextView = inflate.findViewById(R.id.ol_prize_result);
        ImageView prizePointerImageView = inflate.findViewById(R.id.ol_prize_pointer);
        DrawPrizeView drawPrizeView = inflate.findViewById(R.id.ol_draw_prize_pan);
        TextView prizeColorView = inflate.findViewById(R.id.ol_prize_color);
        String result = bundle.getString("N");
        prizeResultTextView.setText(result);
        int prizeNum = bundle.getInt("P");
        if (prizeNum != -1) {
            int prizeType = prizeNum / 100;
            drawPrizeView.luckyStart(prizeType);
            if (prizeType == 0) {
                int color = prizeNum + 7;
                if (prizeNum > 17) {
                    color = 2 + (prizeNum - 18) * 5 / 82;
                }
                prizeColorView.setBackgroundResource(ColorUtil.userColor[color]);
                prizeColorView.setVisibility(View.VISIBLE);
            }
            try {
                new JPDialogBuilder(this).setTitle("抽取奖励").loadInflate(inflate)
                        .setFirstButton("确认领取", (dialog, which) -> {
                            if (OnlineUtil.getConnectionService() != null) {
                                OnlineChallengeDTO.Builder builder = OnlineChallengeDTO.newBuilder();
                                builder.setType(6);
                                OnlineUtil.getConnectionService().writeData(OnlineProtocolType.CHALLENGE, builder.build());
                                Toast.makeText(this, "领取奖励成功!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        })
                        .setSecondButton("放弃领取", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            prizePointerImageView.setVisibility(View.GONE);
            drawPrizeView.setVisibility(View.GONE);
            try {
                new JPDialogBuilder(this).loadInflate(inflate).setTitle("提示")
                        .setSecondButton("确定", (dialog, which) -> dialog.dismiss()).buildAndShowDialog();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsg(int type, MessageLite msg) {
        if (OnlineUtil.getConnectionService() != null) {
            OnlineUtil.getConnectionService().writeData(type, msg);
        } else {
            Toast.makeText(this, "连接已断开，请重新登录", Toast.LENGTH_SHORT).show();
        }
    }
}
