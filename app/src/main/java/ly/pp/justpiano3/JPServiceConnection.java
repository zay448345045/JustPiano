package ly.pp.justpiano3;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

final class JPServiceConnection implements ServiceConnection {
    private final JPApplication jpapplication;

    JPServiceConnection(JPApplication jPApplication) {
        jpapplication = jPApplication;
    }

    @Override
    public final void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        jpapplication.setConnectionService(((JPBinder) iBinder).getConnectionService());
    }

    @Override
    public final void onServiceDisconnected(ComponentName componentName) {
        jpapplication.setConnectionService(null);
        Toast.makeText(jpapplication, "Service  Failed.", Toast.LENGTH_LONG).show();
    }
}
