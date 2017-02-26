package com.gps.itunes.media.player.updater;

/**
 * Created by leogps on 2/26/17.
 */
public class UpdateResult {

    private boolean updated;
    private Reason reason;

    public enum Reason {

        METADATA_COULD_NOT_BE_LOADED("Release metadata could not be loaded."),
        UPDATE_NOT_AVAILABLE("No Update available."),
        UPDATE_SUCCESS("Updated successfully."),
        UPDATE_FAILED_UNKNOWN("Update failed for unknown reasons."),
        EXCEPTION_OCCURRED("An exception occurred.");

        private final String reason;

        Reason(String reason) {
            this.reason = reason;
        }

        public String getReason() {
            return reason;
        }
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }
}
