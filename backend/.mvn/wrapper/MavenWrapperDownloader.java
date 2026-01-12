import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class MavenWrapperDownloader {
    private static final String WRAPPER_VERSION = "3.2.0";
    private static final String DEFAULT_DOWNLOAD_URL =
            "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/" + WRAPPER_VERSION +
                    "/maven-wrapper-" + WRAPPER_VERSION + ".jar";

    public static void main(String[] args) {
        try {
            File baseDirectory = new File(args.length > 0 ? args[0] : ".");
            File wrapperJar = new File(baseDirectory, ".mvn/wrapper/maven-wrapper.jar");
            if (wrapperJar.exists()) {
                System.out.println("maven-wrapper.jar already exists.");
                return;
            }
            wrapperJar.getParentFile().mkdirs();
            System.out.println("Downloading " + DEFAULT_DOWNLOAD_URL + " to " + wrapperJar.getAbsolutePath());

            downloadFile(DEFAULT_DOWNLOAD_URL, wrapperJar);

            System.out.println("Done.");
        } catch (Exception e) {
            System.err.println("Failed to download Maven Wrapper jar.");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void downloadFile(String url, File destination) throws IOException {
        URL website = new URL(url);
        try (InputStream in = website.openStream();
             ReadableByteChannel rbc = Channels.newChannel(in);
             FileOutputStream fos = new FileOutputStream(destination)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}
