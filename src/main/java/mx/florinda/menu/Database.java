package mx.florinda.menu;

import static mx.florinda.menu.MenuItem.MenuCategory.*;

import java.math.BigDecimal;
import java.util.*;

public class Database {

	private final Map<Long, MenuItem> items = new HashMap<>();
	private final Map<MenuItem, BigDecimal> pricesAudit = new HashMap<>();

	public Database() {
		var refrescoDoChaves = new MenuItem(
				1L,
				"Refresco do Chaves",
				"""
						Suco de limão, que parece tamarindo, mas tem gosto de groselha
						""",
				BEVERAGES,
				new BigDecimal("2.99"),
				null);
		items.put(1L, refrescoDoChaves);

		var sanduicheDoChaves = new MenuItem(
				2L,
				"Sanduíche de Presunto do Chaves",
				"Sanduíche de presunto simples, mas feito com muito amor.",
				MAIN_COURSES,
				new BigDecimal("3.50"),
				new BigDecimal("2.99"));
		items.put(2L, sanduicheDoChaves);

		var tortaDaDonaFlorinda = new MenuItem(
				5L,
				"Torta de Frango da Dona Florinda",
				"Torta de frango com recheio cremoso e massa crocante.",
				MAIN_COURSES,
				new BigDecimal("12.99"),
				new BigDecimal("10.99"));
		items.put(5L, tortaDaDonaFlorinda);

		var pipocaDoQuico = new MenuItem(
				6L,
				"Pipoca do Quico",
				"Balde de pipoca preparado com carinho pelo Quico.",
				MAIN_COURSES,
				new BigDecimal("4.99"),
				new BigDecimal("3.99"));
		items.put(6L, pipocaDoQuico);

		var aguaDeJamaica = new MenuItem(
				7L,
				"Água de Jamaica",
				"Água aromatizada com hibisco e toque de açúcar.",
				BEVERAGES,
				new BigDecimal("2.50"),
				new BigDecimal("2.00"));
		items.put(7L, aguaDeJamaica);

		var churrosDoChaves = new MenuItem(
				9L,
				"Churros do Chaves",
				"Churros recheados com doce de leite, clássicos e irresistíveis.",
				DESSERTS,
				new BigDecimal("4.99"),
				new BigDecimal("3.99"));
		items.put(9L, churrosDoChaves);
	}

	public List<MenuItem> menuItemsList() {
		return new ArrayList<>(items.values());
	}

	public Optional<MenuItem> findItemById(Long id) {
		return Optional.ofNullable(items.get(id));
	}

	public boolean removeItemById(Long id) {
		MenuItem item = items.remove(id);
		return item != null;
	}

	public boolean updatePrice(Long id, BigDecimal price) {
		MenuItem item = items.get(id);
		if (item == null) {
			return false;
		}

		items.put(id, item.setPrice(price));
		return true;
	}

	public void priceAuditHistory() {
		System.out.printf("Price audit history:\n");
		pricesAudit.forEach(
				(oldItem, newPrice) -> System.out.printf("Old price: %s, New price: %s\n", oldItem.price(), newPrice));

	}
}
