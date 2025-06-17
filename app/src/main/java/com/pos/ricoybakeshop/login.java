package com.pos.ricoybakeshop;

import static com.pos.ricoybakeshop.NetworkUtils.isNetworkAvailable;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class login extends AppCompatActivity {

    private TextInputEditText userName, passWord;

    private TextInputLayout user, pass, ddbranch;
    private MaterialButton btnSignIn;

    private SupabaseClient supabase;

    private AppDatabase db;

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
        user = findViewById(R.id.user);
        pass = findViewById(R.id.pass);
        ddbranch = findViewById(R.id.ddbranch);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.bakery_branch_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        autoCompleteTextView.setAdapter(adapter);

        supabase = new SupabaseClient();
        db = AppDatabase.getInstance(getApplicationContext());
        ProgressBar progressBar = findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(v -> {
            btnSignIn.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            btnSignIn.setText("Signing in...");

            String username = userName.getText().toString().trim();
            String password = passWord.getText().toString();
            String branch = autoCompleteTextView.getText().toString().trim();

            boolean hasError = false;

            if (username.isEmpty()) {
                user.setError("Username is required");
                userName.requestFocus();
                hasError = true;
            } else {
                user.setError(null);
            }

            if (password.isEmpty()) {
                pass.setError("Password is required");
                if (!hasError) passWord.requestFocus();
                hasError = true;
            } else {
                pass.setError(null);
            }

            if (branch.isEmpty()) {
                ddbranch.setError("Please select a branch");
                if (!hasError) ddbranch.requestFocus();
                hasError = true;
            } else {
                ddbranch.setError(null);
            }

            if (hasError) {
                btnSignIn.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                btnSignIn.setText("Sign In");
                return;
            }

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                if (isNetworkAvailable(login.this)) {
                    // Online login via Supabase
                    try {
                        JSONObject profile = supabase.fetchUserProfileByUsername(username);
                        if (profile == null) {
                            runOnUiThread(() -> showToast("Username not found"));
                            btnSignIn.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            btnSignIn.setText("Sign In");
                            return;
                        }

                        String email = profile.getString("email");
                        SupabaseSession session = supabase.loginWithEmail(email, password);
                        if (session == null) {
                            runOnUiThread(() -> showToast("Invalid credentials"));
                            btnSignIn.setEnabled(true);
                            progressBar.setVisibility(View.GONE);
                            btnSignIn.setText("Sign In");
                            return;
                        }

                        // Hash password before storing
                        String hashedPassword = PasswordUtils.hash(password);

                        // Save to Room for offline login
                        LoggedInUser user = new LoggedInUser();
                        user.userId = session.getUserId();
                        user.username = username;
                        user.password = hashedPassword;
                        user.role = profile.getString("role");
                        user.branch = profile.getString("branch");

                        db.loggedInUserDao().insertUser(user);

                        runOnUiThread(() -> {
                            showToast("Login successful (online)");
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                            SessionManager sessionManager = new SessionManager(this);
                            sessionManager.saveSession(username, user.role); // Save logged in user
                        });

                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() -> showToast("Error: " + e.getMessage()));
                        btnSignIn.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        btnSignIn.setText("Sign In");
                    }
                } else {
                    // Offline login
                    LoggedInUser localUser = db.loggedInUserDao().getUser(username);
                    if (localUser != null && PasswordUtils.verify(password, localUser.password)) {
                        runOnUiThread(() -> {
                            showToast("Login successful (offline)");
                            startActivity(new Intent(login.this, MainActivity.class));
                            finish();
                            SessionManager sessionManager = new SessionManager(this);
                            sessionManager.saveSession(username, localUser.role); // Save logged in user
                        });
                    } else {
                        runOnUiThread(() -> showToast("Login failed"));
                        btnSignIn.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                        btnSignIn.setText("Sign In");
                    }
                }
            });
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