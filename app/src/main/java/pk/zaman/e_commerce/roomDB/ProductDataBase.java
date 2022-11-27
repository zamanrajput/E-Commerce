package pk.zaman.e_commerce.roomDB;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Product.class}, version = 1)
public abstract class ProductDataBase extends RoomDatabase {
    public abstract ProductDAO productDAO();
}