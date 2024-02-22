package com.logycraft.duzzcalll.helper.compatibility;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class CallActionService extends IntentService {

    public static final String ACTION_ANSWER = "com.example.app.ACTION_ANSWER";
    public static final String ACTION_HANGUP = "com.example.app.ACTION_HANGUP";

    public CallActionService() {
        super("CallActionService");
    }

    public static Intent createIntent(Context context, String action) {
        Intent intent = new Intent(context, CallActionService.class);
        intent.setAction(action);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            Log.e("hrlllllllllllll",""+intent.getAction());
//            Toast.makeText(this, ""+intent.getAction(), Toast.LENGTH_SHORT).show();
            int callId = intent.getIntExtra("call_id", -1);
            if (ACTION_ANSWER.equals(action)) {
                Compatibility.getCallAnswerAction(getApplicationContext(), callId);
            } else if (ACTION_HANGUP.equals(action)) {
                Compatibility.getCallDeclineAction(getApplicationContext(), callId);

            }
        }
    }
}