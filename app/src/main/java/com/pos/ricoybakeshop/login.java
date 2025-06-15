package com.pos.ricoybakeshop;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.concurrent.Executors;

public class login extends AppCompatActivity {

    private TextInputEditText userName, passWord;
    private MaterialButton btnSignIn;

    private SupabaseClient supabase;
    private UserDao userDao;

    private MaterialAutoCompleteTextView autoCompleteTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName = findViewById(R.id.txtUsername);
        passWord = findViewById(R.id.txtPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        autoCompleteTextView = findViewById(R.id.branch_dropdown);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.bakery_branch_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        autoCompleteTextView.setAdapter(adapter);

        supabase = new SupabaseClient();
        userDao = AppDatabase.getInstance(this).userDao();

        btnSignIn.setOnClickListener(v -> {
            String username = userName.getText().toString().trim();
            String password = passWord.getText().toString();
            String branch = autoCompleteTextView.getText().toString();

            if (isOnline()){
                loginOnline(username, password, branch);
            }else {
                loginOffline(username, password, branch);
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    private void loginOnline(String username, String password, String branch) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                JSONObject user = supabase.fetchUser(username, branch);
                if (user == null) {
                    runOnUiThread(() -> showToast("User not found or wrong branch"));
                    return;
                }

                String email = username + "@gmail.com"; // Supabase uses email for login
                SupabaseClient.AuthResponse auth = supabase.login(email, password);
                if (auth == null) {
                    runOnUiThread(() -> showToast("Login failed."));
                    return;
                }

                // Save session to Room
                LocalUserSession session = new LocalUserSession();
                session.userId = user.getString("id");
                session.username = username;
                session.branch = branch;
                session.role = user.getString("role");
                session.hashedPassword = PasswordUtils.hash(password); // Save hashed password
                session.accessToken = auth.accessToken;
                session.refreshToken = auth.refreshToken;

                userDao.insert(session);
                runOnUiThread(() -> redirectToRoleScreen(session.role));

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> showToast("Login error: " + e.getMessage()));
            }
        });
    }

    private void loginOffline(String username, String password, String branch) {
        Executors.newSingleThreadExecutor().execute(() -> {
            LocalUserSession session = userDao.getUser(username, branch);
            if (session != null && PasswordUtils.verify(password, session.hashedPassword)) {
                runOnUiThread(() -> redirectToRoleScreen(session.role));
            } else {
                runOnUiThread(() -> showToast("Offline login failed"));
            }
        });
    }

    private void redirectToRoleScreen(String role) {
        Class<?> activityClass = null;
        switch (role) {
            case "admin": activityClass = MainActivity.class; break;
            case "cashier": activityClass = Cashier.class; break;
            case "baker": activityClass = Baker.class; break;
        }

        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            startActivity(intent);
            finish();
        } else {
            showToast("Unknown role.");
        }
    }



}