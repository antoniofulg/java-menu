package mx.florinda.menu;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

public class SocketMenuServer {
	private static final Path MENU_JSON_PATH = Path.of("menu.json");
	private static final Object MENU_FILE_LOCK = new Object();
	private static final Gson GSON_PRETTY = new GsonBuilder().setPrettyPrinting().create();

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
			String rawRequest = readHttpRequest(clientSocket.getInputStream());
			System.out.println(rawRequest);

			Thread.sleep(250);

			String method = parseRequestMethod(rawRequest);
			String path = parseRequestPath(rawRequest);
			String body = parseRequestBody(rawRequest);
			OutputStream out = clientSocket.getOutputStream();

			if ("POST".equalsIgnoreCase(method) && "/menu".equals(path)) {
				try {
					String created = handlePostMenu(body);
					writeJsonResponse(out, 201, "Created", created);
				} catch (IllegalArgumentException e) {
					writeJsonResponse(out, 400, "Bad Request", jsonError(e.getMessage()));
				} catch (JsonSyntaxException e) {
					writeJsonResponse(out, 400, "Bad Request", jsonError("Invalid JSON"));
				}
				return;
			}

			if (!"GET".equalsIgnoreCase(method)) {
				writeJsonResponse(out, 405, "Method Not Allowed", jsonError("Method not allowed"));
				return;
			}

			String menuJson = Files.readString(MENU_JSON_PATH);
			String responseBody = responseForGetPath(path, menuJson);
			writeJsonResponse(out, 200, "OK", responseBody);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String readHttpRequest(InputStream clientInputStream) throws IOException {
		StringBuilder requestBuilder = new StringBuilder();
		int data;
		do {
			data = clientInputStream.read();
			requestBuilder.append((char) data);
		} while (clientInputStream.available() > 0);
		return requestBuilder.toString();
	}

	private static String parseRequestMethod(String request) {
		String firstLine = firstLine(request);
		String[] parts = firstLine.split("\\s+");
		return parts.length >= 1 ? parts[0].trim().toUpperCase() : "GET";
	}

	private static String parseRequestPath(String request) {
		String firstLine = firstLine(request);
		String[] parts = firstLine.split("\\s+");
		if (parts.length >= 2) {
			String raw = parts[1];
			int q = raw.indexOf('?');
			return q >= 0 ? raw.substring(0, q) : raw;
		}
		return "/";
	}

	private static String parseRequestBody(String request) {
		int idx = request.indexOf("\r\n\r\n");
		if (idx >= 0) {
			return request.substring(idx + 4);
		}
		idx = request.indexOf("\n\n");
		if (idx >= 0) {
			return request.substring(idx + 2);
		}
		return "";
	}

	private static String firstLine(String request) {
		int lineEnd = request.indexOf("\r\n");
		if (lineEnd < 0) {
			lineEnd = request.indexOf('\n');
		}
		return lineEnd >= 0 ? request.substring(0, lineEnd) : request;
	}

	private static String responseForGetPath(String path, String menuJson) {
		switch (path) {
			case "/menu/total":
				return handleMenuTotal(menuJson);
			default:
				return handleMenuFull(menuJson);
		}
	}

	private static String handlePostMenu(String body) throws IOException {
		if (body == null || body.isBlank()) {
			throw new IllegalArgumentException("Request body is required");
		}
		JsonObject item = JsonParser.parseString(body).getAsJsonObject();
		validateMenuItem(item);

		synchronized (MENU_FILE_LOCK) {
			String menuJson = Files.readString(MENU_JSON_PATH);
			JsonArray items = JsonParser.parseString(menuJson).getAsJsonArray();
			assignIdIfMissing(items, item);
			items.add(item);
			Files.writeString(MENU_JSON_PATH, GSON_PRETTY.toJson(items), StandardCharsets.UTF_8);
			return GSON_PRETTY.toJson(item);
		}
	}

	private static void validateMenuItem(JsonObject item) {
		requireNonBlankString(item, "name");
		requireNonBlankString(item, "description");
		requireNonBlankString(item, "category");
		if (!item.has("price") || item.get("price").isJsonNull()) {
			throw new IllegalArgumentException("price is required");
		}
		if (!item.get("price").isJsonPrimitive() || !item.get("price").getAsJsonPrimitive().isNumber()) {
			throw new IllegalArgumentException("price must be a number");
		}
		if (item.has("priceWithDiscount") && !item.get("priceWithDiscount").isJsonNull()) {
			JsonElement discount = item.get("priceWithDiscount");
			if (!discount.isJsonPrimitive() || !discount.getAsJsonPrimitive().isNumber()) {
				throw new IllegalArgumentException("priceWithDiscount must be a number");
			}
		}
		if (item.has("id") && !item.get("id").isJsonNull()) {
			JsonElement id = item.get("id");
			if (!id.isJsonPrimitive() || !id.getAsJsonPrimitive().isNumber()) {
				throw new IllegalArgumentException("id must be a number");
			}
		}
	}

	private static void requireNonBlankString(JsonObject item, String field) {
		if (!item.has(field) || item.get(field).isJsonNull() || !item.get(field).isJsonPrimitive()) {
			throw new IllegalArgumentException(field + " is required");
		}
		String value = item.get(field).getAsString();
		if (value.isBlank()) {
			throw new IllegalArgumentException(field + " is required");
		}
	}

	private static void assignIdIfMissing(JsonArray items, JsonObject newItem) {
		if (newItem.has("id") && !newItem.get("id").isJsonNull()) {
			return;
		}
		long max = 0L;
		for (JsonElement el : items) {
			if (el.isJsonObject()) {
				JsonObject o = el.getAsJsonObject();
				if (o.has("id") && !o.get("id").isJsonNull() && o.get("id").getAsJsonPrimitive().isNumber()) {
					long id = o.get("id").getAsLong();
					if (id > max) {
						max = id;
					}
				}
			}
		}
		newItem.addProperty("id", max + 1);
	}

	private static String handleMenuTotal(String menuJson) {
		JsonArray items = JsonParser.parseString(menuJson).getAsJsonArray();
		JsonObject totalJson = new JsonObject();
		totalJson.addProperty("total", items.size());
		return totalJson.toString();
	}

	private static String handleMenuFull(String menuJson) {
		return menuJson;
	}

	private static String jsonError(String message) {
		JsonObject err = new JsonObject();
		err.addProperty("error", message);
		return err.toString();
	}

	private static void writeJsonResponse(OutputStream clientOutputStream, int statusCode, String reason,
			String jsonBody) throws IOException {
		byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);
		PrintStream printStream = new PrintStream(clientOutputStream, true, StandardCharsets.UTF_8);
		printStream.print("HTTP/1.1 " + statusCode + " " + reason + "\r\n");
		printStream.print("Content-Type: application/json; charset=UTF-8\r\n");
		printStream.print("Content-Length: " + bodyBytes.length + "\r\n");
		printStream.print("\r\n");
		printStream.write(bodyBytes);
	}

}
