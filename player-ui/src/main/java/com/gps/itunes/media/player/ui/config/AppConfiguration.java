package com.gps.itunes.media.player.ui.config;

import com.gps.itunes.lib.parser.utils.OSInfo;
import com.gps.itunes.lib.parser.utils.PropertyManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

/**
 * Created by leogps on 12/26/16.
 */
public class AppConfiguration {

    private static Logger LOG = Logger.getLogger(AppConfiguration.class);

    public void configure() {
        LOG.debug("Configuring...");

        Properties props = new Properties();
        try {

            props.load(AppConfiguration.class.getResourceAsStream("/application.properties"));
            initDebianConfiguration(props);

        } catch (IOException e) {
            LOG.error("Failed to load application properties.", e);
        }
    }

    private void initDebianConfiguration(Properties props) {
        if(!OSInfo.isOSWin() && !OSInfo.isOSMac() && props.get("debian.config.location") != null) {
            String debianConfigLocation = (String) props.get("debian.config.location");
            File debianConfigurationFile = new File(debianConfigLocation);
            if(debianConfigurationFile.exists()) {
                loadConfigurations(debianConfigurationFile);
            }
        }
    }

    private void loadConfigurations(File configuration) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(configuration));
            Enumeration enumeration = properties.propertyNames();
            while(enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                PropertyManager.getConfigurationMap().put(key, (String) properties.get(key));
            }
            LOG.debug("Finished loading properties from location: " + configuration.getAbsolutePath());
        } catch (IOException e) {
            LOG.error("Failed to load configuration from: " + configuration.getAbsolutePath(), e);
        }
    }
}
