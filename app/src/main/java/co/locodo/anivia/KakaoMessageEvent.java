package co.locodo.anivia;

import android.app.Notification;

public class KakaoMessageEvent
{
    String room;
    String message;
    String senderName;
    boolean isGroupChat;
    Notification.Action notiAction;

    public KakaoMessageEvent(String room, String message, String senderName, boolean isGroupChat, Notification.Action notiAction)
    {
        this.room = room;
        this.message = message;
        this.senderName = senderName;
        this.isGroupChat = isGroupChat;
        this.notiAction = notiAction;
    }

    public int GetID()
    {
        return notiAction.hashCode();
    }
}