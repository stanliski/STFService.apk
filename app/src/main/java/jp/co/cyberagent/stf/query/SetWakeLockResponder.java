package jp.co.cyberagent.stf.query;

import android.content.Context;
import android.os.PowerManager;
import android.util.Log;

import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;

import jp.co.cyberagent.stf.proto.Wire;

public class SetWakeLockResponder extends AbstractResponder {
    private static final String TAG = "STFSetWakeLockResponder";

    private PowerManager.WakeLock wakeLock;

    public SetWakeLockResponder(Context context) {
        super(context);
    }

    @Override
    public GeneratedMessage respond(Wire.RequestEnvelope envelope) throws InvalidProtocolBufferException {
        Wire.SetWakeLockRequest request =
                Wire.SetWakeLockRequest.parseFrom(envelope.getRequest());

        if (request.getEnabled()) {
            acquireWakeLock();
        }
        else {
            releaseWakeLock();
        }

        return Wire.SetWakeLockResponse.newBuilder()
                .setSuccess(true)
                .build();
    }

    @Override
    public void cleanup() {
        releaseWakeLock();
    }

    @SuppressWarnings("deprecation")
    private void acquireWakeLock() {
        releaseWakeLock();
        Log.i(TAG, "Acquiring wake lock");
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP,
                "STFService"
        );
        wakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (wakeLock != null) {
            Log.i(TAG, "Releasing wake lock");
            wakeLock.release();
            wakeLock = null;
        }
    }
}
