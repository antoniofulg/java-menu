package mx.florinda.menu;

import java.time.LocalDateTime;
import java.util.*;

public class ViewHistory {

	private final Database database;

	private final Map<MenuItem, LocalDateTime> views = new HashMap<>();

	public ViewHistory(Database database) {
		this.database = database;
	}

	public void registerView(Long itemId) {
		Optional<MenuItem> item = database.findItemById(itemId);
		if (item.isEmpty()) {
			System.out.println("Item not found: " + itemId);
			return;
		}

		var menuItem = item.get();
		var now = LocalDateTime.now();
		views.put(menuItem, now);

		System.out.printf("'%s' viewed at %s\n", menuItem.name(), now.toString());
	}

	public void listViews() {
		if (views.isEmpty()) {
			System.out.println("No views registered");
			return;
		}

		views.forEach((menuItem, localDateTime) -> System.out.printf("'%s' viewed at %s\n", menuItem.name(),
				localDateTime.toString()));
	}

	public void showTotalItemsViews() {
		System.out.println("Total items views: " + views.size());
	}
}
