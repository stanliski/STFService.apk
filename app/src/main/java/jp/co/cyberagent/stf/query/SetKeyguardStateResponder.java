package jp.co.cyberagent.stf.query;

import android.app.KeyguardManager;
import android.content.Context;
import android.util.Log;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import jp.co.cyberagent.stf.proto.Wire;

public class SetKeyguardStateResponder extends AbstractResponder {
    private static final String TAG = "STFKeyguardStateResponder";

    private KeyguardManager.KeyguardLock lock;

    public SetKeyguardStateResponder(Context context) {
        super(context);
    }

    @Override
    public GeneratedMessage respond(Wire.RequestEnvelope envelope) throws InvalidProtocolBufferException {
        Wire.SetKeyguardStateRequest request =
                Wire.SetKeyguardStateRequest.parseFrom(envelope.getRequest());

        if (request.getEnabled()) {
            enable();
        }
        else {
            dismiss();
        }

        return Wire.SetKeyguardStateResponse.newBuilder()
                .setSuccess(true)
                .build();
    }

    @Override
    public void cleanup() {
        enable();
    }

    @SuppressWarnings("deprecation")
    private void dismiss() {
        Log.i(TAG, "Unlocking device");
        if (lock == null) {
            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            lock = km.newKeyguardLock("STFService");
        }
        lock.reenableKeyguard();
        lock.disableKeyguard();
    }

    private void enable() {
        Log.i(TAG, "Locking device");
        if (lock != null) {
            lock.reenableKeyguard();
        }
    }
}
