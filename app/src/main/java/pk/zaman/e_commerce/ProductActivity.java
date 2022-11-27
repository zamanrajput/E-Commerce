package pk.zaman.e_commerce;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pk.zaman.e_commerce.databinding.ActivityProductBinding;
import pk.zaman.e_commerce.roomDB.Product;

public class ProductActivity extends AppCompatActivity {
    protected ActivityProductBinding binding;
    List<Product> products = new ArrayList<>();

    public static byte[] imageTOShow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle("Products");
        binding.productRecView.setAdapter(new Adapter(getApplicationContext()));
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
                startActivity(new Intent(ProductActivity.this, CheckOutActivity.class));
                break;
            case R.id.profile:
                String type = Util.getPref(getApplicationContext()).getString("username", "");
                Util.showToast(getApplicationContext(), "you are in " + type + " profile");
                break;
            case R.id.logOut:
                Util.getPrefEditor(getApplicationContext()).putString("username", "").apply();
                Util.getPrefEditor(getApplicationContext()).putString("pass", "").apply();
                startActivity(new Intent(ProductActivity.this, ConfirmerActivity.class));
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
            this.products = Util.getAllProducts(context);

        }


        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(context).inflate(R.layout.row_layout, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull Holder holder, @SuppressLint("RecyclerView") int position) {
            SharedPreferences preferences = context.getSharedPreferences("Pref", MODE_PRIVATE);
            String sym = preferences.getString("sym", "").equals("") ? "$" : preferences.getString("sym", "");


            Product product = products.get(position);
            holder.itemView.setOnClickListener(view -> openDetailedActivity(product));


            holder.symbol.setText(sym);
            holder.price.setText(product.getPrice() + "");
            holder.title.setText(product.getTitle());
            holder.rating.setText(product.getRating() + "");
            holder.thumbnail.setImageBitmap(Util.getBitmapFromByteArray(product.getImage()));
            holder.buttonBuy.setOnClickListener(view -> buyItem(product));

        }

        @SuppressLint("SetTextI18n")
        private void buyItem(Product product) {
            Dialog dialog = new Dialog(ProductActivity.this);
            dialog.setContentView(R.layout.buy_dailog_layout);
            Util.makeDialogBackgroundTransparent(getApplicationContext(), dialog);
            dialog.setCancelable(true);
            dialog.show();

            TextView result = dialog.findViewById(R.id.amoutResBuyD);
            EditText quantity = dialog.findViewById(R.id.quantityBuyD);
            Button buttonBuy = dialog.findViewById(R.id.buttonBuyD);
            Button buttonCancel = dialog.findViewById(R.id.cancelButtonD);
            Button buttonAddToCart = dialog.findViewById(R.id.addToCartD);
            ImageView imageView = dialog.findViewById(R.id.buyDialogIV);
            imageView.setImageBitmap(Util.getBitmapFromByteArray(product.getImage()));

            result.setText(1 + " x " + product.getPrice() + " = " + product.getPrice() + "$");
            quantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (!charSequence.toString().isEmpty()) {
                        try {
                            int ans = (int) (Integer.parseInt(charSequence.toString()) * product.getPrice());
                            result.setText(charSequence + " x " + product.getPrice() + " = " + ans + "$");
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            int ans = (int) (product.getPrice());
                            result.setText(1 + " x " + product.getPrice() + " = " + ans + "$");
                        }

                    } else {
                        result.setText("0");
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

            buttonCancel.setOnClickListener(view -> dialog.dismiss());
            buttonBuy.setOnClickListener(view -> {
                if (quantity.getText().toString().equals("")) {
                    Util.showToast(getApplicationContext(), "Please Enter Quantity");
                } else {
                    Util.showOrderSuccessDialog(ProductActivity.this);
                    dialog.cancel();
                }
            });

            buttonAddToCart.setOnClickListener(view -> {
                if (quantity.getText().toString().equals("")) {
                    Util.showToast(getApplicationContext(), "Please Enter Quantity");
                } else {
                    Util.reflectDialog(ProductActivity.this);
                    Product productToAdd = new Product(product.getTitle(), product.getDescription(), product.getPrice(), product.getBrand(), product.rating, product.image);
                    for (int i = 0; i < Integer.parseInt(quantity.getText().toString()); i++) {
                        Util.getCartDataBaseInstance(getApplicationContext()).productDAO().insert(productToAdd);
                    }

                    Dialog okDialog = new Dialog(ProductActivity.this);
                    okDialog.setCancelable(true);
                    okDialog.setContentView(R.layout.buy_dailog_ok_layout);
                    okDialog.show();
                    Util.makeDialogBackgroundTransparent(context, okDialog);
                    Button checkOut = okDialog.findViewById(R.id.checkButtonSD);
                    Button goBack = okDialog.findViewById(R.id.goBackSD);
                    checkOut.setOnClickListener(v -> checkOutClick(okDialog));
                    goBack.setOnClickListener(v -> okDialog.dismiss());

                    dialog.cancel();
                }
            });


        }

        private void checkOutClick(Dialog dialog) {
            dialog.dismiss();
            Intent intent = new Intent(ProductActivity.this, CheckOutActivity.class);
            intent.putExtra("type", "check");
            startActivity(intent);
        }

        private void openDetailedActivity(Product product) {
            Intent intent = new Intent(ProductActivity.this, DetailedActivity.class);
            intent.putExtra("des", product.getDescription()).
                    putExtra("title", product.getTitle()).
                    putExtra("price", product.getPrice() + "").
                    putExtra("rating", product.getRating() + "").
                    putExtra("brand", product.getBrand());

            imageTOShow = product.getImage();
            startActivity(intent);
        }


        @Override
        public int getItemCount() {
            return products.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            ImageView thumbnail;
            TextView price, symbol, title, rating;
            Button buttonBuy;

            public Holder(@NonNull View itemView) {
                super(itemView);
                thumbnail = itemView.findViewById(R.id.thumbnailRL);
                price = itemView.findViewById(R.id.priceRL);
                symbol = itemView.findViewById(R.id.symbolRL);
                title = itemView.findViewById(R.id.titleRL);
                rating = itemView.findViewById(R.id.ratingRL);
                buttonBuy = itemView.findViewById(R.id.buyButton);

            }
        }
    }
}