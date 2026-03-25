import com.google.gson.Gson;
import mx.florinda.menu.MenuItem;

import java.math.BigDecimal;

import static mx.florinda.menu.MenuItem.MenuCategory.*;

public class Main {

    public static void main(String[] args) {

        MenuItem refrescoDoChaves = new MenuItem(1L, "Refresco do Chaves", """
                Suco de limão, que parece tamarindo, mas tem gosto de groselha
                """, BEVERAGES, new BigDecimal("2.99"), null);

        Gson gson = new Gson();
        String json = gson.toJson(refrescoDoChaves);

        System.out.println(json);
    }
}
