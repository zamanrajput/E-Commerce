package pk.zaman.e_commerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pk.zaman.e_commerce.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.buttonLogin.setOnClickListener(view -> Login());
        if (getIntent().getStringExtra("selected_type").equals("user")) {
            binding.loginIconView.setImageResource(R.drawable.smilie);
        } else {
            binding.loginIconView.setImageResource(R.drawable.admin);
        }

    }


    private void Login() {
        SharedPreferences.Editor editor = Util.getPrefEditor(getApplicationContext());
        String email = binding.emailET.getText().toString();
        String pass = binding.passET.getText().toString();
        if (email.equals("") | pass.equals("")) {
            Toast.makeText(LoginActivity.this, "please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            if (email.equals("admin") && pass.equals("admin")) {
                Util.reflectDialog(LoginActivity.this);
                editor.putString("username", "admin");
                editor.putString("pass", "admin");
                editor.apply();
                startActivity(new Intent(LoginActivity.this, AdminActivity.class));
                new Handler().postDelayed(this::finish, 950);
            } else if (email.equals("user") && pass.equals("user")) {
                Util.reflectDialog(LoginActivity.this);
                editor.putString("username", "user");
                editor.putString("pass", "user");
                editor.apply();
                startActivity(new Intent(LoginActivity.this, ProductActivity.class));
                new Handler().postDelayed(this::finish, 950);
            } else {
                Toast.makeText(getApplicationContext(), "invalid credentials ", Toast.LENGTH_SHORT).show();
            }
        }


    }
}