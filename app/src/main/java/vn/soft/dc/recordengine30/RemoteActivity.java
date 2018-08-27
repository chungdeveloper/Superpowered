package vn.soft.dc.recordengine30;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okio.ByteString;
import vn.soft.dc.recordengine30.socket.SocketClientListener;
import vn.soft.dc.recordengine30.socket.model.SocketSender;

public class RemoteActivity extends AppCompatActivity {

    @BindView(R.id.edUserID)
    EditText edUserID;
    @BindView(R.id.btnLogin)
    TextView btnLogin;
    @BindView(R.id.edChannel)
    EditText edChannel;
    @BindView(R.id.btnChannel)
    TextView btnChannel;
    @BindView(R.id.edSeeker)
    EditText edSeeker;
    @BindView(R.id.btnSeeker)
    TextView btnSeeker;
    @BindView(R.id.tvLog)
    TextView tvLog;
    @BindView(R.id.btnStart)
    TextView btnStart;
    @BindView(R.id.llControl)
    LinearLayout llControl;

    private OkHttpClient mClient;
    private WebSocket ws;
    private long timeConnected;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        setStartVisible(true);
    }

    private void initSocket() {
        mClient = new OkHttpClient();
        gson = new Gson();
        Request request = new Request.Builder().url("ws://172.104.60.5:9502/").build();
        ws = mClient.newWebSocket(request, listener);
        timeConnected = System.currentTimeMillis();
        mClient.dispatcher().executorService().shutdown();
    }

    public static void start(AppCompatActivity activity) {
        Intent intent = new Intent(activity, RemoteActivity.class);
        activity.startActivity(intent);
    }

    @OnClick({R.id.btnLogin, R.id.btnChannel, R.id.btnSeeker, R.id.btnStart})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                doLogin(edUserID.getText().toString().trim());
                break;
            case R.id.btnChannel:
                doJoinChannel(edChannel.getText().toString().trim());
                break;
            case R.id.btnSeeker:
                doSeek(edSeeker.getText().toString().trim(), edChannel.getText().toString().trim());
                break;
            case R.id.btnStart:
                initSocket();
                break;
        }
    }

    private void doSeek(String seeker, String channel) {
        if (seeker.equalsIgnoreCase("") || channel.equalsIgnoreCase("")) return;
        SocketSender sender = new SocketSender(SocketSender.SEEK);
        sender.setChannel(channel);
        sender.setMessage(seeker);
        send(sender);
    }

    private void doJoinChannel(String trim) {
        if (trim.equalsIgnoreCase("")) return;
        SocketSender sender = new SocketSender(SocketSender.JOIN);
        sender.setChannel(trim);
        send(sender);
    }

    private void send(SocketSender sender) {
        ws.send(gson.toJson(sender));
    }


    private void doLogin(String trim) {
        if (trim.equalsIgnoreCase("")) return;
        SocketSender sender = new SocketSender(SocketSender.LOGIN);
        sender.setId(trim);
        send(sender);
    }

    private void output(final String txt) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvLog.setText(String.format("%s\n\n%s", tvLog.getText().toString(), txt));
            }
        });
    }

    private SocketClientListener listener = new SocketClientListener() {
        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            super.onOpen(webSocket, response);
            output("onOpen: " + response.toString() + "\nPing: " + (System.currentTimeMillis() - timeConnected) + "ms");
            setStartVisible(false);
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            super.onMessage(webSocket, text);
            output("onMessage: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            super.onMessage(webSocket, bytes);
            output(bytes.toString());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) {
            super.onClosing(webSocket, code, reason);
            output("Closing = Code: " + code + "; reason: " + reason);
        }

        @Override
        public void onClosed(WebSocket webSocket, int code, String reason) {
            super.onClosed(webSocket, code, reason);
            output("onClosed = Code: " + code + "; reason: " + reason);
            setStartVisible(true);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            output("onClosed = Throwable: " + t.toString() + "; Response: " + response);
        }
    };

    private void setStartVisible(final boolean b) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                llControl.setVisibility(b ? View.GONE : View.VISIBLE);
                btnStart.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });
    }

}
