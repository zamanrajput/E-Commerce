package pk.zaman.e_commerce;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pk.zaman.e_commerce.databinding.ActivityAdminBinding;
import pk.zaman.e_commerce.roomDB.Product;

public class AdminActivity extends AppCompatActivity {
    private ActivityAdminBinding binding;
    public static Adapter adapter;
    public static List<Product> productsToUpdate;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Admin");
        binding.fabADD.setOnClickListener(view -> addItem());
        adapter = new Adapter(getApplicationContext());
        binding.recViewAdmin.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_admin_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearItems:
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setMessage("Are You Sure To Delete All Items?");
                builder.setPositiveButton("Yes", (dialogInterface, i) -> {
                    Util.reflectDialog(AdminActivity.this);
                    new Thread(() -> {
                        Util.deleteAllProductsBG(getApplicationContext());
                    }).start();
                    new Handler().postDelayed(() -> {
                        adapter = new Adapter(getApplicationContext());
                        runOnUiThread(() -> binding.recViewAdmin.setAdapter(adapter));
                    }, 1500);

                }).setNegativeButton("No", null);
                builder.show();
                break;
            case R.id.profile:
                Util.showToast(getApplicationContext(), "you are in admin profile");
                break;
            case R.id.logOut:
                Util.getPrefEditor(getApplicationContext()).putString("username", "").apply();
                startActivity(new Intent(AdminActivity.this, ConfirmerActivity.class));
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private void addItem() {
        startActivity(new Intent(AdminActivity.this, AddNewItemActivity.class));
    }


    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        Context context;
        List<Product> products;


        public Adapter(Context context) {
            this.context = context;
            this.products = Util.getAllProducts(AdminActivity.this);
            productsToUpdate = products;

        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.admin_row_layout, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
            SharedPreferences preferences = context.getSharedPreferences("Pref", MODE_PRIVATE);


            Product product = products.get(position);

            holder.price.setText(product.getPrice() + "$");
            holder.title.setText(product.getTitle());
            holder.brand.setText(product.getBrand() + "");

            holder.thumbnail.setImageBitmap(Util.getBitmapFromByteArray(product.getImage()));

            holder.itemView.setOnLongClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
                builder.setCancelable(false);
                builder.setMessage("are you sure to delete this item?");
                builder.setIcon(R.drawable.warn);

                DialogInterface.OnClickListener listener = (dialogInterface, i) -> {
                    Util.reflectDialog(AdminActivity.this);
                    Util.getProductDataBaseInstance(getApplicationContext()).productDAO().delete(product);
                    products.remove(position);
                    notifyItemRemoved(position);

                };

                builder.setPositiveButton("Yes", listener).setNegativeButton("No", null);
                builder.show();
                return false;
            });
        }


        @Override
        public int getItemCount() {
            return products.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView thumbnail;
            TextView price, title, brand;

            public Holder(@NonNull View itemView) {
                super(itemView);
                thumbnail = itemView.findViewById(R.id.thumbnailARL);
                price = itemView.findViewById(R.id.priceARL);
                title = itemView.findViewById(R.id.titleARL);
                brand = itemView.findViewById(R.id.brandARL);
            }
        }
    }


}