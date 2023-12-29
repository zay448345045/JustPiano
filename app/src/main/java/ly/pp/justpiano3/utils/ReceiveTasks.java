package ly.pp.justpiano3.utils;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONObject;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.util.internal.StringUtil;
import ly.pp.justpiano3.activity.local.PianoPlay;
import ly.pp.justpiano3.activity.online.OLBaseActivity;
import ly.pp.justpiano3.activity.online.OLChallenge;
import ly.pp.justpiano3.activity.online.OLFamily;
import ly.pp.justpiano3.activity.online.OLMainMode;
import ly.pp.justpiano3.activity.online.OLPlayDressRoom;
import ly.pp.justpiano3.activity.online.OLPlayHall;
import ly.pp.justpiano3.activity.online.OLPlayHallRoom;
import ly.pp.justpiano3.activity.online.OLPlayKeyboardRoom;
import ly.pp.justpiano3.activity.online.OLPlayRoom;
import ly.pp.justpiano3.activity.online.OLRoomActivity;
import ly.pp.justpiano3.constant.OnlineProtocolType;
import ly.pp.justpiano3.entity.Room;
import ly.pp.justpiano3.entity.User;
import ly.pp.justpiano3.task.ReceiveTask;
import protobuf.vo.OnlineChallengeUserVO;
import protobuf.vo.OnlineChallengeVO;
import protobuf.vo.OnlineChangeClothesVO;
import protobuf.vo.OnlineChangeRoomPositionVO;
import protobuf.vo.OnlineClTestVO;
import protobuf.vo.OnlineCoupleVO;
import protobuf.vo.OnlineDailyTimeUserVO;
import protobuf.vo.OnlineDailyVO;
import protobuf.vo.OnlineDialogVO;
import protobuf.vo.OnlineEnterHallVO;
import protobuf.vo.OnlineFamilyInfoVO;
import protobuf.vo.OnlineFamilyUserVO;
import protobuf.vo.OnlineFamilyVO;
import protobuf.vo.OnlineFriendUserVO;
import protobuf.vo.OnlineHallChatVO;
import protobuf.vo.OnlineHallVO;
import protobuf.vo.OnlineLoadPlayUserVO;
import protobuf.vo.OnlineLoadRoomListVO;
import protobuf.vo.OnlineLoadRoomPositionVO;
import protobuf.vo.OnlineLoadRoomUserListVO;
import protobuf.vo.OnlineLoadRoomUserVO;
import protobuf.vo.OnlineLoadUserCoupleVO;
import protobuf.vo.OnlineLoadUserInfoVO;
import protobuf.vo.OnlineLoadUserListVO;
import protobuf.vo.OnlineLoadUserVO;
import protobuf.vo.OnlineMailVO;
import protobuf.vo.OnlineMiniGradeOnVO;
import protobuf.vo.OnlinePlayFinishVO;
import protobuf.vo.OnlinePlayGradeVO;
import protobuf.vo.OnlinePlayUserVO;
import protobuf.vo.OnlineRoomChatVO;
import protobuf.vo.OnlineRoomPositionUserVO;
import protobuf.vo.OnlineRoomVO;
import protobuf.vo.OnlineSetMiniGradeVO;
import protobuf.vo.OnlineSetUserInfoVO;
import protobuf.vo.OnlineShopProductVO;
import protobuf.vo.OnlineShopVO;
import protobuf.vo.OnlineUserInfoDialogVO;
import protobuf.vo.OnlineUserVO;

public final class ReceiveTasks {

