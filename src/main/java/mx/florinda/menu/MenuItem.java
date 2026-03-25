package mx.florinda.menu;

import java.math.BigDecimal;

public record MenuItem(Long id, String name, String description, MenuCategory category, BigDecimal price,
                       BigDecimal priceWithDiscount) {

    public enum MenuCategory {
        STARTERS, MAIN_COURSES, BEVERAGES, DESSERTS
    }

}
