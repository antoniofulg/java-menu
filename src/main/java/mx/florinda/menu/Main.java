package mx.florinda.menu;

public class Main {

	public static void main(String[] args) {

		Database database = new Database();

		ViewHistory viewHistory = new ViewHistory(database);
		viewHistory.registerView(1L);
		viewHistory.registerView(2L);
		viewHistory.registerView(5L);
		viewHistory.registerView(6L);
		viewHistory.registerView(7L);
		viewHistory.registerView(92L);

		System.out.println("--------------------------------");
		viewHistory.showTotalItemsViews();
		System.out.println("--------------------------------");
		viewHistory.listViews();
		System.out.println("--------------------------------");
	}
}
