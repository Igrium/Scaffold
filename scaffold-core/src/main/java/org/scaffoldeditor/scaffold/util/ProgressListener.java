package org.scaffoldeditor.scaffold.util;

/**
 * Interface used to allow operations to inform the UI about their progress.
 */
public interface ProgressListener {

    /**
     * Called when the operation has made progress.
     * 
     * @param percent A value from 0-1 indicating the percentage of the operation
     *                that has been completed.
     * @param stage   A string allowing the operation to specify details to the
     *                user. Typically rendered under the progress bar.
     */
    void progress(float percent, String stage);

    /**
     * Called when a non-fatal error occurs in the operation (as supposed to a fatal
     * one, where it throws).
     * 
     * @param e             The error.
     * @param detailMessage An optional message detailing where in the operation it
     *                      occured. (may be <code>null</code>)
     */
    default void error(Throwable e, String detailMessage) {};

    /**
     * A dummy progress listener.
     */
    public static ProgressListener DUMMY = (percent, stage) -> {};
}
