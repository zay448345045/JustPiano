// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: OnlineRequestProtocol.proto

package protobuf.dto;

public interface OnlineBaseDTOOrBuilder extends
    // @@protoc_insertion_point(interface_extends:protobuf.dto.OnlineBaseDTO)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * 查看用户个人资料对话框
   * </pre>
   *
   * <code>.protobuf.dto.OnlineUserInfoDialogDTO user_info_dialog = 2;</code>
   * @return Whether the userInfoDialog field is set.
   */
  boolean hasUserInfoDialog();
  /**
   * <pre>
   * 查看用户个人资料对话框
   * </pre>
   *
   * <code>.protobuf.dto.OnlineUserInfoDialogDTO user_info_dialog = 2;</code>
   * @return The userInfoDialog.
   */
  protobuf.dto.OnlineUserInfoDialogDTO getUserInfoDialog();
  /**
   * <pre>
   * 查看用户个人资料对话框
   * </pre>
   *
   * <code>.protobuf.dto.OnlineUserInfoDialogDTO user_info_dialog = 2;</code>
   */
  protobuf.dto.OnlineUserInfoDialogDTOOrBuilder getUserInfoDialogOrBuilder();

  /**
   * <pre>
   * 开始弹奏
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlayStartDTO play_start = 3;</code>
   * @return Whether the playStart field is set.
   */
  boolean hasPlayStart();
  /**
   * <pre>
   * 开始弹奏
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlayStartDTO play_start = 3;</code>
   * @return The playStart.
   */
  protobuf.dto.OnlinePlayStartDTO getPlayStart();
  /**
   * <pre>
   * 开始弹奏
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlayStartDTO play_start = 3;</code>
   */
  protobuf.dto.OnlinePlayStartDTOOrBuilder getPlayStartOrBuilder();

  /**
   * <pre>
   * 用户房间中状态变化
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomUserStatusDTO change_room_user_status = 4;</code>
   * @return Whether the changeRoomUserStatus field is set.
   */
  boolean hasChangeRoomUserStatus();
  /**
   * <pre>
   * 用户房间中状态变化
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomUserStatusDTO change_room_user_status = 4;</code>
   * @return The changeRoomUserStatus.
   */
  protobuf.dto.OnlineChangeRoomUserStatusDTO getChangeRoomUserStatus();
  /**
   * <pre>
   * 用户房间中状态变化
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomUserStatusDTO change_room_user_status = 4;</code>
   */
  protobuf.dto.OnlineChangeRoomUserStatusDTOOrBuilder getChangeRoomUserStatusOrBuilder();

  /**
   * <pre>
   * 弹奏成绩判定
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlayFinishDTO play_finish = 5;</code>
   * @return Whether the playFinish field is set.
   */
  boolean hasPlayFinish();
  /**
   * <pre>
   * 弹奏成绩判定
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlayFinishDTO play_finish = 5;</code>
   * @return The playFinish.
   */
  protobuf.dto.OnlinePlayFinishDTO getPlayFinish();
  /**
   * <pre>
   * 弹奏成绩判定
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlayFinishDTO play_finish = 5;</code>
   */
  protobuf.dto.OnlinePlayFinishDTOOrBuilder getPlayFinishOrBuilder();

  /**
   * <pre>
   * 创建房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineCreateRoomDTO create_room = 6;</code>
   * @return Whether the createRoom field is set.
   */
  boolean hasCreateRoom();
  /**
   * <pre>
   * 创建房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineCreateRoomDTO create_room = 6;</code>
   * @return The createRoom.
   */
  protobuf.dto.OnlineCreateRoomDTO getCreateRoom();
  /**
   * <pre>
   * 创建房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineCreateRoomDTO create_room = 6;</code>
   */
  protobuf.dto.OnlineCreateRoomDTOOrBuilder getCreateRoomOrBuilder();

  /**
   * <pre>
   * 进入房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineEnterRoomDTO enter_room = 7;</code>
   * @return Whether the enterRoom field is set.
   */
  boolean hasEnterRoom();
  /**
   * <pre>
   * 进入房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineEnterRoomDTO enter_room = 7;</code>
   * @return The enterRoom.
   */
  protobuf.dto.OnlineEnterRoomDTO getEnterRoom();
  /**
   * <pre>
   * 进入房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineEnterRoomDTO enter_room = 7;</code>
   */
  protobuf.dto.OnlineEnterRoomDTOOrBuilder getEnterRoomOrBuilder();

  /**
   * <pre>
   * 退出房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineQuitRoomDTO quit_room = 8;</code>
   * @return Whether the quitRoom field is set.
   */
  boolean hasQuitRoom();
  /**
   * <pre>
   * 退出房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineQuitRoomDTO quit_room = 8;</code>
   * @return The quitRoom.
   */
  protobuf.dto.OnlineQuitRoomDTO getQuitRoom();
  /**
   * <pre>
   * 退出房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineQuitRoomDTO quit_room = 8;</code>
   */
  protobuf.dto.OnlineQuitRoomDTOOrBuilder getQuitRoomOrBuilder();

  /**
   * <pre>
   * 被踢出房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineKickedQuitRoomDTO kicked_quit_room = 9;</code>
   * @return Whether the kickedQuitRoom field is set.
   */
  boolean hasKickedQuitRoom();
  /**
   * <pre>
   * 被踢出房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineKickedQuitRoomDTO kicked_quit_room = 9;</code>
   * @return The kickedQuitRoom.
   */
  protobuf.dto.OnlineKickedQuitRoomDTO getKickedQuitRoom();
  /**
   * <pre>
   * 被踢出房间
   * </pre>
   *
   * <code>.protobuf.dto.OnlineKickedQuitRoomDTO kicked_quit_room = 9;</code>
   */
  protobuf.dto.OnlineKickedQuitRoomDTOOrBuilder getKickedQuitRoomOrBuilder();

  /**
   * <pre>
   * 用户登录对战
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoginDTO login = 10;</code>
   * @return Whether the login field is set.
   */
  boolean hasLogin();
  /**
   * <pre>
   * 用户登录对战
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoginDTO login = 10;</code>
   * @return The login.
   */
  protobuf.dto.OnlineLoginDTO getLogin();
  /**
   * <pre>
   * 用户登录对战
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoginDTO login = 10;</code>
   */
  protobuf.dto.OnlineLoginDTOOrBuilder getLoginOrBuilder();

  /**
   * <pre>
   * 大厅聊天
   * </pre>
   *
   * <code>.protobuf.dto.OnlineHallChatDTO hall_chat = 12;</code>
   * @return Whether the hallChat field is set.
   */
  boolean hasHallChat();
  /**
   * <pre>
   * 大厅聊天
   * </pre>
   *
   * <code>.protobuf.dto.OnlineHallChatDTO hall_chat = 12;</code>
   * @return The hallChat.
   */
  protobuf.dto.OnlineHallChatDTO getHallChat();
  /**
   * <pre>
   * 大厅聊天
   * </pre>
   *
   * <code>.protobuf.dto.OnlineHallChatDTO hall_chat = 12;</code>
   */
  protobuf.dto.OnlineHallChatDTOOrBuilder getHallChatOrBuilder();

  /**
   * <pre>
   * 房间聊天
   * </pre>
   *
   * <code>.protobuf.dto.OnlineRoomChatDTO room_chat = 13;</code>
   * @return Whether the roomChat field is set.
   */
  boolean hasRoomChat();
  /**
   * <pre>
   * 房间聊天
   * </pre>
   *
   * <code>.protobuf.dto.OnlineRoomChatDTO room_chat = 13;</code>
   * @return The roomChat.
   */
  protobuf.dto.OnlineRoomChatDTO getRoomChat();
  /**
   * <pre>
   * 房间聊天
   * </pre>
   *
   * <code>.protobuf.dto.OnlineRoomChatDTO room_chat = 13;</code>
   */
  protobuf.dto.OnlineRoomChatDTOOrBuilder getRoomChatOrBuilder();

  /**
   * <pre>
   * 改变房间信息
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomInfoDTO change_room_info = 14;</code>
   * @return Whether the changeRoomInfo field is set.
   */
  boolean hasChangeRoomInfo();
  /**
   * <pre>
   * 改变房间信息
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomInfoDTO change_room_info = 14;</code>
   * @return The changeRoomInfo.
   */
  protobuf.dto.OnlineChangeRoomInfoDTO getChangeRoomInfo();
  /**
   * <pre>
   * 改变房间信息
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomInfoDTO change_room_info = 14;</code>
   */
  protobuf.dto.OnlineChangeRoomInfoDTOOrBuilder getChangeRoomInfoOrBuilder();

  /**
   * <pre>
   * 播放曲谱
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlaySongDTO play_song = 15;</code>
   * @return Whether the playSong field is set.
   */
  boolean hasPlaySong();
  /**
   * <pre>
   * 播放曲谱
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlaySongDTO play_song = 15;</code>
   * @return The playSong.
   */
  protobuf.dto.OnlinePlaySongDTO getPlaySong();
  /**
   * <pre>
   * 播放曲谱
   * </pre>
   *
   * <code>.protobuf.dto.OnlinePlaySongDTO play_song = 15;</code>
   */
  protobuf.dto.OnlinePlaySongDTOOrBuilder getPlaySongOrBuilder();

  /**
   * <pre>
   * 每日挑战
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChallengeDTO challenge = 16;</code>
   * @return Whether the challenge field is set.
   */
  boolean hasChallenge();
  /**
   * <pre>
   * 每日挑战
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChallengeDTO challenge = 16;</code>
   * @return The challenge.
   */
  protobuf.dto.OnlineChallengeDTO getChallenge();
  /**
   * <pre>
   * 每日挑战
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChallengeDTO challenge = 16;</code>
   */
  protobuf.dto.OnlineChallengeDTOOrBuilder getChallengeOrBuilder();

  /**
   * <pre>
   * 家族
   * </pre>
   *
   * <code>.protobuf.dto.OnlineFamilyDTO family = 18;</code>
   * @return Whether the family field is set.
   */
  boolean hasFamily();
  /**
   * <pre>
   * 家族
   * </pre>
   *
   * <code>.protobuf.dto.OnlineFamilyDTO family = 18;</code>
   * @return The family.
   */
  protobuf.dto.OnlineFamilyDTO getFamily();
  /**
   * <pre>
   * 家族
   * </pre>
   *
   * <code>.protobuf.dto.OnlineFamilyDTO family = 18;</code>
   */
  protobuf.dto.OnlineFamilyDTOOrBuilder getFamilyOrBuilder();

  /**
   * <pre>
   * 进入大厅后加载房间列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomListDTO load_room = 19;</code>
   * @return Whether the loadRoom field is set.
   */
  boolean hasLoadRoom();
  /**
   * <pre>
   * 进入大厅后加载房间列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomListDTO load_room = 19;</code>
   * @return The loadRoom.
   */
  protobuf.dto.OnlineLoadRoomListDTO getLoadRoom();
  /**
   * <pre>
   * 进入大厅后加载房间列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomListDTO load_room = 19;</code>
   */
  protobuf.dto.OnlineLoadRoomListDTOOrBuilder getLoadRoomOrBuilder();

  /**
   * <pre>
   * 房间内初次加载所有用户数据
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomPositionDTO load_room_position = 21;</code>
   * @return Whether the loadRoomPosition field is set.
   */
  boolean hasLoadRoomPosition();
  /**
   * <pre>
   * 房间内初次加载所有用户数据
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomPositionDTO load_room_position = 21;</code>
   * @return The loadRoomPosition.
   */
  protobuf.dto.OnlineLoadRoomPositionDTO getLoadRoomPosition();
  /**
   * <pre>
   * 房间内初次加载所有用户数据
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomPositionDTO load_room_position = 21;</code>
   */
  protobuf.dto.OnlineLoadRoomPositionDTOOrBuilder getLoadRoomPositionOrBuilder();

  /**
   * <pre>
   * 房间开始弹奏后加载用户
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadPlayUserDTO load_play_user = 23;</code>
   * @return Whether the loadPlayUser field is set.
   */
  boolean hasLoadPlayUser();
  /**
   * <pre>
   * 房间开始弹奏后加载用户
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadPlayUserDTO load_play_user = 23;</code>
   * @return The loadPlayUser.
   */
  protobuf.dto.OnlineLoadPlayUserDTO getLoadPlayUser();
  /**
   * <pre>
   * 房间开始弹奏后加载用户
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadPlayUserDTO load_play_user = 23;</code>
   */
  protobuf.dto.OnlineLoadPlayUserDTOOrBuilder getLoadPlayUserOrBuilder();

  /**
   * <pre>
   * 弹奏界面左上角成绩条
   * </pre>
   *
   * <code>.protobuf.dto.OnlineMiniGradeDTO mini_grade = 25;</code>
   * @return Whether the miniGrade field is set.
   */
  boolean hasMiniGrade();
  /**
   * <pre>
   * 弹奏界面左上角成绩条
   * </pre>
   *
   * <code>.protobuf.dto.OnlineMiniGradeDTO mini_grade = 25;</code>
   * @return The miniGrade.
   */
  protobuf.dto.OnlineMiniGradeDTO getMiniGrade();
  /**
   * <pre>
   * 弹奏界面左上角成绩条
   * </pre>
   *
   * <code>.protobuf.dto.OnlineMiniGradeDTO mini_grade = 25;</code>
   */
  protobuf.dto.OnlineMiniGradeDTOOrBuilder getMiniGradeOrBuilder();

  /**
   * <pre>
   * 商店
   * </pre>
   *
   * <code>.protobuf.dto.OnlineShopDTO shop = 26;</code>
   * @return Whether the shop field is set.
   */
  boolean hasShop();
  /**
   * <pre>
   * 商店
   * </pre>
   *
   * <code>.protobuf.dto.OnlineShopDTO shop = 26;</code>
   * @return The shop.
   */
  protobuf.dto.OnlineShopDTO getShop();
  /**
   * <pre>
   * 商店
   * </pre>
   *
   * <code>.protobuf.dto.OnlineShopDTO shop = 26;</code>
   */
  protobuf.dto.OnlineShopDTOOrBuilder getShopOrBuilder();

  /**
   * <pre>
   * 主界面加载用户信息及大厅列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserDTO load_user = 28;</code>
   * @return Whether the loadUser field is set.
   */
  boolean hasLoadUser();
  /**
   * <pre>
   * 主界面加载用户信息及大厅列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserDTO load_user = 28;</code>
   * @return The loadUser.
   */
  protobuf.dto.OnlineLoadUserDTO getLoadUser();
  /**
   * <pre>
   * 主界面加载用户信息及大厅列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserDTO load_user = 28;</code>
   */
  protobuf.dto.OnlineLoadUserDTOOrBuilder getLoadUserOrBuilder();

  /**
   * <pre>
   * 进入大厅
   * </pre>
   *
   * <code>.protobuf.dto.OnlineEnterHallDTO enter_hall = 29;</code>
   * @return Whether the enterHall field is set.
   */
  boolean hasEnterHall();
  /**
   * <pre>
   * 进入大厅
   * </pre>
   *
   * <code>.protobuf.dto.OnlineEnterHallDTO enter_hall = 29;</code>
   * @return The enterHall.
   */
  protobuf.dto.OnlineEnterHallDTO getEnterHall();
  /**
   * <pre>
   * 进入大厅
   * </pre>
   *
   * <code>.protobuf.dto.OnlineEnterHallDTO enter_hall = 29;</code>
   */
  protobuf.dto.OnlineEnterHallDTOOrBuilder getEnterHallOrBuilder();

  /**
   * <pre>
   * 退出大厅
   * </pre>
   *
   * <code>.protobuf.dto.OnlineQuitHallDTO quit_hall = 30;</code>
   * @return Whether the quitHall field is set.
   */
  boolean hasQuitHall();
  /**
   * <pre>
   * 退出大厅
   * </pre>
   *
   * <code>.protobuf.dto.OnlineQuitHallDTO quit_hall = 30;</code>
   * @return The quitHall.
   */
  protobuf.dto.OnlineQuitHallDTO getQuitHall();
  /**
   * <pre>
   * 退出大厅
   * </pre>
   *
   * <code>.protobuf.dto.OnlineQuitHallDTO quit_hall = 30;</code>
   */
  protobuf.dto.OnlineQuitHallDTOOrBuilder getQuitHallOrBuilder();

  /**
   * <pre>
   * 增删好友、解除搭档、设置祝语
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSetUserInfoDTO set_user_info = 31;</code>
   * @return Whether the setUserInfo field is set.
   */
  boolean hasSetUserInfo();
  /**
   * <pre>
   * 增删好友、解除搭档、设置祝语
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSetUserInfoDTO set_user_info = 31;</code>
   * @return The setUserInfo.
   */
  protobuf.dto.OnlineSetUserInfoDTO getSetUserInfo();
  /**
   * <pre>
   * 增删好友、解除搭档、设置祝语
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSetUserInfoDTO set_user_info = 31;</code>
   */
  protobuf.dto.OnlineSetUserInfoDTOOrBuilder getSetUserInfoOrBuilder();

  /**
   * <pre>
   * 显示/隐藏弹奏界面左上角成绩条
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSetMiniGradeDTO set_mini_grade = 32;</code>
   * @return Whether the setMiniGrade field is set.
   */
  boolean hasSetMiniGrade();
  /**
   * <pre>
   * 显示/隐藏弹奏界面左上角成绩条
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSetMiniGradeDTO set_mini_grade = 32;</code>
   * @return The setMiniGrade.
   */
  protobuf.dto.OnlineSetMiniGradeDTO getSetMiniGrade();
  /**
   * <pre>
   * 显示/隐藏弹奏界面左上角成绩条
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSetMiniGradeDTO set_mini_grade = 32;</code>
   */
  protobuf.dto.OnlineSetMiniGradeDTOOrBuilder getSetMiniGradeOrBuilder();

  /**
   * <pre>
   * 更换服装
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeClothesDTO change_clothes = 33;</code>
   * @return Whether the changeClothes field is set.
   */
  boolean hasChangeClothes();
  /**
   * <pre>
   * 更换服装
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeClothesDTO change_clothes = 33;</code>
   * @return The changeClothes.
   */
  protobuf.dto.OnlineChangeClothesDTO getChangeClothes();
  /**
   * <pre>
   * 更换服装
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeClothesDTO change_clothes = 33;</code>
   */
  protobuf.dto.OnlineChangeClothesDTOOrBuilder getChangeClothesOrBuilder();

  /**
   * <pre>
   * 查看好友、私信和主界面查看搭档
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserInfoDTO load_user_info = 34;</code>
   * @return Whether the loadUserInfo field is set.
   */
  boolean hasLoadUserInfo();
  /**
   * <pre>
   * 查看好友、私信和主界面查看搭档
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserInfoDTO load_user_info = 34;</code>
   * @return The loadUserInfo.
   */
  protobuf.dto.OnlineLoadUserInfoDTO getLoadUserInfo();
  /**
   * <pre>
   * 查看好友、私信和主界面查看搭档
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserInfoDTO load_user_info = 34;</code>
   */
  protobuf.dto.OnlineLoadUserInfoDTOOrBuilder getLoadUserInfoOrBuilder();

  /**
   * <pre>
   * 发送私信
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSendMailDTO send_mail = 35;</code>
   * @return Whether the sendMail field is set.
   */
  boolean hasSendMail();
  /**
   * <pre>
   * 发送私信
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSendMailDTO send_mail = 35;</code>
   * @return The sendMail.
   */
  protobuf.dto.OnlineSendMailDTO getSendMail();
  /**
   * <pre>
   * 发送私信
   * </pre>
   *
   * <code>.protobuf.dto.OnlineSendMailDTO send_mail = 35;</code>
   */
  protobuf.dto.OnlineSendMailDTOOrBuilder getSendMailOrBuilder();

  /**
   * <pre>
   * 大厅加载用户列表或房间加载邀请列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserListDTO load_user_list = 36;</code>
   * @return Whether the loadUserList field is set.
   */
  boolean hasLoadUserList();
  /**
   * <pre>
   * 大厅加载用户列表或房间加载邀请列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserListDTO load_user_list = 36;</code>
   * @return The loadUserList.
   */
  protobuf.dto.OnlineLoadUserListDTO getLoadUserList();
  /**
   * <pre>
   * 大厅加载用户列表或房间加载邀请列表
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadUserListDTO load_user_list = 36;</code>
   */
  protobuf.dto.OnlineLoadUserListDTOOrBuilder getLoadUserListOrBuilder();

  /**
   * <pre>
   * 找Ta或邀请对话框
   * </pre>
   *
   * <code>.protobuf.dto.OnlineDialogDTO dialog = 37;</code>
   * @return Whether the dialog field is set.
   */
  boolean hasDialog();
  /**
   * <pre>
   * 找Ta或邀请对话框
   * </pre>
   *
   * <code>.protobuf.dto.OnlineDialogDTO dialog = 37;</code>
   * @return The dialog.
   */
  protobuf.dto.OnlineDialogDTO getDialog();
  /**
   * <pre>
   * 找Ta或邀请对话框
   * </pre>
   *
   * <code>.protobuf.dto.OnlineDialogDTO dialog = 37;</code>
   */
  protobuf.dto.OnlineDialogDTOOrBuilder getDialogOrBuilder();

  /**
   * <pre>
   * 每日奖励
   * </pre>
   *
   * <code>.protobuf.dto.OnlineDailyDTO daily = 38;</code>
   * @return Whether the daily field is set.
   */
  boolean hasDaily();
  /**
   * <pre>
   * 每日奖励
   * </pre>
   *
   * <code>.protobuf.dto.OnlineDailyDTO daily = 38;</code>
   * @return The daily.
   */
  protobuf.dto.OnlineDailyDTO getDaily();
  /**
   * <pre>
   * 每日奖励
   * </pre>
   *
   * <code>.protobuf.dto.OnlineDailyDTO daily = 38;</code>
   */
  protobuf.dto.OnlineDailyDTOOrBuilder getDailyOrBuilder();

  /**
   * <pre>
   * 键盘模式传输音符数据
   * </pre>
   *
   * <code>.protobuf.dto.OnlineKeyboardNoteDTO keyboard_note = 39;</code>
   * @return Whether the keyboardNote field is set.
   */
  boolean hasKeyboardNote();
  /**
   * <pre>
   * 键盘模式传输音符数据
   * </pre>
   *
   * <code>.protobuf.dto.OnlineKeyboardNoteDTO keyboard_note = 39;</code>
   * @return The keyboardNote.
   */
  protobuf.dto.OnlineKeyboardNoteDTO getKeyboardNote();
  /**
   * <pre>
   * 键盘模式传输音符数据
   * </pre>
   *
   * <code>.protobuf.dto.OnlineKeyboardNoteDTO keyboard_note = 39;</code>
   */
  protobuf.dto.OnlineKeyboardNoteDTOOrBuilder getKeyboardNoteOrBuilder();

  /**
   * <pre>
   * 等级考试
   * </pre>
   *
   * <code>.protobuf.dto.OnlineClTestDTO cl_test = 40;</code>
   * @return Whether the clTest field is set.
   */
  boolean hasClTest();
  /**
   * <pre>
   * 等级考试
   * </pre>
   *
   * <code>.protobuf.dto.OnlineClTestDTO cl_test = 40;</code>
   * @return The clTest.
   */
  protobuf.dto.OnlineClTestDTO getClTest();
  /**
   * <pre>
   * 等级考试
   * </pre>
   *
   * <code>.protobuf.dto.OnlineClTestDTO cl_test = 40;</code>
   */
  protobuf.dto.OnlineClTestDTOOrBuilder getClTestOrBuilder();

  /**
   * <pre>
   * 心跳检测
   * </pre>
   *
   * <code>.protobuf.dto.OnlineHeartBeatDTO heart_beat = 41;</code>
   * @return Whether the heartBeat field is set.
   */
  boolean hasHeartBeat();
  /**
   * <pre>
   * 心跳检测
   * </pre>
   *
   * <code>.protobuf.dto.OnlineHeartBeatDTO heart_beat = 41;</code>
   * @return The heartBeat.
   */
  protobuf.dto.OnlineHeartBeatDTO getHeartBeat();
  /**
   * <pre>
   * 心跳检测
   * </pre>
   *
   * <code>.protobuf.dto.OnlineHeartBeatDTO heart_beat = 41;</code>
   */
  protobuf.dto.OnlineHeartBeatDTOOrBuilder getHeartBeatOrBuilder();

  /**
   * <pre>
   * 打开/关闭空位
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomDoorDTO change_room_door = 42;</code>
   * @return Whether the changeRoomDoor field is set.
   */
  boolean hasChangeRoomDoor();
  /**
   * <pre>
   * 打开/关闭空位
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomDoorDTO change_room_door = 42;</code>
   * @return The changeRoomDoor.
   */
  protobuf.dto.OnlineChangeRoomDoorDTO getChangeRoomDoor();
  /**
   * <pre>
   * 打开/关闭空位
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomDoorDTO change_room_door = 42;</code>
   */
  protobuf.dto.OnlineChangeRoomDoorDTOOrBuilder getChangeRoomDoorOrBuilder();

  /**
   * <pre>
   * 大厅中显示房间内成员信息
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomUserListDTO load_room_user_list = 43;</code>
   * @return Whether the loadRoomUserList field is set.
   */
  boolean hasLoadRoomUserList();
  /**
   * <pre>
   * 大厅中显示房间内成员信息
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomUserListDTO load_room_user_list = 43;</code>
   * @return The loadRoomUserList.
   */
  protobuf.dto.OnlineLoadRoomUserListDTO getLoadRoomUserList();
  /**
   * <pre>
   * 大厅中显示房间内成员信息
   * </pre>
   *
   * <code>.protobuf.dto.OnlineLoadRoomUserListDTO load_room_user_list = 43;</code>
   */
  protobuf.dto.OnlineLoadRoomUserListDTOOrBuilder getLoadRoomUserListOrBuilder();

  /**
   * <pre>
   * 改变左右手
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomHandDTO change_room_hand = 44;</code>
   * @return Whether the changeRoomHand field is set.
   */
  boolean hasChangeRoomHand();
  /**
   * <pre>
   * 改变左右手
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomHandDTO change_room_hand = 44;</code>
   * @return The changeRoomHand.
   */
  protobuf.dto.OnlineChangeRoomHandDTO getChangeRoomHand();
  /**
   * <pre>
   * 改变左右手
   * </pre>
   *
   * <code>.protobuf.dto.OnlineChangeRoomHandDTO change_room_hand = 44;</code>
   */
  protobuf.dto.OnlineChangeRoomHandDTOOrBuilder getChangeRoomHandOrBuilder();

  /**
   * <pre>
   * 搭档、祝福
   * </pre>
   *
   * <code>.protobuf.dto.OnlineCoupleDTO couple = 45;</code>
   * @return Whether the couple field is set.
   */
  boolean hasCouple();
  /**
   * <pre>
   * 搭档、祝福
   * </pre>
   *
   * <code>.protobuf.dto.OnlineCoupleDTO couple = 45;</code>
   * @return The couple.
   */
  protobuf.dto.OnlineCoupleDTO getCouple();
  /**
   * <pre>
   * 搭档、祝福
   * </pre>
   *
   * <code>.protobuf.dto.OnlineCoupleDTO couple = 45;</code>
   */
  protobuf.dto.OnlineCoupleDTOOrBuilder getCoupleOrBuilder();

  public protobuf.dto.OnlineBaseDTO.RequestCase getRequestCase();
}
