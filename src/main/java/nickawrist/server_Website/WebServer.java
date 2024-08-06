package nickawrist.server_Website;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.getLogger;


public class WebServer {

    private HttpServer server;
    private final String webRoot;
    private Set<String> htmlFiles;

    public WebServer(String webRoot) {
        this.webRoot = webRoot;
        this.htmlFiles = new HashSet<>();
        cacheHtmlFiles();
    }

    private void cacheHtmlFiles(){
        try (Stream<Path> paths = Files.walk(Paths.get(webRoot))) {
            htmlFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".html"))
                    .map(path -> path.getFileName().toString())
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            getLogger().severe("Failed to cache html files: " + e.getMessage());
        }
    }

    public boolean start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/", new FileHandler(webRoot, htmlFiles));
            server.setExecutor(null);
            server.start();
            return true;
        } catch (Exception e) {
            getLogger().severe("Failed to start web server: " + e.getMessage());
            return false;
        }
    }

    public void stop() {
        if(server != null) {
            server.stop(0);
        }
    }

    public void reload() {
        stop();
        start();
    }

    static class FileHandler implements HttpHandler {
        private final String webRoot;
        private final Set<String> htmlFiles;

        public FileHandler(String webRoot, Set<String> htmlFiles) {
            this.webRoot = webRoot;
            this.htmlFiles = htmlFiles;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String uri = exchange.getRequestURI().toString();

            if (uri.equals("/")) {
                uri = "/index.html";
            } else if(!uri.endsWith(".html")){
                String strippedUri = uri.startsWith("/") ? uri.substring(1) : uri;
                if (htmlFiles.contains(strippedUri + ".html")) {
                    uri += ".html";
                }
            }

            Path filePath = Paths.get(webRoot + uri);
            getLogger().info("Request: " + filePath);
            if(Files.exists(filePath)) {
                String mimeType = getMimeType(filePath);


                exchange.getResponseHeaders().set("Content-Type", mimeType);
                byte[] fileBytes = Files.readAllBytes(filePath);
                exchange.sendResponseHeaders(200, fileBytes.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileBytes);
                os.close();
            }else{
                String response = "404 (Not Found)\n";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private String getMimeType(Path filePath) throws IOException {
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                String fileName = filePath.toString();
                String endsWith = fileName.substring(fileName.lastIndexOf("."));

                mimeType = switch (endsWith) {
                    case ".ico" -> "image/x-icon";
                    case ".svg" -> "image/svg+xml";
                    case ".json" -> "application/json";
                    case ".pdf" -> "application/pdf";
                    case ".otf" -> "font/otf";
                    case ".ttf" -> "font/ttf";
                    case ".woff" -> "font/woff";
                    case ".woff2" -> "font/woff2";
                    default -> "application/octet-stream";
                };

            }
            return mimeType;
        }
    }
}
