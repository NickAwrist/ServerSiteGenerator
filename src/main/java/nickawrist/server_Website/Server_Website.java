package nickawrist.server_Website;

import nickawrist.server_Website.commands.ServerWebReload;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Server_Website extends JavaPlugin {

    private WebServer webServer;

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("serverweb").setExecutor(new ServerWebReload(this));

        this.saveDefaultConfig();
        getLogger().info("Enabling Server_Website...");

        createWebFiles();
        if(startWebServer()){
            getLogger().info("Server_Website enabled!");
        }else{
            getLogger().severe("Failed to enable Server_Website!");
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Disabling Server_Website...");
        webServer.stop();
        getLogger().info("Server_Website disabled!");
    }

    private void createWebFiles() {
        Path webDir = getDataFolder().toPath().resolve("web");
        if(!Files.exists(webDir)) {
            try {
                // Create web directory
                Files.createDirectories(webDir);
                copyResource("web/index.html", webDir.resolve("index.html"));
                copyResource("web/about.html", webDir.resolve("about.html"));
                copyResource("web/styles.css", webDir.resolve("styles.css"));
                copyResource("web/script.js", webDir.resolve("script.js"));

                // Create fonts directory
                Path fontsDir = webDir.resolve("fonts");
                Files.createDirectories(fontsDir);

                // Copy images directory
                Path imagesDir = webDir.resolve("imgs");
                Files.createDirectories(imagesDir);
                copyResource("web/imgs/favicon.jpg", imagesDir.resolve("favicon.jpg"));
                copyResource("web/imgs/logo.png", imagesDir.resolve("logo.png"));

            } catch (Exception e) {
                getLogger().severe("Failed to create web directory: " + e.getMessage());
            }
        }
    }

    private void copyResource(String resource, Path target) {
        try (InputStream in = getResource(resource)) {
            assert in != null;
            try (BufferedInputStream bin = new BufferedInputStream(in);
                 OutputStream out = Files.newOutputStream(target);
                 BufferedOutputStream bout = new BufferedOutputStream(out)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bin.read(buffer)) != -1) {
                    bout.write(buffer, 0, bytesRead);
                }
            }
        } catch (IOException e) {
            getLogger().severe("Failed to copy resource " + resource + ": " + e.getMessage());
        }
    }



    public boolean startWebServer() {
        webServer = new WebServer(getDataFolder().toPath().resolve("web").toString());
        return webServer.start();
    }

    public void stopWebServer() {
        if (webServer != null) {
            webServer.stop();
        }
    }

    public void reloadWebServer() {
        stopWebServer();
        startWebServer();
    }
}
