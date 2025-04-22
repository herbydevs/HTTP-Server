import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        int port = 8080; // listening port

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                // Handle the client connection in a separate thread
                new Thread(() -> handleClient(clientSocket)).start();
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

            // Read the request line (e.g., "GET / HTTP/1.1")
            String requestLine = in.readLine();
            System.out.println("Request: " + requestLine);

            // Check if the request is for the root path
            if (requestLine != null && requestLine.startsWith("GET / ")) {
                File htmlFile = new File("../assets/index.html");
                if (!htmlFile.exists()) {
                    // File doesn't exist, send 404 response
                    out.println("HTTP/1.1 404 Not Found");
                    out.println("Content-Type: text/html");
                    out.println();
                    out.println("<html><body><h1>404 Not Found</h1></body></html>");
                } else {
                    // File exists, send 200 OK response with file content
                    BufferedReader fileReader = new BufferedReader(new FileReader(htmlFile));
                    StringBuilder fileContent = new StringBuilder();
                    String line;
                    while ((line = fileReader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }
                    fileReader.close();

                    out.println("HTTP/1.1 200 OK");
                    out.println("Content-Type: text/html");
                    out.println("Content-Length: " + fileContent.length());
                    out.println();  // Blank line before the content
                    out.println(fileContent.toString());
                }
            } else {
                // Handle other requests or send a 400 Bad Request response
                out.println("HTTP/1.1 400 Bad Request");
                out.println("Content-Type: text/html");
                out.println();
                out.println("<html><body><h1>400 Bad Request</h1></body></html>");
            }
        } catch (IOException ex) {
            System.out.println("Client handling exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}