    public static final Map<Integer, ReceiveTask> receiveTaskMap = new ConcurrentHashMap<>();

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
            if (topActivity instanceof OLPlayRoom olPlayRoom) {
                olPlayRoom.olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLFamily olFamily) {
                olFamily.familyHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayHall olPlayHall) {
                olPlayHall.olPlayHallHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.PLAY_START, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayRoom olPlayRoom) {
                message.what = 5;
                Bundle bundle = new Bundle();
                bundle.putString("S", receivedMessage.getPlayStart().getSongPath());
                bundle.putInt("D", receivedMessage.getPlayStart().getTune());
                message.setData(bundle);
                olPlayRoom.olPlayRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.PLAY_FINISH, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof PianoPlay pianoPlay) {
                Bundle bundle = new Bundle();
                message.what = 3;
                OnlinePlayFinishVO playFinish = receivedMessage.getPlayFinish();
                for (int i = 0; i < playFinish.getPlayGradeList().size(); i++) {
                    OnlinePlayGradeVO playGrade = playFinish.getPlayGradeList().get(i);
                    Bundle innerBundle = new Bundle();
                    innerBundle.putString("I", playGrade.getIsPlaying() ? "P" : "");
                    innerBundle.putString("N", playGrade.getName());
                    innerBundle.putString("SC", String.valueOf(playGrade.getScore()));
                    innerBundle.putString("P", String.valueOf(playGrade.getGrade().getPerfect()));
                    innerBundle.putString("C", String.valueOf(playGrade.getGrade().getCool()));
                    innerBundle.putString("G", String.valueOf(playGrade.getGrade().getGreat()));
                    innerBundle.putString("B", String.valueOf(playGrade.getGrade().getBad()));
                    innerBundle.putString("M", String.valueOf(playGrade.getGrade().getMiss()));
                    innerBundle.putString("T", String.valueOf(playGrade.getGrade().getCombo()));
                    innerBundle.putString("E", String.valueOf(playGrade.getGrade().getExp()));
                    innerBundle.putString("GR", String.valueOf(playGrade.getGrade().getGradeColor()));
                    bundle.putBundle(String.valueOf(i), innerBundle);
                }
                message.setData(bundle);
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CREATE_ROOM, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall olPlayHall) {
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
            if (topActivity instanceof OLPlayHall olPlayHall) {
                Bundle bundle = new Bundle();
                switch (receivedMessage.getEnterRoom().getStatus()) {
                    case 0 -> {
                        bundle.putString("result", "该房间正在弹奏中!");
                        message.what = 4;
                    }
                    case 1 -> {
                        bundle.putString("result", "该房间人数已满!");
                        message.what = 4;
                    }
                    case 2 -> {
                        bundle.putString("result", "房间数已满!");
                        message.what = 4;
                    }
                    case 3 -> {
                        bundle.putString("result", "密码有误!");
                        message.what = 4;
                    }
                    case 4 -> {
                        message.what = 2;
                        bundle.putString("R", receivedMessage.getEnterRoom().getRoomName());
                        bundle.putByte("ID", (byte) receivedMessage.getEnterRoom().getRoomId());
                        bundle.putString("isHost", "G");
                        bundle.putInt("mode", receivedMessage.getEnterRoom().getRoomMode());
                    }
                    default -> {
                    }
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.KICKED_QUIT_ROOM, (receivedMessage, topActivity, message) -> {
            message.what = 8;
            if (topActivity instanceof OLPlayRoom olPlayRoom) {
                olPlayRoom.olPlayRoomHandler.handleMessage(message);
            } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOGIN, (receivedMessage, topActivity, message) -> {
            EncryptUtil.setServerTimeInterval(receivedMessage.getLogin().getTime());
            if (Objects.equals("X", receivedMessage.getLogin().getStatus())) {
                OnlineUtil.outLineAndDialog(topActivity.getApplicationContext());
            } else if (topActivity instanceof OLMainMode olMainMode) {
                switch (receivedMessage.getLogin().getStatus()) {
                    case "N" -> message.what = 4;
                    case "E" -> message.what = 5;
                    case "V" -> message.what = 6;
                    default -> message.what = 1;
                }
                olMainMode.olMainModeHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.HALL_CHAT, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall olPlayHall) {
                Bundle bundle = new Bundle();
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
            if (topActivity instanceof OLRoomActivity) {
                Bundle bundle = new Bundle();
                OnlineRoomChatVO roomChat = receivedMessage.getRoomChat();
                message.what = 2;
                bundle.putString("U", roomChat.getUserName());
                bundle.putString("M", roomChat.getMessage());
                bundle.putInt("T", roomChat.getType());
                if (roomChat.getType() == 1) {
                    bundle.putInt("V", roomChat.getColor());
                } else if (roomChat.getType() == OnlineProtocolType.MsgType.STREAM_MESSAGE) {
                    // 自定义了一个简单的协议
                    streamMessageBundleBuild(roomChat.getMessage(), bundle);
                }
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom olPlayRoom) {
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_ROOM_INFO, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLRoomActivity) {
                Bundle bundle = new Bundle();
                message.what = 10;
                bundle.putString("R", receivedMessage.getChangeRoomInfo().getRoomName());
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom olPlayRoom) {
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.PLAY_SONG, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayRoom olPlayRoom) {
                Bundle bundle = new Bundle();
                message.what = 3;
                bundle.putString("song_path", receivedMessage.getPlaySong().getSongPath().replace("\\/", "/"));
                bundle.putInt("diao", receivedMessage.getPlaySong().getTune());
                message.setData(bundle);
                olPlayRoom.olPlayRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHALLENGE, (receivedMessage, topActivity, message) -> {
            OnlineChallengeVO challenge = receivedMessage.getChallenge();
            switch (challenge.getType()) {
                case 1 -> {
                    if (topActivity instanceof OLChallenge olChallenge) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        for (int i = 0; i < challenge.getChallengeEnter().getUserScoreList().size(); i++) {
                            OnlineChallengeUserVO challengeUser = challenge.getChallengeEnter().getUserScoreList().get(i);
                            Bundle innerBundle = new Bundle();
                            innerBundle.putString("N", challengeUser.getName());
                            innerBundle.putString("S", String.valueOf(challengeUser.getScore()));
                            innerBundle.putString("T", challengeUser.getTime());
                            bundle.putBundle(String.valueOf(i), innerBundle);
                        }
                        bundle.putInt("S", challenge.getChallengeEnter().getScore());
                        bundle.putString("P", challenge.getChallengeEnter().getPosition());
                        bundle.putString("T", String.valueOf(challenge.getChallengeEnter().getChallengeNum()));
                        bundle.putString("Z", challenge.getChallengeEnter().getYesterdayPosition());
                        message.setData(bundle);
                        olChallenge.challengeHandler.handleMessage(message);
                    }
                }
                case 2 -> {
                    if (topActivity instanceof OLChallenge olChallenge) {
                        olChallenge.jpprogressBar.dismiss();
                        message.what = 2;
                        Bundle bundle = new Bundle();
                        bundle.putInt("R", challenge.getChallengeDialog().getAllowed() ? 1 : 0);
                        bundle.putString("I", challenge.getChallengeDialog().getMessage());
                        bundle.putString("P", GZIPUtil.ZIPTo(challenge.getChallengeDialog().getSongContent()));
                        message.setData(bundle);
                        olChallenge.challengeHandler.handleMessage(message);
                    }
                }
                case 3 -> {
                    if (topActivity instanceof PianoPlay pianoPlay) {
                        message.setData(new Bundle());
                        message.what = 5;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                    }
                }
                case 4 -> {
                    if (topActivity instanceof PianoPlay pianoPlay) {
                        Bundle bundle = new Bundle();
                        bundle.putString("I", challenge.getChallengeFinish().getMessage());
                        message.setData(bundle);
                        message.what = 9;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                    }
                }
                case 5 -> {
                    if (topActivity instanceof OLChallenge olChallenge) {
                        message.what = 5;
                        Bundle bundle = new Bundle();
                        bundle.putInt("P", challenge.getChallengePrize().getPrizeType());
                        bundle.putString("N", challenge.getChallengePrize().getPrizeName());
                        message.setData(bundle);
                        olChallenge.challengeHandler.handleMessage(message);
                    }
                }
                default -> {
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.BROADCAST, (receivedMessage, topActivity, message) -> {

        });

        receiveTaskMap.put(OnlineProtocolType.FAMILY, (receivedMessage, topActivity, message) -> {
            OnlineFamilyVO family = receivedMessage.getFamily();
            Bundle bundle = new Bundle();
            switch (family.getType()) {
                case 1 -> {
                    if (topActivity instanceof OLFamily olFamily) {
                        message.what = 1;
                        olFamily.jpprogressBar.dismiss();
                        for (int i = 0; i < family.getFamilyEnter().getFamilyUserList().size(); i++) {
                            OnlineFamilyUserVO familyUser = family.getFamilyEnter().getFamilyUserList().get(i);
                            Bundle innerBundle = new Bundle();
                            innerBundle.putString("N", familyUser.getName());
                            innerBundle.putString("C", String.valueOf(familyUser.getContribution()));
                            innerBundle.putString("L", String.valueOf(familyUser.getLv()));
                            innerBundle.putString("S", familyUser.getGender());
                            innerBundle.putString("O", String.valueOf(familyUser.getOnline() ? 1 : 0));
                            innerBundle.putString("P", String.valueOf(familyUser.getPosition()));
                            innerBundle.putString("D", familyUser.getLoginDate());
                            bundle.putBundle(String.valueOf(i), innerBundle);
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
                }
                case 2 -> {
                    if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                        message.what = 2;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        for (int i = 0; i < family.getFamilyList().getFamilyInfoList().size(); i++) {
                            OnlineFamilyInfoVO familyInfo = family.getFamilyList().getFamilyInfoList().get(i);
                            Bundle innerBundle = new Bundle();
                            innerBundle.putString("N", familyInfo.getName());
                            innerBundle.putString("C", String.valueOf(familyInfo.getContribution()));
                            innerBundle.putString("T", String.valueOf(familyInfo.getCapacity()));
                            innerBundle.putString("U", String.valueOf(familyInfo.getSize()));
                            innerBundle.putString("I", String.valueOf(familyInfo.getFamilyId()));
                            innerBundle.putByteArray("J", GZIPUtil.ZIPToArray(familyInfo.getPicture()));
                            bundle.putBundle(String.valueOf(i), innerBundle);
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
                }
                case 3 -> {
                    if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                        bundle.putString("I", family.getFamilyDialog().getMessage());
                        bundle.putInt("R", family.getFamilyDialog().getAllowed() ? 1 : 0);
                        message.setData(bundle);
                        message.what = 9;
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                }
                case 4 -> {
                    if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                        bundle.putInt("R", family.getFamilyCreate().getResult());
                        message.setData(bundle);
                        message.what = 10;
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                }
                case 5, 6, 7, 10 -> {
                    if (topActivity instanceof OLFamily olFamily) {
                        bundle.putString("I", family.getFamilyDialog().getMessage());
                        message.setData(bundle);
                        message.what = 5;
                        olFamily.familyHandler.handleMessage(message);
                    }
                }
                case 8 -> {
                    if (topActivity instanceof OLFamily olFamily) {
                        try {
                            bundle.putString("I", family.getFamilyDialog().getMessage());
                            bundle.putInt("R", family.getFamilyDialog().getAllowed() ? 1 : 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        message.setData(bundle);
                        message.what = 8;
                        olFamily.familyHandler.handleMessage(message);
                    }
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_ROOM_LIST, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall olPlayHall) {
                Bundle bundle = new Bundle();
                message.what = 3;
                OnlineLoadRoomListVO loadRoomList = receivedMessage.getLoadRoomList();
                for (int i = 0; i < loadRoomList.getRoomList().size(); i++) {
                    OnlineRoomVO roomRaw = loadRoomList.getRoomList().get(i);
                    Room room = new Room((byte) roomRaw.getRoomId(), roomRaw.getRoomName(), roomRaw.getFemaleNum(),
                            roomRaw.getMaleNum(), roomRaw.getIsPlaying() ? 1 : 0, roomRaw.getIsEncrypt() ? 1 : 0,
                            roomRaw.getColor(), roomRaw.getCloseNum(), roomRaw.getRoomMode());
                    Bundle innerBundle = new Bundle();
                    olPlayHall.putRoomToMap(room.getRoomID(), room);
                    innerBundle.putByte("I", room.getRoomID());
                    innerBundle.putString("N", room.getRoomName());
                    innerBundle.putIntArray("UA", room.getPeople());
                    innerBundle.putBoolean("IF", room.isPeopleFull());
                    innerBundle.putInt("IP", room.isPlaying());
                    innerBundle.putInt("PA", room.isPassword());
                    innerBundle.putInt("V", room.getRoomColor());
                    innerBundle.putInt("D", room.getRoomMode());
                    bundle.putBundle(String.valueOf(i), innerBundle);
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_ROOM_POSITION, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLRoomActivity olRoomActivity) {
                OnlineChangeRoomPositionVO changeRoomPosition = receivedMessage.getChangeRoomPosition();
                message.what = 1;
                for (OnlineRoomPositionUserVO roomPositionUser : changeRoomPosition.getRoomPositionUserList()) {
                    buildAndPutUser(olRoomActivity, roomPositionUser);
                }
                Bundle bundle = new Bundle();
                Iterator<User> it = OLBaseActivity.getRoomPlayerMap().values().iterator();
                for (int i = 0; it.hasNext(); i++) {
                    fillUserBundle(bundle, i, it.next());
                }
                bundle.putString("SI", "");
                bundle.putInt("diao", 0);
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom olPlayRoom) {
                    bundle.putString("MSG_C", "");
                    bundle.putInt("MSG_CI", 0);
                    bundle.putInt("MSG_CT", 0);
                    bundle.putBoolean("MSG_I", false);
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_ROOM_POSITION, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLRoomActivity olRoomActivity) {
                OnlineLoadRoomPositionVO loadRoomPosition = receivedMessage.getLoadRoomPosition();
                message.what = 1;
                Bundle bundle = new Bundle();
                for (int i = 0; i < loadRoomPosition.getRoomPositionUserList().size(); i++) {
                    OnlineRoomPositionUserVO roomPositionUser = loadRoomPosition.getRoomPositionUserList().get(i);
                    fillUserBundle(bundle, i, buildAndPutUser(olRoomActivity, roomPositionUser));
                }
                bundle.putString("SI", loadRoomPosition.getSongPath());
                bundle.putInt("diao", loadRoomPosition.getTune());
                message.setData(bundle);
                // 兼容4.8暂时这样写
                message.arg1 = loadRoomPosition.getSongProgress();
                if (topActivity instanceof OLPlayRoom olPlayRoom) {
                    bundle.putString("MSG_C", loadRoomPosition.getRoomPositionCouple().getCoupleName());
                    bundle.putInt("MSG_CI", loadRoomPosition.getRoomPositionCouple().getCouplePosition());
                    bundle.putInt("MSG_CT", loadRoomPosition.getRoomPositionCouple().getCoupleType());
                    bundle.putBoolean("MSG_I", loadRoomPosition.getRoomPositionCouple().getInvite());
                    message.setData(bundle);
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    message.setData(bundle);
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_ROOM_LIST, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall olPlayHall) {
                Bundle bundle = new Bundle();
                message.what = 3;
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
                    Bundle innerBundle = new Bundle();
                    innerBundle.putByte("I", room.getRoomID());
                    innerBundle.putString("N", room.getRoomName());
                    innerBundle.putIntArray("UA", room.getPeople());
                    innerBundle.putBoolean("IF", room.isPeopleFull());
                    innerBundle.putInt("IP", room.isPlaying());
                    innerBundle.putInt("PA", room.isPassword());
                    innerBundle.putInt("V", room.getRoomColor());
                    innerBundle.putInt("D", room.getRoomMode());
                    bundle.putBundle(String.valueOf(i), innerBundle);
                }
                message.setData(bundle);
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_PLAY_USER, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof PianoPlay pianoPlay) {
                Bundle bundle = new Bundle();
                message.what = 1;
                message.arg1 = 0;
                OnlineLoadPlayUserVO loadPlayUser = receivedMessage.getLoadPlayUser();
                List<OnlinePlayUserVO> playUserList = loadPlayUser.getPlayUserList();
                for (int i = 0; i < playUserList.size(); i++) {
                    OnlinePlayUserVO playUser = playUserList.get(i);
                    Bundle innerBundle = new Bundle();
                    innerBundle.putString("G", String.valueOf(playUser.getHand()));
                    innerBundle.putString("U", playUser.getName());
                    innerBundle.putString("M", String.valueOf(playUser.getMode()));
                    bundle.putBundle(String.valueOf(i), innerBundle);
                }
                message.setData(bundle);
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.MINI_GRADE, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof PianoPlay pianoPlay) {
                User user = OLBaseActivity.getRoomPlayerMap().get((byte) receivedMessage.getMiniGrade().getRoomPosition());
                if (user == null) {
                    return;
                }
                Bundle bundle = new Bundle();
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
                    User currentUser = OLBaseActivity.getRoomPlayerMap().get(b);
                    if (currentUser == null) {
                        continue;
                    }
                    if (!currentUser.getPlayerName().isEmpty()) {
                        Bundle innerBundle = new Bundle();
                        innerBundle.putString("G", String.valueOf(currentUser.getHand()));
                        innerBundle.putString("U", currentUser.getPlayerName());
                        innerBundle.putString("M", String.valueOf(currentUser.getScore()));
                        innerBundle.putString("T", String.valueOf(currentUser.getCombo()));
                        bundle.putBundle(String.valueOf(i), innerBundle);
                        i++;
                    }
                }
                message.what = 2;
                message.setData(bundle);
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.SHOP, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayDressRoom olPlayDressRoom) {
                OnlineShopVO shop = receivedMessage.getShop();
                Bundle bundle = new Bundle();
                switch (shop.getType()) {
                    case 1 -> {  // 加载商品
                        message.what = 3;
                        olPlayDressRoom.jpprogressBar.dismiss();
                        for (int i = 0; i < shop.getShopProductShow().getProductList().size(); i++) {
                            OnlineShopProductVO shopProduct = shop.getShopProductShow().getProductList().get(i);
                            Bundle innerBundle = new Bundle();
                            innerBundle.putString("N", shopProduct.getName());
                            innerBundle.putString("D", shopProduct.getDescription());
                            innerBundle.putInt("G", shopProduct.getPrice());
                            innerBundle.putString("P", shopProduct.getPicture());
                            innerBundle.putInt("I", shopProduct.getId());
                            bundle.putBundle(String.valueOf(i), innerBundle);
                        }
                        bundle.putString("G", String.valueOf(shop.getShopProductShow().getGold()));
                        message.setData(bundle);
                        olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
                    }
                    case 2 -> {  // 购买商品
                        bundle.putString("I", shop.getShopProductBuy().getMessage());
                        bundle.putString("G", String.valueOf(shop.getShopProductBuy().getGold()));
                        message.setData(bundle);
                        message.what = 4;
                        olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
                    }
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.RECOMMEND_SONG, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayRoom olPlayRoom) {
                Bundle bundle = new Bundle();
                message.what = 4;
                bundle.putString("U", receivedMessage.getRecommendSong().getName());
                bundle.putString("M", "推荐歌曲:");
                bundle.putString("I", receivedMessage.getRecommendSong().getSongPath());
                bundle.putInt("D", 0);
                bundle.putInt("T", 0);
                message.setData(bundle);
                olPlayRoom.olPlayRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_USER, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                Bundle bundle = new Bundle();
                Bundle dataBundle = new Bundle();
                message.what = 0;
                OnlineLoadUserVO loadUser = receivedMessage.getLoadUser();
                for (int i = 0; i < loadUser.getHallList().size(); i++) {
                    OnlineHallVO hall = loadUser.getHallList().get(i);
                    Bundle innerBundle = new Bundle();
                    innerBundle.putByte("I", (byte) hall.getId());
                    innerBundle.putString("N", hall.getName());
                    innerBundle.putInt("PN", hall.getSize());
                    innerBundle.putInt("TN", hall.getCapacity());
                    innerBundle.putInt("W", hall.getEncrypt() ? 1 : 0);
                    dataBundle.putBundle(String.valueOf(i), innerBundle);
                }
                bundle.putBundle("L", dataBundle);
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
                olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.ENTER_HALL, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
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
                olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.SET_USER_INFO, (receivedMessage, topActivity, message) -> {
            OnlineSetUserInfoVO setUserInfo = receivedMessage.getSetUserInfo();
            int type = setUserInfo.getType();
            if (type == 3 || type == 4) {
                if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("T", 1);
                    bundle.putString("I", setUserInfo.getMessage());
                    bundle.putString("N", setUserInfo.getCoupleTitle());
                    message.what = 1;
                    message.setData(bundle);
                    olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                }
                return;
            }
            Handler handler = null;
            switch (setUserInfo.getLocation()) {
                case 0 -> {
                    if (topActivity instanceof OLPlayRoom olPlayRoom) {
                        handler = olPlayRoom.olPlayRoomHandler;
                        message.what = 9;
                        break;
                    } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                        handler = olPlayKeyboardRoom.olPlayKeyboardRoomHandler;
                        message.what = 9;
                        break;
                    }
                    return;
                }
                case 1 -> {
                    if (topActivity instanceof OLPlayHall olPlayHall) {
                        handler = olPlayHall.olPlayHallHandler;
                        message.what = 8;
                        break;
                    } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                        handler = olPlayKeyboardRoom.olPlayKeyboardRoomHandler;
                        message.what = 8;
                        break;
                    }
                    return;
                }
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
            if (topActivity instanceof PianoPlay pianoPlay) {
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
                pianoPlay.pianoPlayHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CHANGE_CLOTHES, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayDressRoom olPlayDressRoom) {
                OnlineChangeClothesVO changeClothes = receivedMessage.getChangeClothes();
                int type = changeClothes.getType();
                message.what = type;
                Bundle bundle = new Bundle();
                switch (type) {
                    case 0 ->  // 保存服装
                            bundle.putString("I", changeClothes.getMessage());
                    case 1 -> {  // 进入换衣间加载音符和解锁情况
                        bundle.putString("G", String.valueOf(changeClothes.getGold()));
                        bundle.putByteArray("U", GZIPUtil.ZIPToArray(changeClothes.getUnlock()));
                    }
                    case 2 -> {  // 购买服装
                        bundle.putString("I", changeClothes.getMessage());
                        bundle.putString("G", String.valueOf(changeClothes.getGold()));
                        bundle.putInt("U_T", changeClothes.getBuyClothesType());
                        bundle.putInt("U_I", changeClothes.getBuyClothesId());
                    }
                    case 3 -> {  // 服务器下发服装价格
                        message.what = 5;
                        int[] priseArr = new int[changeClothes.getBuyClothesPricesCount()];
                        for (int y = 0; y < changeClothes.getBuyClothesPricesCount(); y++) {
                            priseArr[y] = changeClothes.getBuyClothesPricesList().get(y);
                        }
                        bundle.putInt("C_T", changeClothes.getBuyClothesType());
                        bundle.putIntArray("P", priseArr);
                    }
                }
                message.setData(bundle);
                olPlayDressRoom.olPlayDressRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_USER_INFO, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            OnlineLoadUserInfoVO loadUserInfo = receivedMessage.getLoadUserInfo();
            switch (loadUserInfo.getType()) {
                case 1 -> {
                    for (int i = 0; i < loadUserInfo.getLoadUserFriend().getFriendUserList().size(); i++) {
                        OnlineFriendUserVO friendUser = loadUserInfo.getLoadUserFriend().getFriendUserList().get(i);
                        Bundle innerBundle = new Bundle();
                        innerBundle.putString("F", friendUser.getName());
                        innerBundle.putInt("O", friendUser.getOnline() ? 1 : 0);
                        innerBundle.putString("S", friendUser.getGender());
                        innerBundle.putInt("LV", friendUser.getLv());
                        bundle.putBundle(String.valueOf(i), innerBundle);
                    }
                    switch (loadUserInfo.getLoadUserFriend().getLocation()) {
                        case 0 -> {
                            if (topActivity instanceof OLPlayRoom olPlayRoom) {
                                message.what = 11;
                                message.setData(bundle);
                                olPlayRoom.olPlayRoomHandler.handleMessage(message);
                            } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                                message.what = 11;
                                message.setData(bundle);
                                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                            }
                        }
                        case 1 -> {
                            message.what = 5;
                            message.setData(bundle);
                            if (topActivity instanceof OLPlayHall olPlayHall) {
                                olPlayHall.olPlayHallHandler.handleMessage(message);
                            }
                        }
                        case 2 -> {
                            message.what = 3;
                            message.setData(bundle);
                            if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                                olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                            }
                        }
                        default -> {
                        }
                    }
                }
                case 2 -> {
                    for (int i = 0; i < loadUserInfo.getLoadUserMail().getMailList().size(); i++) {
                        OnlineMailVO mail = loadUserInfo.getLoadUserMail().getMailList().get(i);
                        Bundle innerBundle = new Bundle();
                        innerBundle.putString("F", mail.getUserFrom());
                        innerBundle.putString("M", mail.getMessage());
                        innerBundle.putString("T", DateUtil.format(new Date(mail.getTime())));
                        innerBundle.putInt("type", 0);
                        bundle.putBundle(String.valueOf(i), innerBundle);
                    }
                    message.what = 4;
                    message.setData(bundle);
                    if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                }
                case 3 -> {
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
                    if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                }
                default -> {
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_USER_LIST, (receivedMessage, topActivity, message) -> {
            Bundle bundle = new Bundle();
            OnlineLoadUserListVO loadUserList = receivedMessage.getLoadUserList();
            for (int i = 0; i < loadUserList.getUserList().size(); i++) {
                OnlineUserVO user = loadUserList.getUserList().get(i);
                Bundle innerBundle = new Bundle();
                innerBundle.putString("U", user.getName());
                innerBundle.putString("S", user.getGender());
                innerBundle.putInt("LV", user.getLv());
                innerBundle.putInt("R", user.getRoomId());
                bundle.putBundle(String.valueOf(i), innerBundle);
            }
            if (loadUserList.getIsInRoom()) {
                message.what = 15;
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom olPlayRoom) {
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            } else {
                message.what = 7;
                message.setData(bundle);
                if (topActivity instanceof OLPlayHall olPlayHall) {
                    olPlayHall.olPlayHallHandler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.DIALOG, (receivedMessage, topActivity, message) -> {
            OnlineDialogVO dialog = receivedMessage.getDialog();
            int type = dialog.getType();
            Handler handler = null;
            switch (dialog.getLocation()) {
                case 0 -> {
                    if (topActivity instanceof OLPlayRoom olPlayRoom) {
                        handler = olPlayRoom.olPlayRoomHandler;
                        message.what = 14;
                        break;
                    } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                        handler = olPlayKeyboardRoom.olPlayKeyboardRoomHandler;
                        message.what = 14;
                        break;
                    }
                    return;
                }
                case 1 -> {
                    if (topActivity instanceof OLPlayHall olPlayHall) {
                        handler = olPlayHall.olPlayHallHandler;
                        message.what = 9;
                        break;
                    }
                    return;
                }
                case 2 -> {
                    if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                        handler = olPlayHallRoom.olPlayHallRoomHandler;
                        message.what = 5;
                        break;
                    }
                    return;
                }
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
                    if (!TextUtils.isEmpty(dialog.getBizData())) {
                        bundle.putString("F", new JSONObject(dialog.getBizData()).getString("handlingFee"));
                    }
                    message.setData(bundle);
                    handler.handleMessage(message);
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.DAILY, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHallRoom olPlayHallRoom) {
                Bundle bundle = new Bundle();
                OnlineDailyVO daily = receivedMessage.getDaily();
                switch (daily.getType()) {
                    case 1 -> {
                        message.what = 11;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        for (int i = 0; i < daily.getDailyTimeList().getDailyTimeUserList().size(); i++) {
                            OnlineDailyTimeUserVO dailyTimeUser = daily.getDailyTimeList().getDailyTimeUserList().get(i);
                            Bundle innerBundle = new Bundle();
                            innerBundle.putString("N", dailyTimeUser.getName());
                            innerBundle.putString("T", String.valueOf(dailyTimeUser.getOnlineTime()));
                            innerBundle.putString("B", dailyTimeUser.getBonus());
                            innerBundle.putString("G", String.valueOf(dailyTimeUser.getBonusGet() ? 1 : 0));
                            bundle.putBundle(String.valueOf(i), innerBundle);
                        }
                        bundle.putString("M", daily.getDailyTimeList().getTomorrowBonus());
                        bundle.putString("T", String.valueOf(daily.getDailyTimeList().getTodayOnlineTime()));
                        message.setData(bundle);
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                    case 2 -> {
                        bundle.putString("M", daily.getDailyPrizeGet().getMessage());
                        message.setData(bundle);
                        message.what = 12;
                        olPlayHallRoom.jpprogressBar.dismiss();
                        olPlayHallRoom.olPlayHallRoomHandler.handleMessage(message);
                    }
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.KEYBOARD, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                long[] array = new long[receivedMessage.getKeyboardNote().getDataCount()];
                for (int i = 0; i < receivedMessage.getKeyboardNote().getDataCount(); i++) {
                    array[i] = receivedMessage.getKeyboardNote().getData(i);
                }
                Bundle bundle = new Bundle();
                bundle.putLongArray("NOTES", array);
                message.what = 5;
                message.setData(bundle);
                olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.CL_TEST, (receivedMessage, topActivity, message) -> {
            OnlineClTestVO clTest = receivedMessage.getClTest();
            Bundle bundle = new Bundle();
            switch (clTest.getType()) {
                case 0 -> {
                    if (topActivity instanceof OLPlayHall olPlayHall) {
                        olPlayHall.jpprogressBar.dismiss();
                        bundle.putInt("result", clTest.getClTestDialog().getAllowed() ? 1 : 0);
                        bundle.putString("info", clTest.getClTestDialog().getMessage());
                        message.setData(bundle);
                        message.what = 11;
                        olPlayHall.olPlayHallHandler.handleMessage(message);
                    }
                }
                case 1 -> {
                    if (topActivity instanceof OLPlayHall olPlayHall) {
                        olPlayHall.jpprogressBar.dismiss();
                        bundle.putInt("songsID", clTest.getClTestSong().getCl());
                        bundle.putString("songBytes", GZIPUtil.ZIPTo(clTest.getClTestSong().getSongContent()));
                        bundle.putInt("hand", clTest.getClTestSong().getHand());
                        message.setData(bundle);
                        message.what = 13;
                        olPlayHall.olPlayHallHandler.handleMessage(message);
                    }
                }
                case 2 -> {
                    if (topActivity instanceof PianoPlay pianoPlay) {
                        message.setData(bundle);
                        message.what = 5;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                    }
                }
                case 3 -> {
                    if (topActivity instanceof PianoPlay pianoPlay) {
                        bundle.putInt("R", clTest.getClTestFinish().getStatus());
                        bundle.putInt("G", clTest.getClTestFinish().getTargetScore());
                        bundle.putString("S", clTest.getClTestFinish().getScore());
                        bundle.putString("E", clTest.getClTestFinish().getExp());
                        message.setData(bundle);
                        message.what = 6;
                        pianoPlay.pianoPlayHandler.handleMessage(message);
                    }
                }
            }
        });

        receiveTaskMap.put(OnlineProtocolType.LOAD_ROOM_USER_LIST, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLPlayHall olPlayHall) {
                Bundle bundle = new Bundle();
                OnlineLoadRoomUserListVO loadRoomUserList = receivedMessage.getLoadRoomUserList();
                Bundle dataBundle = new Bundle();
                List<OnlineLoadRoomUserVO> roomUserList = loadRoomUserList.getLoadRoomUserList();
                for (int i = 0; i < roomUserList.size(); i++) {
                    OnlineLoadRoomUserVO loadRoomUser = roomUserList.get(i);
                    Bundle innerBundle = new Bundle();
                    innerBundle.putString("U", loadRoomUser.getName());
                    innerBundle.putString("S", loadRoomUser.getGender());
                    innerBundle.putInt("LV", loadRoomUser.getLv());
                    innerBundle.putInt("R", loadRoomUserList.getRoomId());
                    dataBundle.putBundle(String.valueOf(i), innerBundle);
                }
                bundle.putBundle("L", dataBundle);
                bundle.putInt("R", loadRoomUserList.getRoomId());
                bundle.putInt("S", loadRoomUserList.getIsPlaying() ? 1 : 0);
                bundle.putInt("P", loadRoomUserList.getIsEncrypt() ? 1 : 0);
                message.setData(bundle);
                message.what = 12;
                olPlayHall.olPlayHallHandler.handleMessage(message);
            }
        });

        receiveTaskMap.put(OnlineProtocolType.COUPLE, (receivedMessage, topActivity, message) -> {
            if (topActivity instanceof OLRoomActivity) {
                Bundle bundle = new Bundle();
                message.what = 22;
                OnlineCoupleVO couple = receivedMessage.getCouple();
                switch (couple.getType()) {
                    case 2, 3, 5 -> {
                        bundle.putString("MSG_C", couple.getContent());
                        bundle.putInt("MSG_CI", couple.getCoupleRoomPosition());
                        bundle.putInt("MSG_CT", couple.getCoupleType());
                        bundle.putInt("MSG_T", couple.getType());
                    }
                    case 4 -> {
                        JSONObject jsonObject = new JSONObject();
                        JSONObject jsonObjectP = new JSONObject();
                        JSONObject jsonObjectC = new JSONObject();
                        JSONObject jsonObjectI = new JSONObject();
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
                        bundle.putString("MSG_C", jsonObject.toString());
                        bundle.putInt("MSG_CI", couple.getCoupleRoomPosition());
                        bundle.putInt("MSG_CT", couple.getCoupleType());
                        bundle.putInt("MSG_T", couple.getType());
                    }
                }
                message.setData(bundle);
                if (topActivity instanceof OLPlayRoom olPlayRoom) {
                    olPlayRoom.olPlayRoomHandler.handleMessage(message);
                } else if (topActivity instanceof OLPlayKeyboardRoom olPlayKeyboardRoom) {
                    olPlayKeyboardRoom.olPlayKeyboardRoomHandler.handleMessage(message);
                }
            }
        });
    }

    /**
     * 设置流消息的 bundle
     *
     * @param message 当前事件消息
     * @param bundle  当前消息的bundle
     */
    private static void streamMessageBundleBuild(String message, Bundle bundle) {
        // 流消息开始
        if (message.startsWith(OnlineProtocolType.MsgType.StreamMsg.START)) {
            bundle.putString(OnlineProtocolType.MsgType.StreamMsg.PARAM_ID, message.substring(OnlineProtocolType.MsgType.StreamMsg.START.length()));
            bundle.putBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS, true);
            bundle.putString("M", StringUtil.EMPTY_STRING);
        }
        // 流消息数据传输
        else if (message.startsWith(OnlineProtocolType.MsgType.StreamMsg.DATA)) {
            String[] split = message.split(String.valueOf(StringUtil.LINE_FEED));
            bundle.putString(OnlineProtocolType.MsgType.StreamMsg.PARAM_ID, split[0].substring(OnlineProtocolType.MsgType.StreamMsg.DATA.length()));
            bundle.putBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS, true);
            bundle.putString("M", message.substring((split[0] + StringUtil.LINE_FEED).length()));
        }
        // 流消息结束
        else if (message.startsWith(OnlineProtocolType.MsgType.StreamMsg.END)) {
            bundle.putString(OnlineProtocolType.MsgType.StreamMsg.PARAM_ID, message.substring(OnlineProtocolType.MsgType.StreamMsg.END.length()));
            bundle.putBoolean(OnlineProtocolType.MsgType.StreamMsg.PARAM_STATUS, false);
            bundle.putString("M", StringUtil.EMPTY_STRING);
        }
    }

    private static User buildAndPutUser(OLRoomActivity olRoomActivity, OnlineRoomPositionUserVO roomPositionUser) {
        User user = new User((byte) roomPositionUser.getPosition(),
                roomPositionUser.getName(),
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
        OLBaseActivity.getRoomPlayerMap().put(user.getPosition(), user);
        return user;
    }

    private static void fillUserBundle(Bundle bundle, int i, User user) {
        Bundle innerBundle = new Bundle();
        innerBundle.putByte("PI", user.getPosition());
        innerBundle.putString("N", user.getPlayerName());
        innerBundle.putString("S", user.getSex());
        innerBundle.putString("IR", user.getStatus());
        innerBundle.putString("IH", user.getIshost());
        innerBundle.putInt("IV", user.getColor());
        innerBundle.putInt("LV", user.getLevel());
        innerBundle.putInt("TR", user.getTrousers());
        innerBundle.putInt("JA", user.getJacket());
        innerBundle.putInt("EY", user.getEye());
        innerBundle.putInt("HA", user.getHair());
        innerBundle.putInt("SH", user.getShoes());
        innerBundle.putInt("CL", user.getCl());
        innerBundle.putInt("GR", user.getHand());
        innerBundle.putInt("CP", user.getCpKind());
        innerBundle.putString("I", user.getFamilyID());
        bundle.putBundle(String.valueOf(i), innerBundle);
    }
}
