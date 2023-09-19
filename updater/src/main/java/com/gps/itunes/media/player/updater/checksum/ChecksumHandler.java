package com.gps.itunes.media.player.updater.checksum;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface ChecksumHandler {

    String getName();

    boolean canHandle(String checksumName);

    String calculateChecksum(File file) throws NoSuchAlgorithmException, IOException;
}
