package mx.florinda.menu;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

public class MenuServer {

	public static void main(String[] args) throws IOException {
		HttpServer httpServer = HttpServer.create(new InetSocketAddress(8000), 0);

		httpServer.createContext("/menu", exchange -> {
			String json = Files.readString(Path.of("menu.json"));
			byte[] bytes = json.getBytes();

			exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

			exchange.sendResponseHeaders(200, bytes.length);

			OutputStream responseBody = exchange.getResponseBody();
			responseBody.write(bytes);
			responseBody.close();
		});

		System.out.println("Server is running on port 8000");
		System.out.println("Press Ctrl+C to stop");

		httpServer.start();
	}

}
