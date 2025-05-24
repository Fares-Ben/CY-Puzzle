package Factory;

@FunctionalInterface
public interface ProgressListener {
    void onProgress(double progress);
}
