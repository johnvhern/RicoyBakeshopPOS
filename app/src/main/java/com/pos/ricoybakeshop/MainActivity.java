package com.pos.ricoybakeshop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnLogout;
    RecyclerView sidebarRecyclerView;
    List<MenuItem> menuItemList;
    MenuAdapter menuAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        String userRole = prefs.getString("role", "cashier"); // fallback to "cashier"

        if (savedInstanceState == null) {
            Fragment defaultFragment = "baker".equals(userRole)
                    ? new Fragment_Vendor()
                    : new Fragment_SalesOverview();

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainer, defaultFragment)
                    .commit();
        }


        sidebarRecyclerView = findViewById(R.id.sidebarRecyclerView);
        sidebarRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        btnLogout = findViewById(R.id.btnLogout);

        String role = SessionManager.getInstance(this).getUserRole();
        menuItemList = getMenuItemsForRole(role);

        menuAdapter = new MenuAdapter(menuItemList, menuItem -> {
            Fragment selecedFragment = null;
            String title = menuItem.getTitle();

            switch (title){
                case "Sales Overview":
                    selecedFragment = new Fragment_SalesOverview();
                    break;
                case "Inventory Alerts":
                    selecedFragment = new Fragment_InventoryAlert();
                    break;
                case "POS":
                    selecedFragment = new Fragment_Pos();
                    break;
                case "Category":
                    selecedFragment = new Fragment_Category();
                    break;
            }

            if (selecedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, selecedFragment)
                        .commit();
            }

        });

        sidebarRecyclerView.setAdapter(menuAdapter);
        int spaceInPixels = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 12, getResources().getDisplayMetrics()
        );

        sidebarRecyclerView.addItemDecoration(new VerticalSpaceItemDecoration(spaceInPixels));

        btnLogout.setOnClickListener(v -> {
            SessionManager sessionManager = new SessionManager(this);
            sessionManager.logout();

            Intent intent = new Intent(MainActivity.this, login.class);
            startActivity(intent);
            finish();
        });
    }

    private List<MenuItem> getMenuItemsForRole(String role) {
        List<MenuItem> items = new ArrayList<>();
        if (role.equals("admin") || role.equals("cashier")) {
            items.add(new MenuItem(R.drawable.sales, "Sales Overview"));
            items.add(new MenuItem(R.drawable.alert, "Inventory Alerts"));
            items.add(new MenuItem(R.drawable.calculator, "POS"));
            items.add(new MenuItem(R.drawable.history, "Transaction History"));
            items.add(new MenuItem(R.drawable.fileclock, "Shift Summary"));
            items.add(new MenuItem(R.drawable.menu, "Category"));
            items.add(new MenuItem(R.drawable.cookies, "Product List"));
            items.add(new MenuItem(R.drawable.circlearrowup, "Product Stock In"));
            items.add(new MenuItem(R.drawable.circlearrowdown, "Product Adjustment"));
        }
        if (role.equals("admin") || role.equals("baker")) {
            items.add(new MenuItem(R.drawable.truck, "Vendor"));
            items.add(new MenuItem(R.drawable.wheats, "Ingredients List"));
            items.add(new MenuItem(R.drawable.circlearrowup, "Ingredients Stock In"));
            items.add(new MenuItem(R.drawable.circlearrowdown, "Ingredients Adjustment"));
            items.add(new MenuItem(R.drawable.chartcolumnbig, "Ingredients Usage"));
        }
        if (role.equals("admin")) {
            items.add(new MenuItem(R.drawable.usersround, "User Management"));
            items.add(new MenuItem(R.drawable.flag, "Reports"));
            items.add(new MenuItem(R.drawable.settings, "Settings"));
            items.add(new MenuItem(R.drawable.databasebackup, "Backup and Restore"));
        }
        return items;
    }

    private void loadFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}