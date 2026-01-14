package com.example.binbuddy.di;

import android.content.Context;

import androidx.room.Room;

import com.example.binbuddy.data.dao.FavoriteProductDao;
import com.example.binbuddy.data.dao.ProductDao;
import com.example.binbuddy.data.dao.ScanHistoryDao;
import com.example.binbuddy.data.dao.WasteCategoryDao;
import com.example.binbuddy.data.database.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                AppDatabase.class,
                "binbuddy_database"
        )
        .addCallback(new com.example.binbuddy.data.database.DatabaseInitializer())
        .build();
    }

    @Provides
    public static ProductDao provideProductDao(AppDatabase database) {
        return database.productDao();
    }

    @Provides
    public static ScanHistoryDao provideScanHistoryDao(AppDatabase database) {
        return database.scanHistoryDao();
    }

    @Provides
    public static WasteCategoryDao provideWasteCategoryDao(AppDatabase database) {
        return database.wasteCategoryDao();
    }

    @Provides
    public static FavoriteProductDao provideFavoriteProductDao(AppDatabase database) {
        return database.favoriteProductDao();
    }
}
