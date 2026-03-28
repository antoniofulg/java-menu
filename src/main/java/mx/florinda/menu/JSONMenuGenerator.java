package mx.florinda.menu;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.gson.Gson;

public class JSONMenuGenerator {

	public static void main(String[] args) {
		Database database = new Database();
		List<MenuItem> menuItems = database.menuItemsList();

		Gson gson = new Gson();
		String json = gson.toJson(menuItems);
		System.out.println(json);

		Path path = Path.of("menu.json");

		try {
			Files.writeString(path, json);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
