package co.locodo.anivia;

import com.squareup.otto.Bus;

public final class BusProvider {
    private static final MainThreadBus BUS = new MainThreadBus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {
    }
}
