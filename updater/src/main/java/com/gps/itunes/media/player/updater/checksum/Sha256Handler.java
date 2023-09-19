package com.gps.itunes.media.player.updater.checksum;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256Handler implements ChecksumHandler {

    private static final String NAME = "SHA2-256SUMS";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean canHandle(String checksumName) {
        return StringUtils.startsWithIgnoreCase(checksumName, NAME);
    }

    @Override
    public String calculateChecksum(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return FileChecksumCalculator.calculateFileChecksum(digest, file);
    }
}
