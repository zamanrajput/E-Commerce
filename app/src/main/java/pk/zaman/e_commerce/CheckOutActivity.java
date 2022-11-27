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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pk.zaman.e_commerce.databinding.ActivityCheckOutBinding;
import pk.zaman.e_commerce.roomDB.Product;

public class CheckOutActivity extends AppCompatActivity {
    public static byte[] imageTOShowCheck;
    ActivityCheckOutBinding binding;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Checkout");
        adapter = new Adapter(getApplicationContext());
        binding.productRecView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_checkout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearCart:
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);
                builder.setMessage("are you sure to clear");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Util.reflectDialog(CheckOutActivity.this);
                        new Thread(() -> Util.deleteAllCartProductsBG(getApplicationContext())).start();
                        new Handler().postDelayed(() -> {
                            adapter = new Adapter(getApplicationContext());
                            runOnUiThread(() -> binding.productRecView.setAdapter(adapter));
                        }, 1500);

                    }
                }).setNegativeButton("No", null);
                builder.show();
                break;

            case R.id.profile:

                Util.showToast(getApplicationContext(), "you are in user profile");
                break;
            case R.id.logOut:
                Util.getPrefEditor(getApplicationContext()).putString("username", "").apply();
                startActivity(new Intent(CheckOutActivity.this, ConfirmerActivity.class));
                finish();
                break;
        }


        return super.onOptionsItemSelected(item);
    }


    class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        Context context;
        List<Product> products;


        public Adapter(Context context) {
            this.context = context;
            this.products = Util.getAllCartProducts(getApplicationContext());

        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.row_layout_checkout, parent, false));
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
            holder.itemView.setOnClickListener(view -> openDetailedActivity(product));
            holder.remove.setOnClickListener(view -> {

                AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);
                builder.setMessage("are you sure to remove");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Util.reflectDialog(CheckOutActivity.this);
                        Util.getCartDataBaseInstance(getApplicationContext()).productDAO().delete(product);
                        products.remove(position);
                        notifyItemRemoved(position);

                    }
                }).setNegativeButton("No", null);
                builder.show();

            });
            holder.buy.setOnClickListener(view -> {
                Util.reflectDialog(CheckOutActivity.this);
                Util.getCartDataBaseInstance(getApplicationContext()).productDAO().delete(product);
                products.remove(position);
                notifyItemRemoved(position);
                Util.showOrderSuccessDialog(CheckOutActivity.this);
            });
            if (products.size() > 0) {
                binding.productRecView.setVisibility(View.VISIBLE);
                binding.noDataLayout.setVisibility(View.GONE);
            } else {
                binding.productRecView.setVisibility(View.GONE);
                binding.noDataLayout.setVisibility(View.VISIBLE);
            }

        }

        private void openDetailedActivity(Product product) {
            Intent intent = new Intent(CheckOutActivity.this, DetailedActivity.class);
            intent.putExtra("des", product.getDescription()).
                    putExtra("title", product.getTitle()).
                    putExtra("price", product.getPrice() + "").
                    putExtra("rating", product.getRating() + "").
                    putExtra("brand", product.getBrand())
                    .putExtra("type", "check");

            imageTOShowCheck = product.getImage();
            startActivity(intent);
        }


        public void syncAdapter() {
            products = Util.getAllProducts(context);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView thumbnail;
            TextView price, title, brand;
            Button buy, remove;

            public Holder(@NonNull View itemView) {
                super(itemView);
                buy = itemView.findViewById(R.id.buyCheckOutItem);
                remove = itemView.findViewById(R.id.removeCheckOutItem);
                thumbnail = itemView.findViewById(R.id.thumbnailARL);
                price = itemView.findViewById(R.id.priceARL);
                title = itemView.findViewById(R.id.titleARL);
                brand = itemView.findViewById(R.id.brandARL);
            }
        }
    }

}