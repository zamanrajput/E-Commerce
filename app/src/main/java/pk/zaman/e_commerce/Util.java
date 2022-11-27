package pk.zaman.e_commerce;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.room.Room;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import pk.zaman.e_commerce.roomDB.Product;
import pk.zaman.e_commerce.roomDB.ProductDAO;
import pk.zaman.e_commerce.roomDB.ProductDataBase;

public class Util {

    public static final String PRODUCT_DATABASE_TITLE = "shoppy_db";
    public static final String CART_DATABASE_TITLE = "cart_shoppy_db";

    public static final String PREF_NAME = "shoppy_pref";

    public static ProductDataBase getProductDataBaseInstance(Context context) {
        return Room.databaseBuilder(context,
                ProductDataBase.class, PRODUCT_DATABASE_TITLE).allowMainThreadQueries().build();
    }

    public static ProductDataBase getProductDataBaseInstanceBG(Context context) {
        return Room.databaseBuilder(context,
                ProductDataBase.class, PRODUCT_DATABASE_TITLE).build();
    }

    public static ProductDataBase getCartDataBaseInstance(Context context) {
        return Room.databaseBuilder(context,
                ProductDataBase.class, CART_DATABASE_TITLE).allowMainThreadQueries().build();
    }

    public static ProductDataBase getCartDataBaseInstanceBG(Context context) {
        return Room.databaseBuilder(context,
                ProductDataBase.class, CART_DATABASE_TITLE).build();
    }

    public static void showOrderSuccessDialog(Context context) {
        Dialog dialog = new Dialog(context);
        dialog.setCancelable(false);
        makeDialogBackgroundTransparent(context, dialog);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(R.layout.buy_dailog_success_layout);
        Button ok = dialog.findViewById(R.id.OkBtn);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public static void deleteAllCartProductsBG(Context context) {

        List<Product> list = getCartDataBaseInstanceBG(context).productDAO().getAll();
        for (int i = 0; i < list.size(); i++) {
            getCartDataBaseInstanceBG(context).productDAO().delete(list.get(i));
        }
    }

    public static void deleteAllProductsBG(Context context) {

        List<Product> list = getProductDataBaseInstanceBG(context).productDAO().getAll();
        for (int i = 0; i < list.size(); i++) {
            getProductDataBaseInstanceBG(context).productDAO().delete(list.get(i));
        }
    }


    public static List<Product> getAllProducts(Context context) {
        ProductDAO productDAO = getProductDataBaseInstance(context).productDAO();
        return productDAO.getAll();
    }


    public static void makeDialogBackgroundTransparent(Context context, Dialog dialog) {
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(AppCompatResources.getDrawable(context, android.R.color.transparent));
    }

    public static List<Product> getAllCartProducts(Context context) {
        ProductDAO productDAO = getCartDataBaseInstance(context).productDAO();
        return productDAO.getAll();
    }

    public static ProductDAO getProductDAO(Context context) {
        return getProductDataBaseInstance(context).productDAO();
    }

    public static SharedPreferences getPref(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor getPrefEditor(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }


    public static boolean deleteProduct(Context context, Product product) {
        ProductDAO productDAO = getProductDAO(context);
        List<Product> list = getAllProducts(context);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) == product) {
                productDAO.delete(product);
                return true;
            }
        }
        return false;
    }

    @NonNull
    public static byte[] getBytesFromDrawableRes(Context context, int res) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), res);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    @NonNull
    public static byte[] getBytesFromBitmap(Context context, Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


    public static Bitmap inputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        @SuppressWarnings("deprecation")
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        return (Drawable) bd;
    }

    public static Bitmap getBitmapFromByteArray(byte[] bytArray) {
        return BitmapFactory.decodeByteArray(bytArray, 0, bytArray.length);
    }

    public static void reflectDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.show();
        new Handler().postDelayed(progressDialog::dismiss, 900);
    }
}
