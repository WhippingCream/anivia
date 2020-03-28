package co.locodo.anivia;

public class Constants {
    // 10.0.2.2 은 안드로이드 에뮬레이터에서 로컬호스트로 접속하기 위한 host
    public static final String SOCKET_URL = "http://10.0.2.2:3000/";

    public static final String EVENT_SERVER_CONNECTED = "connected";
    public static final String EVENT_KAKAO_MESSAGE = "kakao_message";
    public static final String EVENT_SERVER_MESSAGE = "server_message";

    public static final String SEND_DATA_ID = "message_id";
    public static final String SEND_DATA_ROOM = "room";
    public static final String SEND_DATA_SENDER_NAME = "sender_name";
    public static final String SEND_DATA_MESSAGE = "message";
    public static final String SEND_DATA_IS_GROUP_CHAT = "is_group_chat";

    public static final String RECV_DATA_SERVER_MESSAGE = "server_message";
}
