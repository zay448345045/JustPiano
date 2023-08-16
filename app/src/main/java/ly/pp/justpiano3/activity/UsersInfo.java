package ly.pp.justpiano3.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import ly.pp.justpiano3.*;
import ly.pp.justpiano3.listener.ChangePasswordClick;
import ly.pp.justpiano3.listener.DialogDismissClick;
import ly.pp.justpiano3.task.UsersInfoTask;
import ly.pp.justpiano3.task.UsersInfoTask2;
import ly.pp.justpiano3.task.UsersInfoTask3;
import ly.pp.justpiano3.view.JPDialog;
import ly.pp.justpiano3.view.JPProgressBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class UsersInfo extends BaseActivity implements Callback, OnClickListener {
    public JPApplication jpapplication;
    public boolean autoLogin = false;
    public boolean rememberNewPassword = false;
    public JPProgressBar jpprogressBar;
    public String pSign;
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
    private String accountJpg = "";

    public static void m3930a(UsersInfo usersInfo, String str) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            usersInfo.accountText.setText(usersInfo.jpapplication.getAccountName());
            usersInfo.accountJpg = usersInfo.jpapplication.getAccountName() + ".jpg";
            usersInfo.name = jsonObject.getString("uk");
            usersInfo.nameText.setText(usersInfo.name);
            usersInfo.face = jsonObject.getString("fi");
            usersInfo.faceImage.setTag(usersInfo.face);
            usersInfo.pictureHandle.mo3027a(usersInfo.jpapplication, usersInfo.faceImage, null);
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
            usersInfo.pSign = jsonObject.get("ms").toString();
            if (usersInfo.pSign.isEmpty()) {
                usersInfo.pSignText.setText("未设置签名!");
            } else {
                usersInfo.pSignText.setText(usersInfo.pSign);
            }
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
    }

    public static String m3931b(String str, String str2, String str3) {
        String str4 = "\r\n";
        String str5 = "--";
        String str6 = "*****";
        String str7 = "0";
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
                    return str7;
                }
                stringBuffer.append((char) read2);
            }
        } catch (Exception e) {
            return "1";
        }
    }

    public Uri getUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/JustPiano", accountJpg));
    }

    private void m3929a(Uri uri) {
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
        startActivityForResult(intent, 2);
    }

    @Override
    public boolean handleMessage(Message message) {
        return false;
    }

    @Override
    protected void onActivityResult(int i, int i2, Intent intent) {
        Exception e;
        Throwable th;
        if (i2 == -1) {
            switch (i) {
                case 0:
                    m3929a(intent.getData());
                    break;
                case 1:
                    if (!Environment.getExternalStorageState().equals("mounted")) {
                        Toast.makeText(this, "未找到存储卡，无法存储照片!", Toast.LENGTH_LONG).show();
                    } else {
                        m3929a(getUri());
                    }
                    break;
                case 2:
                    if (intent != null) {
                        Bundle extras = intent.getExtras();
                        if (extras != null) {
                            Bitmap bitmap = extras.getParcelable("data");
                            faceImage.setImageDrawable(new BitmapDrawable(bitmap));
                            FileOutputStream fileOutputStream;
                            try {
                                fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/JustPiano/" + accountJpg);
                                try {
                                    bitmap.compress(CompressFormat.JPEG, 80, fileOutputStream);
                                    new UsersInfoTask3(this).execute("http://" + jpapplication.getServer() + ":8910/JustPianoServer/server/UpLoadFace", Environment.getExternalStorageDirectory() + "/JustPiano/" + accountJpg, accountJpg);
                                    try {
                                        fileOutputStream.close();
                                    } catch (IOException e2) {
                                        e2.printStackTrace();
                                    }
                                } catch (Exception e3) {
                                    e = e3;
                                    try {
                                        e.printStackTrace();
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException e22) {
                                            e22.printStackTrace();
                                        }
                                        super.onActivityResult(i, i2, intent);
                                    } catch (Throwable th2) {
                                        th = th2;
                                        try {
                                            fileOutputStream.close();
                                        } catch (IOException e4) {
                                            e4.printStackTrace();
                                        }
                                        throw th;
                                    }
                                }
                            } catch (Exception e5) {
                                e = e5;
                                e.printStackTrace();
                                super.onActivityResult(i, i2, intent);
                            } catch (Throwable ignored) {
                            }
                        }
                    }
            }
            super.onActivityResult(i, i2, intent);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (jpprogressBar.isShowing()) {
            jpprogressBar.dismiss();
        }
        Intent intent = new Intent();
        intent.setClass(this, OLMainMode.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
//            case R.id.user_face:
//                JPDialog jpdialog = new JPDialog(this);
//                jpdialog.setTitle("抱歉");
//                jpdialog.setMessage("用户自制版服务器空间有限，暂不支持上传头像");
//                jpdialog.setFirstButton("确定", new DialogDismissClick());
//                jpdialog.setSecondButton("打开图库", new C1330na(this));
//                try {f
//                    jpdialog.showDialog();
//                    return;
//                } catch (Exception e) {
//                    return;
//                }
            case R.id.password_button:
                View inflate = getLayoutInflater().inflate(R.layout.password_change, findViewById(R.id.dialog));
                TextView originalPasswordTextView = inflate.findViewById(R.id.original_password);
                TextView newPasswordTextView = inflate.findViewById(R.id.new_password);
                TextView confirmPasswordTextView = inflate.findViewById(R.id.confirm_password);
                CheckBox autoLoginCheckBox = inflate.findViewById(R.id.auto_login);
                autoLoginCheckBox.setChecked(JPApplication.accountListSharedPreferences.getBoolean("chec_autologin", false));
                CheckBox remNewPasswordCheckBox = inflate.findViewById(R.id.re_password);
                remNewPasswordCheckBox.setChecked(JPApplication.accountListSharedPreferences.getBoolean("chec_psw", false));
                JPDialog jpdialog = new JPDialog(this);
                jpdialog
                        .setTitle("修改密码").loadInflate(inflate)
                        .setFirstButton("确定", new ChangePasswordClick(this, originalPasswordTextView,
                                newPasswordTextView, confirmPasswordTextView, autoLoginCheckBox, remNewPasswordCheckBox))
                        .setSecondButton("取消", new DialogDismissClick())
                        .showDialog();
                return;
            case R.id.modify_button:
                String charSequence = ageText.getText().toString();
                pSign = pSignText.getText().toString();
                if (charSequence.startsWith("0")) {
                    Toast.makeText(this, "请输入正确的生年格式:1900-2020", Toast.LENGTH_LONG).show();
                    return;
                }
                age = Integer.parseInt(charSequence);
                if (age < 1900 || age > 2020) {
                    Toast.makeText(this, "请输入正确的生年格式:1900-2020", Toast.LENGTH_LONG).show();
                    return;
                } else if (pSign.length() > 500) {
                    Toast.makeText(this, "确定字数在五百字之内!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    new UsersInfoTask2(this).execute("M", null, null);
                    return;
                }
            default:
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        jpapplication = (JPApplication) getApplication();
        setContentView(R.layout.user_info);
        accountText = findViewById(R.id.user_name);
        Button modifyButton = findViewById(R.id.modify_button);
        modifyButton.setOnClickListener(this);
        Button passwordButton = findViewById(R.id.password_button);
        passwordButton.setOnClickListener(this);
        nameText = findViewById(R.id.user_kitiname);
        sexText = findViewById(R.id.user_sex);
        winnerNumText = findViewById(R.id.user_num);
        scoreText = findViewById(R.id.user_score);
        pSignText = findViewById(R.id.users_info_msg);
        faceImage = findViewById(R.id.user_face);
        faceImage.setOnClickListener(this);
        ageText = findViewById(R.id.user_age);
        jpprogressBar = new JPProgressBar(this);
        Handler pictureHandler = new Handler(this);
        pictureHandle = new PictureHandle(pictureHandler, 1);
        new UsersInfoTask(this).execute();
    }
}
