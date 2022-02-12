package ly.pp.justpiano3;

import android.os.Bundle;
import android.os.Message;

import org.json.JSONException;
import org.json.JSONObject;

final class Receive {

    static void receive(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            int i = jSONObject.getInt("H");
            String string = jSONObject.getString("C");
            OLPlayHall olPlayHall;
            Message message;
            Bundle bundle;
            switch (i) {
                case 2:
                case 3:
                case 4:
                case 5:
                case 9:
                case 13:
                case 14:
                case 15:
                case 17:
                case 24:
                case 27:
                case 31:
                case 32:
                case 37:
                case 45:
                    JsonHandle.m3949a(i, string);
                    return;
                case 6:
                    JsonHandle.m3951a(string, "H");
                    return;
                case 7:
                    JsonHandle.m3951a(string, "G");
                    return;
                case 10:
                    JPStack.create();
                    if (JPStack.top() instanceof OLMainMode) {
                        JPStack.create();
                        OLMainMode oLMainMode = (OLMainMode) JPStack.top();
                        Message message2 = new Message();
                        switch (string) {
                            case "N":
                                message2.what = 4;
                                break;
                            case "E":
                                message2.what = 5;
                                break;
                            case "V":
                                message2.what = 6;
                                break;
                            default:
                                message2.what = 1;
                                break;
                        }
                        assert oLMainMode != null;
                        oLMainMode.jpapplication.setServerTimeInterval(jSONObject.getLong("T"));
                        oLMainMode.olMainModeHandler.handleMessage(message2);
                        return;
                    }
                    return;
                case 12:
                    JPStack.create();
                    if (JPStack.top() instanceof OLPlayHall) {
                        JPStack.create();
                        olPlayHall = (OLPlayHall) JPStack.top();
                        try {
                            JSONObject jSONObject2 = new JSONObject(string);
                            message = new Message();
                            message.what = 1;
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("M", jSONObject2.getString("M"));
                            bundle2.putString("U", jSONObject2.getString("U"));
                            bundle2.putInt("T", jSONObject2.getInt("T"));
                            if (jSONObject2.getInt("T") == 1) {
                                bundle2.putInt("V", 0);
                            }
                            message.setData(bundle2);
                            assert olPlayHall != null;
                            olPlayHall.olPlayHallHandler.handleMessage(message);
                            return;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                    return;
                case 16:
                    JsonHandle.challenge(string);
                    return;
                case 18:
                    JsonHandle.family(string);
                    return;
                case 19:
                    JsonHandle.m3950a(string);
                    return;
                case 20:
                    JsonHandle.m3956d(string);
                    return;
                case 21:
                    JsonHandle.m3957e(string);
                    return;
                case 22:
                    JsonHandle.m3955c(string);
                    return;
                case 25:
                    JsonHandle.m3948a(string);
                    return;
                case 26:
                    JsonHandle.shop(string);
                    return;
                case 28:
                case 33:
                case 34:
                case 36:
                    JsonHandle.m3953b((byte) i, string);
                    return;
                case 29:
                    try {
                        jSONObject = new JSONObject(string);
                        bundle = new Bundle();
                        if (jSONObject.getInt("T") == 0) {
                            bundle.putInt("T", jSONObject.getInt("T"));
                            bundle.putByte("hallID", (byte) jSONObject.getInt("I"));
                            bundle.putString("hallName", jSONObject.getString("N"));
                        } else {
                            bundle.putInt("T", (byte) jSONObject.getInt("T"));
                            bundle.putString("I", jSONObject.getString("I"));
                            bundle.putString("N", jSONObject.getString("N"));
                        }
                        message = new Message();
                        message.what = 1;
                        message.setData(bundle);
                        JPStack.create();
                        if (JPStack.top() instanceof OLPlayHallRoom) {
                            JPStack.create();
                            ((OLPlayHallRoom) JPStack.top()).olPlayHallRoomHandler.handleMessage(message);
                            return;
                        }
                        return;
                    } catch (JSONException e2) {
                        e2.printStackTrace();
                        return;
                    }
                case 38:
                    JsonHandle.daily(string);
                    return;
                case 39:
                    JsonHandle.keyboardNotes(string);
                    return;
                case 40:
                    bundle = new Bundle();
                    message = new Message();
                    try {
                        JSONObject jSONObject3 = new JSONObject(string);
                        PianoPlay pianoPlay;
                        switch (jSONObject3.getInt("T")) {
                            case 0:
                                JPStack.create();
                                if (JPStack.top() instanceof OLPlayHall) {
                                    JPStack.create();
                                    olPlayHall = (OLPlayHall) JPStack.top();
                                    assert olPlayHall != null;
                                    olPlayHall.jpprogressBar.dismiss();
                                    bundle.putInt("result", jSONObject3.getInt("R"));
                                    bundle.putString("info", jSONObject3.getString("I"));
                                    message.setData(bundle);
                                    message.what = 11;
                                    olPlayHall.olPlayHallHandler.handleMessage(message);
                                    return;
                                }
                                return;
                            case 1:
                                JPStack.create();
                                if (JPStack.top() instanceof OLPlayHall) {
                                    JPStack.create();
                                    olPlayHall = (OLPlayHall) JPStack.top();
                                    assert olPlayHall != null;
                                    olPlayHall.jpprogressBar.dismiss();
                                    bundle.putInt("songsID", jSONObject3.getInt("CL"));
                                    bundle.putString("songBytes", GZIP.ZIPTo(jSONObject3.getString("P")));
                                    bundle.putInt("hand", jSONObject3.has("G") ? jSONObject3.getInt("G") : 0);
                                    message.setData(bundle);
                                    message.what = 13;
                                    olPlayHall.olPlayHallHandler.handleMessage(message);
                                    return;
                                }
                                return;
                            case 2:
                                JPStack.create();
                                if (JPStack.top() instanceof PianoPlay) {
                                    JPStack.create();
                                    pianoPlay = (PianoPlay) JPStack.top();
                                    message.setData(bundle);
                                    message.what = 5;
                                    assert pianoPlay != null;
                                    pianoPlay.pianoPlayHandler.handleMessage(message);
                                    return;
                                }
                                return;
                            case 3:
                                JPStack.create();
                                if (JPStack.top() instanceof PianoPlay) {
                                    JPStack.create();
                                    pianoPlay = (PianoPlay) JPStack.top();
                                    bundle.putInt("R", jSONObject3.getInt("R"));
                                    bundle.putInt("G", jSONObject3.getInt("G"));
                                    bundle.putString("S", jSONObject3.getString("S"));
                                    bundle.putString("E", jSONObject3.getString("E"));
                                    message.setData(bundle);
                                    message.what = 6;
                                    assert pianoPlay != null;
                                    pianoPlay.pianoPlayHandler.handleMessage(message);
                                    return;
                                }
                                return;
                        }
                    } catch (JSONException e22) {
                        e22.printStackTrace();
                    }
                    return;
                case 43:
                    JsonHandle.m3954b(string);
                    return;
                default:
            }
        } catch (JSONException e222) {
            e222.printStackTrace();
        }
    }
}
