package ly.pp.justpiano3;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UsersInfo extends Activity implements Callback, OnClickListener {
    public JPApplication jpapplication;
    boolean f5058b = false;
    boolean f5059c = false;
    JPProgressBar jpprogressBar;
    String f5066j;
    int f5067k;
    private PictureHandle f5055A;
    private String f5061e = "";
    private String f5062f = "";
    private String f5063g = "";
    private String f5064h;
    private String f5065i;
    private TextView f5068l;
    private TextView f5069m;
    private TextView f5070n;
    private TextView f5071o;
    private TextView f5072p;
    private TextView f5073q;
    private TextView f5074r;
    private ImageView f5075s;
    private String f5082z = "";

    static void m3930a(UsersInfo usersInfo, String str) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            usersInfo.f5068l.setText(usersInfo.jpapplication.getAccountName());
            usersInfo.f5082z = usersInfo.jpapplication.getAccountName() + ".jpg";
            usersInfo.f5061e = jsonObject.getString("uk");
            usersInfo.f5069m.setText(usersInfo.f5061e);
            usersInfo.f5062f = jsonObject.getString("fi");
            usersInfo.f5075s.setTag(usersInfo.f5062f);
            usersInfo.f5055A.mo3027a(usersInfo.jpapplication, usersInfo.f5075s, null);
            usersInfo.f5063g = jsonObject.getString("sx");
            if (usersInfo.f5063g.equals("m")) {
                usersInfo.f5070n.setText("男");
            } else {
                usersInfo.f5070n.setText("女");
            }
            usersInfo.f5067k = jsonObject.getInt("age");
            if (usersInfo.f5067k < 1900) {
                usersInfo.f5067k = 1990;
            }
            usersInfo.f5071o.setText(String.valueOf(usersInfo.f5067k));
            usersInfo.f5064h = jsonObject.get("nu").toString();
            usersInfo.f5072p.setText(usersInfo.f5064h);
            usersInfo.f5065i = jsonObject.get("sc").toString();
            usersInfo.f5073q.setText(usersInfo.f5065i);
            usersInfo.f5066j = jsonObject.get("ms").toString();
            if (usersInfo.f5066j.isEmpty()) {
                usersInfo.f5074r.setText("未设置签名!");
            } else {
                usersInfo.f5074r.setText(usersInfo.f5066j);
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

    Uri getUri() {
        return Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/JustPiano", f5082z));
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
                            f5075s.setImageDrawable(new BitmapDrawable(bitmap));
                            FileOutputStream fileOutputStream;
                            try {
                                fileOutputStream = new FileOutputStream(Environment.getExternalStorageDirectory() + "/JustPiano/" + f5082z);
                                try {
                                    bitmap.compress(CompressFormat.JPEG, 80, fileOutputStream);
                                    new UsersInfoTask3(this).execute("http://" + jpapplication.getServer() + ":8910/JustPianoServer/server/UpLoadFace", Environment.getExternalStorageDirectory() + "/JustPiano/" + f5082z, f5082z);
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
            case R.id.user_face:
                JPDialog jpdialog = new JPDialog(this);
                jpdialog.setTitle("抱歉");
                jpdialog.setMessage("用户自制版服务器空间有限，暂不支持上传头像");
                jpdialog.setFirstButton("确定", new DialogDismissClick());
                //jpdialog.setSecondButton("打开图库", new C1330na(this));
                try {
                    jpdialog.showDialog();
                    return;
                } catch (Exception e) {
                    return;
                }
            case R.id.password_button:
                View inflate = getLayoutInflater().inflate(R.layout.password_change, findViewById(R.id.dialog));
                TextView textView = inflate.findViewById(R.id.original_password);
                TextView textView2 = inflate.findViewById(R.id.new_password);
                TextView textView3 = inflate.findViewById(R.id.confirm_password);
                CheckBox checkBox = inflate.findViewById(R.id.auto_login);
                checkBox.setChecked(JPApplication.sharedpreferences.getBoolean("chec_autologin", false));
                CheckBox checkBox2 = inflate.findViewById(R.id.re_password);
                checkBox2.setChecked(JPApplication.sharedpreferences.getBoolean("chec_psw", false));
                new JPDialog(this).setTitle("修改密码").loadInflate(inflate).setFirstButton("确定", new ChangePasswordClick(this, textView, textView2, textView3, checkBox, checkBox2)).setSecondButton("取消", new DialogDismissClick()).showDialog();
                return;
            case R.id.modify_button:
                String charSequence = f5071o.getText().toString();
                f5066j = f5074r.getText().toString();
                if (charSequence.startsWith("0")) {
                    Toast.makeText(this, "请输入正确的生年格式:1900-2020", Toast.LENGTH_LONG).show();
                    return;
                }
                f5067k = Integer.parseInt(charSequence);
                if (f5067k < 1900 || f5067k > 2020) {
                    Toast.makeText(this, "请输入正确的生年格式:1900-2020", Toast.LENGTH_LONG).show();
                    return;
                } else if (f5066j.length() >= 200) {
                    Toast.makeText(this, "确定字数在二百字之内!", Toast.LENGTH_SHORT).show();
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
        f5068l = findViewById(R.id.user_name);
        Button f5076t = findViewById(R.id.modify_button);
        f5076t.setOnClickListener(this);
        Button f5077u = findViewById(R.id.password_button);
        f5077u.setOnClickListener(this);
        f5069m = findViewById(R.id.user_kitiname);
        f5070n = findViewById(R.id.user_sex);
        f5072p = findViewById(R.id.user_num);
        f5073q = findViewById(R.id.user_score);
        f5074r = findViewById(R.id.users_info_msg);
        f5075s = findViewById(R.id.user_face);
        f5071o = findViewById(R.id.user_age);
        f5075s.setOnClickListener(this);
        jpprogressBar = new JPProgressBar(this);
        Handler f5056B = new Handler(this);
        f5055A = new PictureHandle(f5056B, 1);
        new UsersInfoTask(this).execute();
    }
}
