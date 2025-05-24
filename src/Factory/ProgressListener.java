/**
 * ProgressListener is an interface to track progress updates during puzzle resolution.
 */
package Factory;

/**
 * Interface used to monitor the progress of puzzle generation or solving.
 */
@FunctionalInterface
public interface ProgressListener {
    /**
     * Called to indicate the current progress between 0.0 and 1.0.
     *
     * @param progress the current progress value
     */
    void onProgress(double progress);
}
