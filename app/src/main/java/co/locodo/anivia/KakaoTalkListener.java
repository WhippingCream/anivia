package co.locodo.anivia;

import android.app.Notification;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.widget.Toast;

public class KakaoTalkListener extends NotificationListenerService {
    @Override
    public void onCreate(){
        super.onCreate();
        Toast.makeText(this, "카카오톡 봇이 알림에 접근하기 시작합니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Toast.makeText(this, "카카오톡 봇이 알림에 접근하는 것이 정지되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onListenerConnected() {
        Toast.makeText(this, "카카오톡 리스너가 연결되었습니다.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        if (!loadSetting("botOn"))
            return;

        if (!sbn.getPackageName().equals("com.kakao.talk"))
            return;

        try {
            Notification.WearableExtender wExt = new Notification.WearableExtender(sbn.getNotification());
            for (Notification.Action act : wExt.getActions()) {
                if (act.getRemoteInputs() == null || act.getRemoteInputs().length == 0)
                    continue;

                Bundle data = sbn.getNotification().extras;
                String room, sender, msg;
                boolean isGroupChat = data.get("android.text") instanceof SpannableString;
                if (Build.VERSION.SDK_INT > 23) {
                    room = data.getString("android.summaryText");
                    isGroupChat = room != null;
                    sender = data.get("android.title").toString();
                    msg = data.get("android.text").toString();
                } else {
                    room = data.getString("android.title");
                    if (isGroupChat) {
                        String html = Html.toHtml((Spanned) data.get("android.text"));
                        sender = Html.fromHtml(html.split("<b>")[1].split("</b>")[0]).toString();
                        msg = Html.fromHtml(html.split("</b>")[1].split("</p>")[0].substring(1)).toString();
                    }
                    else {
                        sender = room;
                        room = "";
                        msg = data.get("android.text").toString();
                    }
                }

                BusProvider.getInstance().post(new KakaoMessageEvent(room, msg, sender, isGroupChat, act));
            }
        } catch(Exception e) {
            toast(e.toString()+"\nAt:"+e.getStackTrace()[0].getLineNumber());
        }
    }

    private boolean loadSetting(String setting){
        String cache = KakaoBot.readData(setting);
        if (cache == null)
            return false;
        else
            return cache.equals("true");
    }

    private void toast(String value){
        Intent intent = new Intent(this, ToastService.class);
        intent.putExtra("toast", value);
        startService(intent);
    }
}
