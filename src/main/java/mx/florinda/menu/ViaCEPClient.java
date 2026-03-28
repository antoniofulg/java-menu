package mx.florinda.menu;

import java.io.*;
import java.net.*;
import java.net.http.*;

public class ViaCEPClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		// URL url = URI.create("https://viacep.com.br/ws/01001000/json/").toURL();

		// try (Scanner scanner = new Scanner(url.openStream())) {
		// while (scanner.hasNextLine()) {
		// System.out.println(scanner.nextLine());
		// }
		// }

		URI uri = URI.create("https://viacep.com.br/ws/63107040/json/");

		try (HttpClient httpClient = HttpClient.newHttpClient()) {
			HttpRequest httpRequest = HttpRequest.newBuilder(uri).build();
			HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			int statusCode = httpResponse.statusCode();
			String body = httpResponse.body();
			System.out.println(statusCode);
			System.out.println(body);
		}

	}

}
