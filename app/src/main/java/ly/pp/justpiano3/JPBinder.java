package ly.pp.justpiano3;

import android.os.Binder;

final class JPBinder extends Binder {
    private final ConnectionService connectionService;

    JPBinder(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    final ConnectionService getConnectionService() {
        return connectionService;
    }
}
