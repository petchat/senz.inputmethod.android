package demo.ime;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import demo.qrcodescan.MipcaActivityCapture;

public class MidActivity extends Activity {

    private final static int SCANNIN_GREQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent();
        intent.setClass(MidActivity.this, MipcaActivityCapture.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    String result = bundle.getString("result");
                    sendBroadCast(result);
                    finish();
                }else{
                    finish();
                }
                break;
        }
    }

    private void sendBroadCast(String result) {
        Intent intent = new Intent();
        intent.setAction(Const.ACTION_URL_NAME);
        intent.putExtra(Const.ACTION_URL_KEY, result);
        sendBroadcast(intent);
    }
}
