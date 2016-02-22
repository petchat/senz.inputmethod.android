package demo.ime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.inputmethodservice.InputMethodService;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.widget.Button;

import com.wilddog.client.DataSnapshot;
import com.wilddog.client.ValueEventListener;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import demo.R;


public class ImeService extends InputMethodService {

    private Wilddog ref_info;
    private Wilddog ref_send;
    private Button bt;

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String result = intent.getStringExtra(Const.ACTION_URL_KEY);
            Log.d("lk", "onReceive:" + result);
            bt.setText("重新连接");

            ref_info = new Wilddog(Const.WILDDOG_BASE_URL + result + "/info");
            ref_info.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String text = (String) dataSnapshot.getValue();
                    Log.d("lk", "onDataChange:" + text);
                    input(text);
                }

                @Override
                public void onCancelled(WilddogError wilddogError) {
                }
            });

            ref_send = new Wilddog(Const.WILDDOG_BASE_URL + result + "/send");
            ref_send.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Object value = dataSnapshot.getValue();
                    if (value != null) {
                        boolean send = (boolean) value;
                        if (send) {
                            Log.d("lk", "onDataChange:send");
                            keyDownUp(KeyEvent.KEYCODE_ENTER);
                            ref_send.setValue(false);
                        }
                    }
                }

                @Override
                public void onCancelled(WilddogError wilddogError) {
                }
            });
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化
        Wilddog.setAndroidContext(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Const.ACTION_URL_NAME);
        registerReceiver(receiver, filter);
    }

    @Override
    public View onCreateInputView() {
        View v = LayoutInflater.from(this).inflate(R.layout.input, null);
        bt = (Button) v.findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("lk", "click");
                Intent intent = new Intent(ImeService.this, MidActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    public void input(String text) {
        InputConnection inputConn = getCurrentInputConnection();
        if (text != null) {
            inputConn.deleteSurroundingText(100, 100);
            inputConn.commitText(text, text.length());
        }
        //inputConn.performEditorAction(EditorInfo.IME_ACTION_DONE);
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

}
