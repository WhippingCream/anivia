package co.locodo.anivia;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;

import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketService extends IntentService
{
    public enum BusEventType {
        StopService,
    };

    private static final String TAG = "SocketService";

    private HashMap<Integer, Notification.Action> notiMap = new HashMap<>();

    private Socket mSocket;

    public SocketService() {
        super("SocketService");
    }

    public SocketService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BusProvider.getInstance().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        setupSocketClient();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mSocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        mSocket.off(Socket.EVENT_CONNECT, onConnect);
        mSocket.on(Constants.EVENT_SERVER_MESSAGE, onMessageReceived);
        mSocket.disconnect();

        BusProvider.getInstance().unregister(this);
    }

    private void setupSocketClient() {
        try {
            mSocket = IO.socket(Constants.SOCKET_URL);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
            mSocket.on(Constants.EVENT_SERVER_MESSAGE, onMessageReceived);

            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onConnected");
            mSocket.emit(Constants.EVENT_SERVER_CONNECTED);
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "onDisconnected");
        }
    };

    /**
     * Message 전달 Listener
     */
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject rcvData = (JSONObject) args[0];
            String serverMessage = rcvData.optString(Constants.RECV_DATA_SERVER_MESSAGE);
            int id = rcvData.optInt(Constants.SEND_DATA_ID);

            if (notiMap.containsKey(id))
                reply(notiMap.get(id), serverMessage);
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        public void call(Object... args) {
            Log.d(TAG, "error " + args[0].toString());
        }
    };

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent");
    }

    @Subscribe
    public void onBusEvent(BusEventType eventType) {
        switch (eventType)
        {
            case StopService:
                stopSelf();
                break;

            default:
                break;
        }
    }

    @Subscribe
    public void onKakaoMessageEvent(KakaoMessageEvent messageEvent) {
        JSONObject sendData = new JSONObject();
        try {
            sendData.put(Constants.SEND_DATA_ID, messageEvent.GetID());
            sendData.put(Constants.SEND_DATA_ROOM, messageEvent.room);
            sendData.put(Constants.SEND_DATA_MESSAGE, messageEvent.message);
            sendData.put(Constants.SEND_DATA_SENDER_NAME, messageEvent.senderName);
            sendData.put(Constants.SEND_DATA_IS_GROUP_CHAT, messageEvent.isGroupChat);
            mSocket.emit(Constants.EVENT_KAKAO_MESSAGE, sendData);

            notiMap.put(messageEvent.GetID(), messageEvent.notiAction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void reply(Notification.Action session, String value){
        Intent sendIntent = new Intent();
        Bundle msg = new Bundle();
        for (RemoteInput inputable : session.getRemoteInputs())
            msg.putCharSequence(inputable.getResultKey(), value);
        RemoteInput.addResultsToIntent(session.getRemoteInputs(), sendIntent, msg);

        try {
            session.actionIntent.send(this, 0, sendIntent);
        } catch (PendingIntent.CanceledException e) {
        }
    }
}
