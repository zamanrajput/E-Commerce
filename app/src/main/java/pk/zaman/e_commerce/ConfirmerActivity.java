package pk.zaman.e_commerce;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pk.zaman.e_commerce.databinding.ActivityConfirmerBinding;

public class ConfirmerActivity extends AppCompatActivity {
    ActivityConfirmerBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfirmerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        binding.buyer.setOnClickListener(view -> OpenLogin("buyer"));
        binding.admin.setOnClickListener(view -> OpenLogin("admin"));

    }

    private void OpenLogin(String string) {
        SharedPreferences preferences = Util.getPref(getApplicationContext());
        if (preferences.getString("username", "").equals("") || preferences.getString("pass", "").equals("")) {
            Intent intent = new Intent(ConfirmerActivity.this, LoginActivity.class);
            intent.putExtra("selected_type",string);
            startActivity(intent);
        }else{
            if(preferences.getString("username","").equals("admin")){
                Intent intent = new Intent(ConfirmerActivity.this, AdminActivity.class);
                startActivity(intent);
            }else{
                startActivity(new Intent(ConfirmerActivity.this, ProductActivity.class));
            }
        }


        finish();
    }
}