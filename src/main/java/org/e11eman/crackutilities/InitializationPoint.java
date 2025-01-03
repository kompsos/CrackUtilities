package org.e11eman.crackutilities;

import net.fabricmc.api.ModInitializer;
import org.e11eman.crackutilities.utilities.CClient;

public class InitializationPoint implements ModInitializer {
    @Override
    public void onInitialize() {
        CClient.initSystems();
        CClient.registerExtraEvents();
    }
}
