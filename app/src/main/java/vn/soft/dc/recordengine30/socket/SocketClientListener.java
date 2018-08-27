package vn.soft.dc.recordengine30.socket;

import android.util.Log;

import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

/**
 * Created by Le Duc Chung on 2018-08-27.
 * on project 'recordenginev3'
 */
public class SocketClientListener extends WebSocketListener {
    private static final int NORMAL_CLOSURE_STATUS = 1000;
    private static final String TAG = "chungld.socket";

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        Log.e(TAG, "onOpen: " + response);
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        Log.e(TAG, "onMessage: " + text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        Log.e(TAG, "onMessage: " + bytes);
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        Log.e(TAG, "onClosing: " + code + "; reason: " + reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        Log.e(TAG, "onClosed: " + code + "; reason: " + reason);
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "onFailure: " + response + "; throwable: " + t.toString());
    }
}
