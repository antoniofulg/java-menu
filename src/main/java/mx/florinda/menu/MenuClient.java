package mx.florinda.menu;

import java.io.*;
import java.net.*;
import java.net.http.*;

public class MenuClient {

	public static void main(String[] args) throws IOException, InterruptedException {
		URI uri = URI.create("http://localhost:8000/menu");

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
