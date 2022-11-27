package pk.zaman.e_commerce;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import pk.zaman.e_commerce.databinding.ActivityDetailedBinding;

public class DetailedActivity extends AppCompatActivity {
    private ActivityDetailedBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        try {
            if (intent.getStringExtra("type").equals("check")) {
                getSupportActionBar().setTitle(intent.getStringExtra("title"));

                binding.titleTV.setText(intent.getStringExtra("title"));
                binding.brandTv.setText(intent.getStringExtra("brand"));
                binding.descriptionTV.setText(intent.getStringExtra("des"));
                binding.priceTV.setText(intent.getStringExtra("price") + "$");
                binding.ratingTV.setText(intent.getStringExtra("rating"));
                intent.putExtra("type", "");
                binding.iVD.setImageBitmap(Util.getBitmapFromByteArray(CheckOutActivity.imageTOShowCheck));
                binding.backButton.setOnClickListener(view -> super.onBackPressed());
            } else {
                getSupportActionBar().setTitle(intent.getStringExtra("title"));

                binding.titleTV.setText(intent.getStringExtra("title"));
                binding.brandTv.setText(intent.getStringExtra("brand"));
                binding.descriptionTV.setText(intent.getStringExtra("des"));
                binding.priceTV.setText(intent.getStringExtra("price") + "$");
                binding.ratingTV.setText(intent.getStringExtra("rating"));
                binding.iVD.setImageBitmap(Util.getBitmapFromByteArray(ProductActivity.imageTOShow));
                binding.backButton.setOnClickListener(view -> super.onBackPressed());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getSupportActionBar().setTitle(intent.getStringExtra("title"));
            binding.titleTV.setText(intent.getStringExtra("title"));
            binding.brandTv.setText(intent.getStringExtra("brand"));
            binding.descriptionTV.setText(intent.getStringExtra("des"));
            binding.priceTV.setText(intent.getStringExtra("price") + "$");
            binding.ratingTV.setText(intent.getStringExtra("rating"));
            binding.iVD.setImageBitmap(Util.getBitmapFromByteArray(ProductActivity.imageTOShow));
            binding.backButton.setOnClickListener(view -> super.onBackPressed());
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.checkoutOpen:
                startActivity(new Intent(DetailedActivity.this, CheckOutActivity.class));
                break;
            case R.id.profile:
                Util.showToast(getApplicationContext(), "you are in user profile");
                break;
            case R.id.logOut:
                Util.getPrefEditor(getApplicationContext()).putString("username", "").apply();
                Util.getPrefEditor(getApplicationContext()).putString("pass", "").apply();

                startActivity(new Intent(DetailedActivity.this, ConfirmerActivity.class));
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}