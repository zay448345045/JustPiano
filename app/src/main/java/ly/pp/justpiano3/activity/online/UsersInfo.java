package ly.pp.justpiano3.activity.online;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ly.pp.justpiano3.R;
import ly.pp.justpiano3.activity.BaseActivity;
import ly.pp.justpiano3.constant.Consts;
import ly.pp.justpiano3.listener.ChangePasswordClick;
import ly.pp.justpiano3.task.UserInfoChangeTask;
import ly.pp.justpiano3.task.UsersInfoGetTask;
import ly.pp.justpiano3.thread.PictureHandle;
import ly.pp.justpiano3.view.JPDialogBuilder;
import ly.pp.justpiano3.view.JPProgressBar;

public final class UsersInfo extends BaseActivity implements Callback, OnClickListener {
    public boolean autoLogin;
    public boolean rememberNewPassword;
    public JPProgressBar jpprogressBar;
    public String signature;
    public int age;
    private PictureHandle pictureHandle;
    private String name = "";
    private String face = "";
    private String sex = "";
    private String winner;
    private String score;
    private TextView accountText;
    private TextView nameText;
    private TextView sexText;
    private TextView ageText;
    private TextView winnerNumText;
    private TextView scoreText;
    private TextView pSignText;
    private ImageView faceImage;

