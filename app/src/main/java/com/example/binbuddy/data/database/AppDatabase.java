package com.example.binbuddy.data.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.binbuddy.data.dao.FavoriteProductDao;
import com.example.binbuddy.data.dao.ProductDao;
import com.example.binbuddy.data.dao.ScanHistoryDao;
import com.example.binbuddy.data.dao.WasteCategoryDao;
import com.example.binbuddy.data.entity.FavoriteProductEntity;
import com.example.binbuddy.data.entity.ProductEntity;
import com.example.binbuddy.data.entity.ScanHistoryEntity;
import com.example.binbuddy.data.entity.WasteCategoryEntity;

@Database(
    entities = {
        ProductEntity.class,
        ScanHistoryEntity.class,
        WasteCategoryEntity.class,
        FavoriteProductEntity.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductDao productDao();
    public abstract ScanHistoryDao scanHistoryDao();
    public abstract WasteCategoryDao wasteCategoryDao();
    public abstract FavoriteProductDao favoriteProductDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class,
                        "binbuddy_database"
                    )
                    .addCallback(new DatabaseInitializer())
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
