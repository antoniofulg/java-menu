package mx.florinda.menu;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketMenuServer {
	final static int PORT = 8000;

	public static void main(String[] args) throws Exception {

		try (ExecutorService executorService = Executors.newFixedThreadPool(50)) {

			try (ServerSocket serverSocket = new ServerSocket(PORT)) {
				System.out.println("Server is running on port " + PORT);
				System.out.println("Press Ctrl+C to stop");

				while (true) {
					Socket clientSocket = serverSocket.accept();

					executorService.execute(() -> requestHandler(clientSocket));
				}
			}
		}
	}

	private static void requestHandler(Socket clientSocket) {
		try (clientSocket) {
			InputStream clientInputStream = clientSocket.getInputStream();

			StringBuilder requestBuilder = new StringBuilder();

			int data;
			do {
				data = clientInputStream.read();
				requestBuilder.append((char) data);
			} while (clientInputStream.available() > 0);

			String request = requestBuilder.toString();
			System.out.println(request);

			Thread.sleep(250);

			Path path = Path.of("menu.json");
			String json = Files.readString(path);

			OutputStream clientOutputStream = clientSocket.getOutputStream();
			PrintStream printStream = new PrintStream(clientOutputStream);

			printStream.println("HTTP/1.1 200 OK");
			printStream.println("Content-Type: application/json; charset=UTF-8");
			printStream.println();
			printStream.println(json);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