    private final ActivityResultLauncher<Intent> cropPictureLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Bundle extras = result.getData().getExtras();
                    if (extras != null) {
                        Bitmap bitmap = extras.getParcelable("data");
                        faceImage.setImageDrawable(new BitmapDrawable(bitmap));
//                    try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
//                        new UserFaceChangeTask(this).execute(
//                                "http://" + OnlineUtil.server + ":8910/JustPianoServer/server/UploadFace", filePath, accountJpg);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    }
                }
            });

    public static void updateUserInfo(UsersInfo usersInfo, String str) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            usersInfo.accountText.setText(OLBaseActivity.getAccountName());
            usersInfo.name = jsonObject.getString("uk");
            usersInfo.nameText.setText(usersInfo.name);
            usersInfo.face = jsonObject.getString("fi");
            usersInfo.faceImage.setTag(usersInfo.face);
            usersInfo.pictureHandle.setBitmap(usersInfo.faceImage, null);
            usersInfo.sex = jsonObject.getString("sx");
            if (usersInfo.sex.equals("m")) {
                usersInfo.sexText.setText("男");
            } else {
                usersInfo.sexText.setText("女");
            }
            usersInfo.age = jsonObject.getInt("age");
            if (usersInfo.age < 1900) {
                usersInfo.age = 1990;
            }
            usersInfo.ageText.setText(String.valueOf(usersInfo.age));
            usersInfo.winner = jsonObject.get("nu").toString();
            usersInfo.winnerNumText.setText(usersInfo.winner);
            usersInfo.score = jsonObject.get("sc").toString();
            usersInfo.scoreText.setText(usersInfo.score);
            usersInfo.signature = jsonObject.get("ms").toString();
            if (usersInfo.signature.isEmpty()) {
                usersInfo.pSignText.setText("未设置签名!");
            } else {
                usersInfo.pSignText.setText(usersInfo.signature);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static String uploadPic(String str, String str2, String str3) {
        String str4 = "\r\n";
        String str5 = "--";
        String str6 = "*****";
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + str6);
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.writeBytes(str5 + str6 + str4);
            dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"file1\";filename=\"" + str3 + "\"" + str4);
            dataOutputStream.writeBytes(str4);
            FileInputStream fileInputStream = new FileInputStream(str2);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = fileInputStream.read(bArr);
                if (read == -1) {
                    break;
                }
                dataOutputStream.write(bArr, 0, read);
            }
            dataOutputStream.writeBytes(str4);
            dataOutputStream.writeBytes(str5 + str6 + str5 + str4);
            fileInputStream.close();
            dataOutputStream.flush();
            InputStream inputStream = httpURLConnection.getInputStream();
            StringBuilder stringBuffer = new StringBuilder();
            while (true) {
                int read2 = inputStream.read();
                if (read2 == -1) {
                    dataOutputStream.close();
                    return "0";
                }
                stringBuffer.append((char) read2);
            }
        } catch (Exception e) {
            return "1";
        }
    }

    private void cropPicture(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 240);
        intent.putExtra("outputY", 240);
        intent.putExtra("return-data", true);
        cropPictureLauncher.launch(intent);
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        startActivity(new Intent(this, OLMainMode.class));
        finish();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.user_face) {
            JPDialogBuilder jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("提示");
            jpDialogBuilder.setMessage("当前版本暂不支持上传头像");
            jpDialogBuilder.setFirstButton("确定", (dialog, which) -> dialog.dismiss());
            jpDialogBuilder.buildAndShowDialog();
        } else if (id == R.id.password_button) {
            JPDialogBuilder jpDialogBuilder;
            View inflate = getLayoutInflater().inflate(R.layout.password_change, findViewById(R.id.dialog));
            TextView originalPasswordTextView = inflate.findViewById(R.id.original_password);
            TextView newPasswordTextView = inflate.findViewById(R.id.new_password);
            TextView confirmPasswordTextView = inflate.findViewById(R.id.confirm_password);
            CheckBox autoLoginCheckBox = inflate.findViewById(R.id.auto_login);
            autoLoginCheckBox.setChecked(OLBaseActivity.accountListSharedPreferences.getBoolean("chec_autologin", false));
            CheckBox remNewPasswordCheckBox = inflate.findViewById(R.id.re_password);
            remNewPasswordCheckBox.setChecked(OLBaseActivity.accountListSharedPreferences.getBoolean("chec_psw", false));
            jpDialogBuilder = new JPDialogBuilder(this);
            jpDialogBuilder.setTitle("修改密码").loadInflate(inflate)
                    .setFirstButton("确定", new ChangePasswordClick(this, originalPasswordTextView,
                            newPasswordTextView, confirmPasswordTextView, autoLoginCheckBox, remNewPasswordCheckBox))
                    .setSecondButton("取消", (dialog, which) -> dialog.dismiss())
                    .buildAndShowDialog();
        } else if (id == R.id.modify_button) {
            String charSequence = ageText.getText().toString();
            signature = pSignText.getText().toString();
            if (charSequence.startsWith("0")) {
                Toast.makeText(this, "请输入正确的生年格式:1900-2020", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                age = Integer.parseInt(charSequence);
            } catch (Exception e) {
                age = 0;
            }
            if (age < 1900 || age > 2020) {
                Toast.makeText(this, "请输入正确的生年格式:1900-2020", Toast.LENGTH_LONG).show();
            } else if (signature.length() > Consts.MAX_MESSAGE_COUNT) {
                Toast.makeText(this, "确定字数在五百字之内!", Toast.LENGTH_SHORT).show();
            } else {
                new UserInfoChangeTask(this).execute("M", null, null);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ol_user_info);
        accountText = findViewById(R.id.user_name);
        findViewById(R.id.modify_button).setOnClickListener(this);
        findViewById(R.id.password_button).setOnClickListener(this);
        nameText = findViewById(R.id.user_kitiname);
        sexText = findViewById(R.id.user_sex);
        winnerNumText = findViewById(R.id.user_num);
        scoreText = findViewById(R.id.user_score);
        pSignText = findViewById(R.id.users_info_msg);
        faceImage = findViewById(R.id.user_face);
        faceImage.setOnClickListener(this);
        ageText = findViewById(R.id.user_age);
        jpprogressBar = new JPProgressBar(this);
        pictureHandle = new PictureHandle(new Handler(this), 1);
        new UsersInfoGetTask(this).execute();
    }
}
