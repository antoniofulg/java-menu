package mx.florinda.menu;

import java.io.PrintStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class SocketMenuClient {
	final static int PORT = 8000;

	public static void main(String[] args) throws Exception {
		try (Socket socket = new Socket("localhost", PORT)) {
			OutputStream clientOutputStream = socket.getOutputStream();
			PrintStream printStream = new PrintStream(clientOutputStream);

			printStream.println("GET /menu HTTP/1.1");
			printStream.println();

			InputStream clientInputStream = socket.getInputStream();
			try (Scanner scanner = new Scanner(clientInputStream)) {
				while (scanner.hasNextLine()) {
					System.out.println(scanner.nextLine());
				}
			}
		}
	}
}
