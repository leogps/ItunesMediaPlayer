package com.gps.itunes.media.player.vlcj.discovery;

import uk.co.caprica.vlcj.binding.support.runtime.RuntimeUtil;
import uk.co.caprica.vlcj.factory.discovery.strategy.LinuxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.NativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.OsxNativeDiscoveryStrategy;
import uk.co.caprica.vlcj.factory.discovery.strategy.WindowsNativeDiscoveryStrategy;

/**
 * Strategy Resolver to resolve strategy at runtime.
 */
public class NativeDiscoveryStrategyResolver {

    public static NativeDiscoveryStrategy resolve() {
        if (RuntimeUtil.isMac()) {
            return new OsxNativeDiscoveryStrategy();
        } else if (RuntimeUtil.isNix()) {
            return new LinuxNativeDiscoveryStrategy();
        }
        return new WindowsNativeDiscoveryStrategy();
    }
}
