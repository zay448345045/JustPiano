package ly.pp.justpiano3.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import ly.pp.justpiano3.activity.*;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.Room;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.task.ReceiveTask;
import org.json.JSONException;
import org.json.JSONObject;
import protobuf.vo.*;

import java.util.*;

public final class Receive {

    private static final Map<Integer, ReceiveTask> receiveTaskMap = new HashMap<>();

    static {
        receiveTaskMap.put(OnlineProtocolType.USER_INFO_DIALOG, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            OnlineUserInfoDialogVO userInfoDialog = receivedMessage.getUserInfoDialog();
            message.what = 23;
            bundle.putString("U", userInfoDialog.getName());
            bundle.putString("F", userInfoDialog.getFamily());
            bundle.putInt("LV", userInfoDialog.getLv());
            bundle.putInt("E", userInfoDialog.getExp());
            bundle.putInt("CL", userInfoDialog.getCl());
            bundle.putInt("W", userInfoDialog.getWinnerNum());
            bundle.putInt("SC", userInfoDialog.getScore());
            bundle.putString("S", userInfoDialog.getGender());
            bundle.putString("P", userInfoDialog.getSignature());
            bundle.putInt("DR_H", userInfoDialog.getClothes().getHair());
            bundle.putInt("DR_E", userInfoDialog.getClothes().getEye());
            bundle.putInt("DR_J", userInfoDialog.getClothes().getJacket());
            bundle.putInt("DR_T", userInfoDialog.getClothes().getTrousers());
            bundle.putInt("DR_S", userInfoDialog.getClothes().getShoes());
            message.setData(bundle);
            if (topActivity instanceof OLPlayRoom) {
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLFamily) {
                ((OLFamily) topActivity).familyHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayHall) {
                ((OLPlayHall) topActivity).olPlayHallHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayHallRoom) {
                ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.PLAY_START, (receivedMessage, topActivity, message) -> {
            message.what = 5;
            Bundle bundle2 = new Bundle();
            bundle2.putString("S", receivedMessage.getPlayStart().getSongPath());
            bundle2.putInt("D", receivedMessage.getPlayStart().getTune());
            message.setData(bundle2);
            if (topActivity instanceof OLPlayRoom) {
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.PLAY_FINISH, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 3;
            OnlinePlayFinishVO playFinish = receivedMessage.getPlayFinish();
            for (int j = 0; j < playFinish.getPlayGradeList().size(); j++) {
                OnlinePlayGradeVO playGrade = playFinish.getPlayGradeList().get(j);
                Bundle bundle3 = new Bundle();
                bundle3.putString("I", playGrade.getIsPlaying() ? "P" : "");
                bundle3.putString("N", playGrade.getName());
                bundle3.putString("SC", String.valueOf(playGrade.getScore()));
                bundle3.putString("P", String.valueOf(playGrade.getGrade().getPerfect()));
                bundle3.putString("C", String.valueOf(playGrade.getGrade().getCool()));
                bundle3.putString("G", String.valueOf(playGrade.getGrade().getGreat()));
                bundle3.putString("B", String.valueOf(playGrade.getGrade().getBad()));
                bundle3.putString("M", String.valueOf(playGrade.getGrade().getMiss()));
                bundle3.putString("T", String.valueOf(playGrade.getGrade().getCombo()));
                bundle3.putString("E", String.valueOf(playGrade.getGrade().getExp()));
                bundle3.putString("GR", String.valueOf(playGrade.getGrade().getGradeColor()));
                bundle.putBundle(String.valueOf(j), bundle3);
            }
            message.setData(bundle);
            if (topActivity instanceof PianoPlay) {
                ((PianoPlay) topActivity).pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CREATE_ROOM, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall) {
                OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                Bundle bundle = new Bundle();
                if (receivedMessage.getCreateRoom().getIsSuccess()) {
                    message.what = 2;
                    bundle.putString("R", receivedMessage.getCreateRoom().getRoomName());
                    bundle.putByte("ID", (byte) receivedMessage.getCreateRoom().getRoomId());
                    bundle.putString("isHost", "H");
                    bundle.putInt("mode", receivedMessage.getCreateRoom().getRoomMode());
                } else {
                    bundle.putString("result", "房间数已满!");
                    message.what = 4;
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.ENTER_ROOM, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall) {
                OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                Bundle bundle = new Bundle();
                switch (receivedMessage.getEnterRoom().getStatus()) {
                    case 0:
                        bundle.putString("result", "该房间正在弹奏中!");
                        message.what = 4;
                        break;
                    case 1:
                        bundle.putString("result", "该房间人数已满!");
                        message.what = 4;
                        break;
                    case 2:
                        bundle.putString("result", "房间数已满!");
                        message.what = 4;
                        break;
                    case 3:
                        bundle.putString("result", "密码有误!");
                        message.what = 4;
                        break;
                    case 4:
                        message.what = 2;
                        bundle.putString("R", receivedMessage.getEnterRoom().getRoomName());
                        bundle.putByte("ID", (byte) receivedMessage.getEnterRoom().getRoomId());
                        bundle.putString("isHost", "G");
                        bundle.putInt("mode", receivedMessage.getEnterRoom().getRoomMode());
                        break;
                    default:
                        break;
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.KICKED_QUIT_ROOM, (receivedMessage, topActivity, message) -> {
            message.what = 8;
            if (topActivity instanceof OLPlayRoom) {
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOGIN, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLMainMode) {
                OLMainMode oLMainMode = (OLMainMode) topActivity;
                Message message2 = Message.obtain(oLMainMode.olMainModeHandler);
                switch (receivedMessage.getLogin().getStatus()) {
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
                EncryptUtil.setServerTimeInterval(receivedMessage.getLogin().getTime());
                oLMainMode.olMainModeHandler.handleMessage(message2);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.HALL_CHAT, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall) {
                Bundle bundle = new Bundle();
                OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                OnlineHallChatVO hallChat = receivedMessage.getHallChat();
                message.what = 1;
                bundle.putString("M", hallChat.getMessage());
                bundle.putString("U", hallChat.getUserName());
                bundle.putInt("T", hallChat.getType());
                if (hallChat.getType() == 1) {
                    bundle.putInt("V", 0);
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.ROOM_CHAT, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayRoomActivity) {
                Bundle bundle = new Bundle();
                OnlineRoomChatVO roomChat = receivedMessage.getRoomChat();
                message.what = 2;
                bundle.putString("U", roomChat.getUserName());
                bundle.putString("M", roomChat.getMessage());
                bundle.putInt("T", roomChat.getType());
                if (roomChat.getType() == 1) {
                    bundle.putInt("V", roomChat.getColor());
                }
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom) {
                    ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_ROOM_INFO, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 10;
            bundle.putString("R", receivedMessage.getChangeRoomInfo().getRoomName());
            message.setData(bundle);
            if (topActivity instanceof OLPlayRoom) {
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.PLAY_SONG, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 3;
            bundle.putString("song_path", receivedMessage.getPlaySong().getSongPath().replace("\\/", "/"));
            bundle.putInt("diao", receivedMessage.getPlaySong().getTune());
            message.setData(bundle);
            if (topActivity instanceof OLPlayRoom) {
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHALLENGE, (receivedMessage, topActivity, message) -> {
            OnlineChallengeVO challenge = receivedMessage.getChallenge();
            switch (challenge.getType()) {
                case 1:
                    if (topActivity instanceof OLChallenge) {
                        OLChallenge olChallenge = (OLChallenge) topActivity;
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < challenge.getChallengeEnter().getUserScoreList().size(); i++) {
                            OnlineChallengeUserVO challengeUser = challenge.getChallengeEnter().getUserScoreList().get(i);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("N", challengeUser.getName());
                            bundle2.putString("S", String.valueOf(challengeUser.getScore()));
                            bundle2.putString("T", challengeUser.getTime());
                            bundle.putBundle(String.valueOf(i), bundle2);
                        }
                        bundle.putInt("S", challenge.getChallengeEnter().getScore());
                        bundle.putString("P", challenge.getChallengeEnter().getPosition());
                        bundle.putString("T", String.valueOf(challenge.getChallengeEnter().getChallengeNum()));
                        bundle.putString("Z", challenge.getChallengeEnter().getYesterdayPosition());
                        message.setData(bundle);
                        olChallenge.challengeHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 2:
                    if (topActivity instanceof OLChallenge) {
                        OLChallenge olChallenge = (OLChallenge) topActivity;
                        olChallenge.jpprogressBar.dismiss();
                        message.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putInt("R", challenge.getChallengeDialog().getAllowed() ? 1 : 0);
                        bundle.putString("I", challenge.getChallengeDialog().getMessage());
                        bundle.putString("P", GZIPUtil.ZIPTo(challenge.getChallengeDialog().getSongContent()));
                        message.setData(bundle);
                        olChallenge.challengeHandler.handleMessage(message);
                    }
                    break;
                case 3:
                    if (topActivity instanceof PianoPlay) {
                        PianoPlay pianoPlay = (PianoPlay) topActivity;
                        message.setData(new Bundle());
                        message.what = 5;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 4:
                    if (topActivity instanceof PianoPlay) {
                        PianoPlay pianoPlay = (PianoPlay) topActivity;
                        Bundle bundle = new Bundle();
                        bundle.putString("I", challenge.getChallengeFinish().getMessage());
                        message.setData(bundle);
                        message.what = 9;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 5:
                    if (topActivity instanceof OLChallenge) {
                        OLChallenge olChallenge = (OLChallenge) topActivity;
                        message.what = 5;
                        Bundle bundle = new Bundle();
                        bundle.putInt("P", challenge.getChallengePrize().getPrizeType());
                        bundle.putString("N", challenge.getChallengePrize().getPrizeName());
                        message.setData(bundle);
                        olChallenge.challengeHandler.handleMessage(message);
                    }
                    break;
                default:
                    break;
            }
        });

        receiveTaskMap.put(OnlineProtocolType.BROADCAST, (receivedMessage, topActivity, message) -> {

        });

        receiveTaskMap.put(OnlineProtocolType.FAMILY, (receivedMessage, topActivity, message) -> {
            OnlineFamilyVO family = receivedMessage.getFamily();
            switch (family.getType()) {
                case 1:
                    if (topActivity instanceof OLFamily) {
                        OLFamily olFamily = (OLFamily) topActivity;
                        message.what = 1;
                        olFamily.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < family.getFamilyEnter().getFamilyUserList().size(); i++) {
                            OnlineFamilyUserVO familyUser = family.getFamilyEnter().getFamilyUserList().get(i);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("N", familyUser.getName());
                            bundle2.putString("C", String.valueOf(familyUser.getContribution()));
                            bundle2.putString("L", String.valueOf(familyUser.getLv()));
                            bundle2.putString("S", familyUser.getGender());
                            bundle2.putString("O", String.valueOf(familyUser.getOnline() ? 1 : 0));
                            bundle2.putString("P", String.valueOf(familyUser.getPosition()));
                            bundle2.putString("D", familyUser.getLoginDate());
                            bundle.putBundle(String.valueOf(i), bundle2);
                        }
                        bundle.putString("D", family.getFamilyEnter().getDeclaration());
                        String dateTime = DateUtil.format(new Date(family.getFamilyEnter().getCreateDate()));
                        bundle.putString("T", dateTime);
                        bundle.putString("Z", family.getFamilyEnter().getLeader());
                        bundle.putString("N", family.getFamilyEnter().getName());
                        bundle.putString("C", String.valueOf(family.getFamilyEnter().getContribution()));
                        bundle.putString("P", family.getFamilyEnter().getPosition());
                        message.setData(bundle);
                        olFamily.familyHandler.handleMessage(message);
                    }
                    break;
                case 2:
                    if (topActivity instanceof OLPlayHallRoom) {
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) topActivity;
                        message.what = 2;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < family.getFamilyList().getFamilyInfoList().size(); i++) {
                            OnlineFamilyInfoVO familyInfo = family.getFamilyList().getFamilyInfoList().get(i);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("N", familyInfo.getName());
                            bundle2.putString("C", String.valueOf(familyInfo.getContribution()));
                            bundle2.putString("T", String.valueOf(familyInfo.getCapacity()));
                            bundle2.putString("U", String.valueOf(familyInfo.getSize()));
                            bundle2.putString("I", String.valueOf(familyInfo.getFamilyId()));
                            bundle2.putByteArray("J", GZIPUtil.ZIPToArray(familyInfo.getPicture()));
                            bundle.putBundle(String.valueOf(i), bundle2);
                        }
                        bundle.putString("C", String.valueOf(family.getFamilyList().getContribution()));
                        bundle.putString("P", String.valueOf(family.getFamilyList().getPosition()));
                        bundle.putString("N", family.getFamilyList().getName());
                        bundle.putString("T", String.valueOf(family.getFamilyList().getCapacity()));
                        bundle.putString("U", String.valueOf(family.getFamilyList().getSize()));
                        bundle.putString("I", String.valueOf(family.getFamilyList().getFamilyId()));
                        bundle.putByteArray("J", GZIPUtil.ZIPToArray(family.getFamilyList().getPicture()));
                        message.setData(bundle);
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                    break;
                case 3:
                    if (topActivity instanceof OLPlayHallRoom) {
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) topActivity;
                        Bundle bundle = new Bundle();
                        bundle.putString("I", family.getFamilyDialog().getMessage());
                        bundle.putInt("R", family.getFamilyDialog().getAllowed() ? 1 : 0);
                        message.setData(bundle);
                        message.what = 9;
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 4:
                    if (topActivity instanceof OLPlayHallRoom) {
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) topActivity;
                        Bundle bundle = new Bundle();
                        bundle.putInt("R", family.getFamilyCreate().getResult());
                        message.setData(bundle);
                        message.what = 10;
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 5:
                case 6:
                case 7:
                case 10:
                    if (topActivity instanceof OLFamily) {
                        OLFamily olFamily = (OLFamily) topActivity;
                        Bundle bundle = new Bundle();
                        bundle.putString("I", family.getFamilyDialog().getMessage());
                        message.setData(bundle);
                        message.what = 5;
                        olFamily.familyHandler.handleMessage(message);
                        return;
                    }
                    break;
                case 8:
                    if (topActivity instanceof OLFamily) {
                        OLFamily olFamily = (OLFamily) topActivity;
                        Bundle bundle = new Bundle();
                        try {
                            bundle.putString("I", family.getFamilyDialog().getMessage());
                            bundle.putInt("R", family.getFamilyDialog().getAllowed() ? 1 : 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        message.setData(bundle);
                        message.what = 8;
                        olFamily.familyHandler.handleMessage(message);
                        return;
                    }
                    break;
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_ROOM_LIST, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 3;
            OnlineLoadRoomListVO loadRoomList = receivedMessage.getLoadRoomList();
            if (topActivity instanceof OLPlayHall) {
                OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                for (int i = 0; i < loadRoomList.getRoomList().size(); i++) {
                    OnlineRoomVO roomRaw = loadRoomList.getRoomList().get(i);
                    Bundle bundle2 = new Bundle();
                    Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                            roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                            roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
                    olPlayHall.putRoomToMap(room.getRoomID(), room);
                    bundle2.putByte("I", room.getRoomID());
                    bundle2.putString("N", room.getRoomName());
                    bundle2.putIntArray("UA", room.getPeople());
                    bundle2.putBoolean("IF", room.isPeopleFull());
                    bundle2.putInt("IP", room.isPlaying());
                    bundle2.putInt("PA", room.isPassword());
                    bundle2.putInt("V", room.getRoomKuang());
                    bundle2.putInt("D", room.getRoomMode());
                    bundle.putBundle(String.valueOf(i), bundle2);
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_ROOM_POSITION, (receivedMessage, topActivity, message) -> {
            OnlineChangeRoomPositionVO changeRoomPosition = receivedMessage.getChangeRoomPosition();
            message.what = 1;
            if (topActivity instanceof OLPlayRoomActivity) {
                OLPlayRoomActivity olPlayRoomActivity = (OLPlayRoomActivity) topActivity;
                for (OnlineRoomPositionUserVO roomPositionUser : changeRoomPosition.getRoomPositionUserList()) {
                    buildAndPutUser(olPlayRoomActivity, roomPositionUser);
                }
                Bundle bundle = new Bundle();
                Iterator<User> it = olPlayRoomActivity.jpapplication.getRoomPlayerMap().values().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    User user = it.next();
                    fillUserBundle(bundle, i, user);
                }
                bundle.putString("SI", "");
                bundle.putInt("diao", 0);
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom) {
                    OLPlayRoom olPlayRoom = (OLPlayRoom) topActivity;
                    bundle.putString("MSG_C", "");
                    bundle.putInt("MSG_CI", 0);
                    bundle.putInt("MSG_CT", 0);
                    bundle.putBoolean("MSG_I", false);
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom) {
                    OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) topActivity;
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_ROOM_POSITION, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayRoomActivity) {
                OLPlayRoomActivity olPlayRoomActivity = (OLPlayRoomActivity) topActivity;
                OnlineLoadRoomPositionVO loadRoomPosition = receivedMessage.getLoadRoomPosition();
                message.what = 1;
                Bundle bundle = new Bundle();
                for (int i = 0; i < loadRoomPosition.getRoomPositionUserList().size(); i++) {
                    OnlineRoomPositionUserVO roomPositionUser = loadRoomPosition.getRoomPositionUserList().get(i);
                    fillUserBundle(bundle, i, buildAndPutUser(olPlayRoomActivity, roomPositionUser));
                }
                bundle.putString("SI", loadRoomPosition.getSongPath());
                bundle.putInt("diao", loadRoomPosition.getTune());
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom) {
                    OLPlayRoom olPlayRoom = (OLPlayRoom) topActivity;
                    bundle.putString("MSG_C", loadRoomPosition.getRoomPositionCouple().getCoupleName());
                    bundle.putInt("MSG_CI", loadRoomPosition.getRoomPositionCouple().getCouplePosition());
                    bundle.putInt("MSG_CT", loadRoomPosition.getRoomPositionCouple().getCoupleType());
                    bundle.putBoolean("MSG_I", loadRoomPosition.getRoomPositionCouple().getInvite());
                    message.setData(bundle);
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom) {
                    OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) topActivity;
                    message.setData(bundle);
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_ROOM_LIST, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 3;
            if (JPStack.top() instanceof OLPlayHall) {
                OLPlayHall olPlayHall = (OLPlayHall) JPStack.top();
                assert olPlayHall != null;
                OnlineRoomVO roomRaw = receivedMessage.getChangeRoomList();
                Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                        roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                        roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
                if (room.getFcount() + room.getMcount() == 0) {
                    olPlayHall.roomTitleMap.remove(room.getRoomID());
                } else {
                    olPlayHall.putRoomToMap(room.getRoomID(), room);
                }
                Iterator<Room> it = olPlayHall.roomTitleMap.values().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    room = it.next();
                    Bundle bundle2 = new Bundle();
                    bundle2.putByte("I", room.getRoomID());
                    bundle2.putString("N", room.getRoomName());
                    bundle2.putIntArray("UA", room.getPeople());
                    bundle2.putBoolean("IF", room.isPeopleFull());
                    bundle2.putInt("IP", room.isPlaying());
                    bundle2.putInt("PA", room.isPassword());
                    bundle2.putInt("V", room.getRoomKuang());
                    bundle2.putInt("D", room.getRoomMode());
                    bundle.putBundle(String.valueOf(i), bundle2);
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_PLAY_USER, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 1;
            message.arg1 = 0;
            OnlineLoadPlayUserVO loadPlayUser = receivedMessage.getLoadPlayUser();
            List<OnlinePlayUserVO> playUserList = loadPlayUser.getPlayUserList();
            for (int j = 0; j < playUserList.size(); j++) {
                OnlinePlayUserVO playUser = playUserList.get(j);
                Bundle bundle3 = new Bundle();
                bundle3.putString("G", String.valueOf(playUser.getHand()));
                bundle3.putString("U", playUser.getName());
                bundle3.putString("M", String.valueOf(playUser.getMode()));
                bundle.putBundle(String.valueOf(j), bundle3);
            }
            message.setData(bundle);
            if (topActivity instanceof PianoPlay) {
                ((PianoPlay) topActivity).pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.MINI_GRADE, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof PianoPlay) {
                PianoPlay pianoPlay = (PianoPlay) topActivity;
                User user = (User) pianoPlay.userMap.get((byte) receivedMessage.getMiniGrade().getRoomPosition());
                if (user == null) {
                    return;
                }
                Bundle bundle = new Bundle();
                Bundle bundle2;
                User user2;
                int i2 = receivedMessage.getMiniGrade().getScore();
                int i3 = receivedMessage.getMiniGrade().getCombo();
                if (i2 < 0) {
                    user.setOpenPosition();
                    user.setPlayerName("");
                } else {
                    user.setScore(i2);
                    user.setCombo(i3);
                }
                int i = 0;
                for (byte b = 1; b <= 6; b++) {
                    user2 = (User) pianoPlay.userMap.get(b);
                    if (user2 == null) {
                        continue;
                    }
                    if (!user2.getPlayerName().isEmpty()) {
                        bundle2 = new Bundle();
                        bundle2.putString("G", String.valueOf(user2.getHand()));
                        bundle2.putString("U", user2.getPlayerName());
                        bundle2.putString("M", String.valueOf(user2.getScore()));
                        bundle2.putString("T", user2.getCombo() + "");
                        bundle.putBundle(String.valueOf(i), bundle2);
                        i++;
                    }
                }
                message.what = 2;
                message.setData(bundle);
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.SHOP, (receivedMessage, topActivity, message) -> {
            OnlineShopVO shop = receivedMessage.getShop();
            switch (shop.getType()) {
                case 1:  // 加载商品
                    if (topActivity instanceof OLPlayDressRoom) {
                        OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) topActivity;
                        message.what = 3;
                        olPlayDressRoom.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < shop.getShopProductShow().getProductList().size(); i++) {
                            OnlineShopProductVO shopProduct = shop.getShopProductShow().getProductList().get(i);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("N", shopProduct.getName());
                            bundle2.putString("D", shopProduct.getDescription());
                            bundle2.putInt("G", shopProduct.getPrice());
                            bundle2.putString("P", shopProduct.getPicture());
                            bundle2.putInt("I", shopProduct.getId());
                            bundle.putBundle(String.valueOf(i), bundle2);
                        }
                        bundle.putString("G", String.valueOf(shop.getShopProductShow().getGold()));
                        message.setData(bundle);
                        olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
                    }
                    break;
                case 2:  // 购买商品
                    if (topActivity instanceof OLPlayDressRoom) {
                        OLPlayDressRoom olPlayDressRoom = (OLPlayDressRoom) topActivity;
                        Bundle bundle = new Bundle();
                        bundle.putString("I", shop.getShopProductBuy().getMessage());
                        bundle.putString("G", String.valueOf(shop.getShopProductBuy().getGold()));
                        message.setData(bundle);
                        message.what = 4;
                        olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
                        return;
                    }
                    break;
            }
        });

        receiveTaskMap.put(OnlineProtocolType.RECOMMEND_SONG, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 4;
            bundle.putString("U", receivedMessage.getRecommendSong().getName());
            bundle.putString("M", "推荐歌曲:");
            bundle.putString("I", receivedMessage.getRecommendSong().getSongPath());
            bundle.putInt("D", 0);
            bundle.putInt("T", 0);
            message.setData(bundle);
            if (topActivity instanceof OLPlayRoom) {
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_USER, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            Bundle bundle3 = new Bundle();
            message.what = 0;
            OnlineLoadUserVO loadUser = receivedMessage.getLoadUser();
            for (int i = 0; i < loadUser.getHallList().size(); i++) {
                OnlineHallVO hall = loadUser.getHallList().get(i);
                Bundle bundle4 = new Bundle();
                bundle4.putByte("I", (byte) hall.getId());
                bundle4.putString("N", hall.getName());
                bundle4.putInt("PN", hall.getSize());
                bundle4.putInt("TN", hall.getCapacity());
                bundle4.putInt("W", hall.getEncrypt() ? 1 : 0);
                bundle3.putBundle(String.valueOf(i), bundle4);
            }
            bundle.putBundle("L", bundle3);
            bundle.putString("U", loadUser.getName());
            bundle.putString("S", loadUser.getGender());
            bundle.putString("I", String.valueOf(loadUser.getFamily()));
            bundle.putInt("LV", loadUser.getLv());
            bundle.putInt("CL", loadUser.getCl());
            bundle.putInt("E", loadUser.getExp());
            bundle.putInt("M", loadUser.getMessageNum());
            bundle.putInt("DR_H", loadUser.getClothes().getHair());
            bundle.putInt("DR_E", loadUser.getClothes().getEye());
            bundle.putInt("DR_J", loadUser.getClothes().getJacket());
            bundle.putInt("DR_T", loadUser.getClothes().getTrousers());
            bundle.putInt("DR_S", loadUser.getClothes().getShoes());
            bundle.putInt("CP", loadUser.getCoupleType());
            message.setData(bundle);
            if (topActivity instanceof OLPlayHallRoom) {
                ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.ENTER_HALL, (receivedMessage, topActivity, message) -> {
            OnlineEnterHallVO enterHall = receivedMessage.getEnterHall();
            Bundle bundle = new Bundle();
            if (enterHall.getAllowed()) {
                bundle.putInt("T", 0);
                bundle.putByte("hallID", Byte.parseByte(enterHall.getHallIdOrMsg()));
                bundle.putString("hallName", enterHall.getNameOrTitle());
            } else {
                bundle.putInt("T", 1);
                bundle.putString("I", enterHall.getHallIdOrMsg());
                bundle.putString("N", enterHall.getNameOrTitle());
            }
            message.what = 1;
            message.setData(bundle);
            if (topActivity instanceof OLPlayHallRoom) {
                ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.SET_USER_INFO, (receivedMessage, topActivity, message) -> {
            OnlineSetUserInfoVO setUserInfo = receivedMessage.getSetUserInfo();
            int type = setUserInfo.getType();
            if (type == 3 || type == 4) {
                Bundle bundle = new Bundle();
                bundle.putInt("T", 1);
                bundle.putString("I", setUserInfo.getMessage());
                bundle.putString("N", setUserInfo.getCoupleTitle());
                message = Message.obtain();
                message.what = 1;
                message.setData(bundle);
                if (topActivity instanceof OLPlayHallRoom) {
                    ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
                    return;
                }
                return;
            }
            Handler handler = null;
            switch (setUserInfo.getLocation()) {
                case 0:
                    if (topActivity instanceof OLPlayRoom) {
                        handler = ((OLPlayRoom) topActivity).olPlayRoomHandler;
                        message.what = 9;
                        break;
                    } else if (topActivity instanceof OLPlayKeyboardRoom) {
                        handler = ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler;
                        message.what = 9;
                        break;
                    }
                    return;
                case 1:
                    if (topActivity instanceof OLPlayHall) {
                        handler = ((OLPlayHall) topActivity).olPlayHallHandler;
                        message.what = 8;
                        break;
                    } else if (topActivity instanceof OLPlayKeyboardRoom) {
                        handler = ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler;
                        message.what = 8;
                        break;
                    }
                    return;
            }
            if (handler != null) {
                String string = setUserInfo.getName();
                if (!string.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("T", type);
                    if (type == 1) {
                        bundle.putInt("I", setUserInfo.getResult());
                        if (setUserInfo.getResult() == 3) {
                            bundle.putString("title", setUserInfo.getFriendTitle());
                            bundle.putString("Message", setUserInfo.getFriendMsg());
                        }
                    }
                    bundle.putString("F", string);
                    message.setData(bundle);
                    handler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.SET_MINI_GRADE, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 1;
            message.arg1 = 1;
            OnlineSetMiniGradeVO setMiniGrade = receivedMessage.getSetMiniGrade();
            for (int i = 0; i < setMiniGrade.getMiniGradeOnList().size(); i++) {
                OnlineMiniGradeOnVO miniGradeOn = setMiniGrade.getMiniGradeOnList().get(i);
                Bundle innerBundle = new Bundle();
                innerBundle.putString("G", String.valueOf(miniGradeOn.getHand()));
                innerBundle.putString("U", miniGradeOn.getName());
                innerBundle.putString("M", String.valueOf(miniGradeOn.getMode()));
                bundle.putBundle(String.valueOf(i), innerBundle);
            }
            message.setData(bundle);
            if (topActivity instanceof PianoPlay) {
                ((PianoPlay) topActivity).pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_CLOTHES, (receivedMessage, topActivity, message) -> {
            OnlineChangeClothesVO changeClothes = receivedMessage.getChangeClothes();
            int type = changeClothes.getType();
            message.what = type;
            Bundle bundle5 = new Bundle();
            switch (type) {
                case 0:  // 保存服装
                    bundle5.putString("I", changeClothes.getMessage());
                    break;
                case 1:  // 进入换衣间加载音符和解锁情况
                    bundle5.putString("G", String.valueOf(changeClothes.getGold()));
                    bundle5.putByteArray("U", GZIPUtil.ZIPToArray(changeClothes.getUnlock()));
                    break;
                case 2:  // 购买服装
                    bundle5.putString("I", changeClothes.getMessage());
                    bundle5.putString("G", String.valueOf(changeClothes.getGold()));
                    bundle5.putInt("U_T", changeClothes.getBuyClothesType());
                    bundle5.putInt("U_I", changeClothes.getBuyClothesId());
                    break;
                case 3:  // 服务器下发服装价格
                    message.what = 5;
                    int[] priseArr = new int[changeClothes.getBuyClothesPricesCount()];
                    for (int y = 0; y < changeClothes.getBuyClothesPricesCount(); y++) {
                        priseArr[y] = changeClothes.getBuyClothesPricesList().get(y);
                    }
                    bundle5.putInt("C_T", changeClothes.getBuyClothesType());
                    bundle5.putIntArray("P", priseArr);
                    break;
            }
            message.setData(bundle5);
            if (topActivity instanceof OLPlayDressRoom) {
                ((OLPlayDressRoom) topActivity).olPlayDressRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_USER_INFO, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            OnlineLoadUserInfoVO loadUserInfo = receivedMessage.getLoadUserInfo();
            switch (loadUserInfo.getType()) {
                case 1:
                    for (int i = 0; i < loadUserInfo.getLoadUserFriend().getFriendUserList().size(); i++) {
                        OnlineFriendUserVO friendUser = loadUserInfo.getLoadUserFriend().getFriendUserList().get(i);
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("F", friendUser.getName());
                        bundle2.putInt("O", friendUser.getOnline() ? 1 : 0);
                        bundle2.putString("S", friendUser.getGender());
                        bundle2.putInt("LV", friendUser.getLv());
                        bundle.putBundle(String.valueOf(i), bundle2);
                    }
                    switch (loadUserInfo.getLoadUserFriend().getLocation()) {
                        case 0:
                            if (topActivity instanceof OLPlayRoom) {
                                message.what = 11;
                                message.setData(bundle);
                                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
                                return;
                            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                                message.what = 11;
                                message.setData(bundle);
                                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 1:
                            message.what = 5;
                            message.setData(bundle);
                            if (topActivity instanceof OLPlayHall) {
                                ((OLPlayHall) topActivity).olPlayHallHandler.handleMessage(message);
                                return;
                            }
                            return;
                        case 2:
                            message.what = 3;
                            message.setData(bundle);
                            if (topActivity instanceof OLPlayHallRoom) {
                                ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
                                return;
                            }
                            return;
                        default:
                            return;
                    }
                case 2:
                    for (int i = 0; i < loadUserInfo.getLoadUserMail().getMailList().size(); i++) {
                        OnlineMailVO mail = loadUserInfo.getLoadUserMail().getMailList().get(i);
                        Bundle bundle6 = new Bundle();
                        bundle6.putString("F", mail.getUserFrom());
                        bundle6.putString("M", mail.getMessage());
                        String dateTime = DateUtil.format(new Date(mail.getTime()));
                        bundle6.putString("T", dateTime);
                        bundle6.putInt("type", 0);
                        bundle.putBundle(String.valueOf(i), bundle6);
                    }
                    message.what = 4;
                    message.setData(bundle);
                    if (topActivity instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    return;
                case 3:
                    OnlineLoadUserCoupleVO loadUserCouple = loadUserInfo.getLoadUserCouple();
                    bundle.putString("U", loadUserCouple.getName());
                    bundle.putString("S", loadUserCouple.getGender());
                    bundle.putInt("LV", loadUserCouple.getLv());
                    bundle.putInt("CL", loadUserCouple.getCl());
                    bundle.putInt("DR_H", loadUserCouple.getClothes().getHair());
                    bundle.putInt("DR_E", loadUserCouple.getClothes().getEye());
                    bundle.putInt("DR_J", loadUserCouple.getClothes().getJacket());
                    bundle.putInt("DR_T", loadUserCouple.getClothes().getTrousers());
                    bundle.putInt("DR_S", loadUserCouple.getClothes().getShoes());
                    bundle.putString("IN", loadUserCouple.getDeclaration());
                    bundle.putInt("CP", loadUserCouple.getBlessing());
                    message.what = 22;
                    message.setData(bundle);
                    if (topActivity instanceof OLPlayHallRoom) {
                        ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler.handleMessage(message);
                    }
                    return;
                default:
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_USER_LIST, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            OnlineLoadUserListVO loadUserList = receivedMessage.getLoadUserList();
            for (int i = 0; i < loadUserList.getUserList().size(); i++) {
                OnlineUserVO user = loadUserList.getUserList().get(i);
                Bundle bundle2 = new Bundle();
                bundle2.putString("U", user.getName());
                bundle2.putString("S", user.getGender());
                bundle2.putInt("LV", user.getLv());
                bundle2.putInt("R", user.getRoomId());
                bundle.putBundle(String.valueOf(i), bundle2);
            }
            if (loadUserList.getIsInRoom()) {
                message.what = 15;
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom) {
                    ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom) {
                    ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
                }
            } else {
                message.what = 7;
                message.setData(bundle);
                if (topActivity instanceof OLPlayHall) {
                    ((OLPlayHall) topActivity).olPlayHallHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.DIALOG, (receivedMessage, topActivity, message) -> {
            OnlineDialogVO dialog = receivedMessage.getDialog();
            int type = dialog.getType();
            Handler handler = null;
            switch (dialog.getLocation()) {
                case 0:
                    if (topActivity instanceof OLPlayRoom) {
                        handler = ((OLPlayRoom) topActivity).olPlayRoomHandler;
                        message.what = 14;
                        break;
                    } else if (topActivity instanceof OLPlayKeyboardRoom) {
                        handler = ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler;
                        message.what = 14;
                        break;
                    }
                    return;
                case 1:
                    if (topActivity instanceof OLPlayHall) {
                        handler = ((OLPlayHall) topActivity).olPlayHallHandler;
                        message.what = 9;
                        break;
                    }
                    return;
                case 2:
                    if (topActivity instanceof OLPlayHallRoom) {
                        handler = ((OLPlayHallRoom) topActivity).olPlayHallRoomHandler;
                        message.what = 5;
                        break;
                    }
                    return;
            }
            if (handler != null) {
                String string = dialog.getMessage();
                if (!string.isEmpty()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("T", type);
                    bundle.putString("Ti", dialog.getTitle());
                    bundle.putString("P", dialog.getPassword());
                    bundle.putString("I", string);
                    bundle.putInt("C", dialog.getCanEnter() ? 1 : 0);
                    bundle.putInt("H", dialog.getHallId());
                    bundle.putInt("R", dialog.getRoomId());
                    bundle.putString("N", dialog.getName());
                    try {
                        bundle.putString("F", new JSONObject(dialog.getBizData()).getString("handlingFee"));
                    } catch (Exception ignore) {
                    }
                    message.setData(bundle);
                    handler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.DAILY, (receivedMessage, topActivity, message) -> {
            OnlineDailyVO daily = receivedMessage.getDaily();
            switch (daily.getType()) {
                case 0:
                    break;
                case 1:
                    if (topActivity instanceof OLPlayHallRoom) {
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) topActivity;
                        message.what = 11;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < daily.getDailyTimeList().getDailyTimeUserList().size(); i++) {
                            OnlineDailyTimeUserVO dailyTimeUser = daily.getDailyTimeList().getDailyTimeUserList().get(i);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("N", dailyTimeUser.getName());
                            bundle2.putString("T", String.valueOf(dailyTimeUser.getOnlineTime()));
                            bundle2.putString("B", dailyTimeUser.getBonus());
                            bundle2.putString("G", String.valueOf(dailyTimeUser.getBonusGet() ? 1 : 0));
                            bundle.putBundle(String.valueOf(i), bundle2);
                        }
                        bundle.putString("M", daily.getDailyTimeList().getTomorrowBonus());
                        bundle.putString("T", String.valueOf(daily.getDailyTimeList().getTodayOnlineTime()));
                        message.setData(bundle);
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                    break;
                case 2:
                    if (topActivity instanceof OLPlayHallRoom) {
                        OLPlayHallRoom olPlayHallRoom = (OLPlayHallRoom) topActivity;
                        Bundle bundle = new Bundle();
                        bundle.putString("M", daily.getDailyPrizeGet().getMessage());
                        message.setData(bundle);
                        message.what = 12;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                        return;
                    }
                    break;
            }
        });

        receiveTaskMap.put(OnlineProtocolType.KEYBOARD, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayKeyboardRoom) {
                OLPlayKeyboardRoom olPlayKeyboardRoom = (OLPlayKeyboardRoom) topActivity;
                try {
                    byte[] keyboardNotes = receivedMessage.getKeyboardNote().getData().toByteArray();
                    Bundle bundle = new Bundle();
                    bundle.putByteArray("NOTES", keyboardNotes);
                    message.what = 5;
                    message.setData(bundle);
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CL_TEST, (receivedMessage, topActivity, message) -> {
            OnlineClTestVO clTest = receivedMessage.getClTest();
            Bundle bundle = new Bundle();
            message = Message.obtain();
            PianoPlay pianoPlay;
            switch (clTest.getType()) {
                case 0:
                    if (topActivity instanceof OLPlayHall) {
                        OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                        olPlayHall.jpprogressBar.dismiss();
                        bundle.putInt("result", clTest.getClTestDialog().getAllowed() ? 1 : 0);
                        bundle.putString("info", clTest.getClTestDialog().getMessage());
                        message.setData(bundle);
                        message.what = 11;
                        olPlayHall.olPlayHallHandler.handleMessage(message);
                        return;
                    }
                    return;
                case 1:
                    if (topActivity instanceof OLPlayHall) {
                        OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                        olPlayHall.jpprogressBar.dismiss();
                        bundle.putInt("songsID", clTest.getClTestSong().getCl());
                        bundle.putString("songBytes", GZIPUtil.ZIPTo(clTest.getClTestSong().getSongContent()));
                        bundle.putInt("hand", clTest.getClTestSong().getHand());
                        message.setData(bundle);
                        message.what = 13;
                        olPlayHall.olPlayHallHandler.handleMessage(message);
                        return;
                    }
                    return;
                case 2:
                    if (topActivity instanceof PianoPlay) {
                        pianoPlay = (PianoPlay) topActivity;
                        message.setData(bundle);
                        message.what = 5;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                        return;
                    }
                    return;
                case 3:
                    if (topActivity instanceof PianoPlay) {
                        pianoPlay = (PianoPlay) topActivity;
                        bundle.putInt("R", clTest.getClTestFinish().getStatus());
                        bundle.putInt("G", clTest.getClTestFinish().getTargetScore());
                        bundle.putString("S", clTest.getClTestFinish().getScore());
                        bundle.putString("E", clTest.getClTestFinish().getExp());
                        message.setData(bundle);
                        message.what = 6;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                    }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_ROOM_USER_LIST, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            OnlineLoadRoomUserListVO loadRoomUserList = receivedMessage.getLoadRoomUserList();
            if (topActivity instanceof OLPlayHall) {
                OLPlayHall olPlayHall = (OLPlayHall) topActivity;
                Bundle bundle2 = new Bundle();
                List<OnlineLoadRoomUserVO> roomUserList = loadRoomUserList.getLoadRoomUserList();
                for (int i = 0; i < roomUserList.size(); i++) {
                    OnlineLoadRoomUserVO loadRoomUser = roomUserList.get(i);
                    Bundle bundle3 = new Bundle();
                    bundle3.putString("U", loadRoomUser.getName());
                    bundle3.putString("S", loadRoomUser.getGender());
                    bundle3.putInt("LV", loadRoomUser.getLv());
                    bundle3.putInt("R", loadRoomUserList.getRoomId());
                    bundle2.putBundle(String.valueOf(i), bundle3);
                }
                bundle.putBundle("L", bundle2);
                bundle.putInt("R", loadRoomUserList.getRoomId());
                bundle.putInt("S", loadRoomUserList.getIsPlaying() ? 1 : 0);
                bundle.putInt("P", loadRoomUserList.getIsEncrypt() ? 1 : 0);
                message.setData(bundle);
                message.what = 12;
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.COUPLE, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            message.what = 22;
            OnlineCoupleVO couple = receivedMessage.getCouple();
            switch (couple.getType()) {
                case 2:
                case 3:
                case 5:
                    bundle.putString("MSG_C", couple.getContent());
                    bundle.putInt("MSG_CI", couple.getCoupleRoomPosition());
                    bundle.putInt("MSG_CT", couple.getCoupleType());
                    bundle.putInt("MSG_T", couple.getType());
                    break;
                case 4:
                    JSONObject jsonObject = new JSONObject();
                    JSONObject jsonObjectP = new JSONObject();
                    JSONObject jsonObjectC = new JSONObject();
                    JSONObject jsonObjectI = new JSONObject();
                    try {
                        jsonObjectP.put("D_H", couple.getCoupleDialog().getDialogUser().getClothes().getHair());
                        jsonObjectP.put("D_E", couple.getCoupleDialog().getDialogUser().getClothes().getEye());
                        jsonObjectP.put("D_J", couple.getCoupleDialog().getDialogUser().getClothes().getJacket());
                        jsonObjectP.put("D_T", couple.getCoupleDialog().getDialogUser().getClothes().getTrousers());
                        jsonObjectP.put("D_S", couple.getCoupleDialog().getDialogUser().getClothes().getShoes());
                        jsonObjectP.put("N", couple.getCoupleDialog().getDialogUser().getName());
                        jsonObjectP.put("L", couple.getCoupleDialog().getDialogUser().getLv());
                        jsonObjectP.put("C", couple.getCoupleDialog().getDialogUser().getCl());
                        jsonObjectP.put("S", couple.getCoupleDialog().getDialogUser().getGender());
                        jsonObject.put("P", jsonObjectP);
                        jsonObjectC.put("D_H", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getHair());
                        jsonObjectC.put("D_E", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getEye());
                        jsonObjectC.put("D_J", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getJacket());
                        jsonObjectC.put("D_T", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getTrousers());
                        jsonObjectC.put("D_S", couple.getCoupleDialog().getDialogCoupleUser().getClothes().getShoes());
                        jsonObjectC.put("N", couple.getCoupleDialog().getDialogCoupleUser().getName());
                        jsonObjectC.put("L", couple.getCoupleDialog().getDialogCoupleUser().getLv());
                        jsonObjectC.put("C", couple.getCoupleDialog().getDialogCoupleUser().getCl());
                        jsonObjectC.put("S", couple.getCoupleDialog().getDialogCoupleUser().getGender());
                        jsonObject.put("C", jsonObjectC);
                        jsonObjectI.put("B", couple.getCoupleDialog().getCoupleDialogInfo().getDeclaration());
                        jsonObjectI.put("P", couple.getCoupleDialog().getCoupleDialogInfo().getBlessing());
                        jsonObjectI.put("T", couple.getCoupleDialog().getCoupleDialogInfo().getType());
                        jsonObjectI.put("I", couple.getCoupleDialog().getCoupleDialogInfo().getRoomPosition());
                        jsonObject.put("I", jsonObjectI);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bundle.putString("MSG_C", jsonObject.toString());
                    bundle.putInt("MSG_CI", couple.getCoupleRoomPosition());
                    bundle.putInt("MSG_CT", couple.getCoupleType());
                    bundle.putInt("MSG_T", couple.getType());
                    break;
            }
            if (topActivity instanceof OLPlayRoom) {
                message.setData(bundle);
                ((OLPlayRoom) topActivity).olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom) {
                message.setData(bundle);
                ((OLPlayKeyboardRoom) topActivity).olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });
    }

    public static void receive(int msgType, OnlineBaseVO receivedMessage) throws Exception {
        ReceiveTask receiveTask = receiveTaskMap.get(msgType);
        if (receiveTask != null) {
            receiveTask.run(receivedMessage, JPStack.top(), Message.obtain());
        }
    }

    private static User buildAndPutUser(OLPlayRoomActivity olPlayRoomActivity, OnlineRoomPositionUserVO roomPositionUser) {
        User user = new User((byte) roomPositionUser.getPosition(), roomPositionUser.getName(),
                roomPositionUser.getClothes().getHair(),
                roomPositionUser.getClothes().getEye(),
                roomPositionUser.getClothes().getJacket(),
                roomPositionUser.getClothes().getTrousers(),
                roomPositionUser.getClothes().getShoes(),
                roomPositionUser.getGender(),
                roomPositionUser.getUserStatus(),
                roomPositionUser.getPositionStatus(),
                roomPositionUser.getLv(),
                roomPositionUser.getColor(),
                roomPositionUser.getCl(),
                roomPositionUser.getHand(),
                roomPositionUser.getCoupleType(),
                String.valueOf(roomPositionUser.getFamily()));
        olPlayRoomActivity.putRoomPlayerMap(user.getPosition(), user);
        return user;
    }

    private static void fillUserBundle(Bundle bundle, int i, User user) {
        Bundle bundle2 = new Bundle();
        bundle2.putByte("PI", user.getPosition());
        bundle2.putString("N", user.getPlayerName());
        bundle2.putString("S", user.getSex());
        bundle2.putString("IR", user.getStatus());
        bundle2.putString("IH", user.getIshost());
        bundle2.putInt("IV", user.getKuang());
        bundle2.putInt("LV", user.getLevel());
        bundle2.putInt("TR", user.getTrousers());
        bundle2.putInt("JA", user.getJacket());
        bundle2.putInt("EY", user.getEye());
        bundle2.putInt("HA", user.getHair());
        bundle2.putInt("SH", user.getShoes());
        bundle2.putInt("CL", user.getClevel());
        bundle2.putInt("GR", user.getHand());
        bundle2.putInt("CP", user.getCpKind());
        bundle2.putString("I", user.getFamilyID());
        bundle.putBundle(String.valueOf(i), bundle2);
    }
}
