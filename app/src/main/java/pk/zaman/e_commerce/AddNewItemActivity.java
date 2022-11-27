package pk.zaman.e_commerce;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import pk.zaman.e_commerce.databinding.ActivityAddNewItemBinding;
import pk.zaman.e_commerce.roomDB.Product;

public class AddNewItemActivity extends AppCompatActivity {
    private static final int PICK_PHOTO_FOR_AVATAR = 1234;
    private ActivityAddNewItemBinding binding;
    private byte[] image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.saveBTN.setOnClickListener(v -> insertRecord());
        binding.uploadIMG.setOnClickListener(view -> pickImage());
    }


    public void pickImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error

                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Bitmap bmp = Util.inputStream2Bitmap(inputStream);
                int size = bmp.getWidth() * bmp.getHeight();

                if (bmp.getHeight() > 600 || bmp.getWidth() > 600) {
                    Toast.makeText(getApplicationContext(), "image must be equal or less than 600×540", Toast.LENGTH_SHORT).show();
                } else {
                    image = new byte[size];
                    image = Util.getBytesFromBitmap(getApplicationContext(), bmp);
                    binding.uploadIMG.setImageBitmap(bmp);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private byte[] getBytes(InputStream is) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[16384];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        return buffer.toByteArray();
    }

    private void insertRecord() {
        String title = binding.titleDET.getText().toString();
        String description = binding.descriptionDET.getText().toString();
        String brand = binding.brandDET.getText().toString();
        String price = binding.priceDET.getText().toString();
        String rating = binding.ratingDET.getText().toString();
        if (title.equals("") || description.equals("") || brand.equals("") || price.equals("") || rating.equals("") || image == null) {
            Toast.makeText(getApplicationContext(), "please fill all fields", Toast.LENGTH_SHORT).show();
        } else {
            Product product = new Product(title, description, Float.parseFloat(price), brand, Float.parseFloat(rating), image);
            Util.getProductDAO(getApplicationContext()).insert(product);
            Util.reflectDialog(AddNewItemActivity.this);
            Product product1 = Util.getProductDAO(getApplicationContext()).findByTitle(title);
            AdminActivity.productsToUpdate.add(product1);
            AdminActivity.adapter.notifyDataSetChanged();
            new Handler().postDelayed(this::finish, 950);
        }


    }
}