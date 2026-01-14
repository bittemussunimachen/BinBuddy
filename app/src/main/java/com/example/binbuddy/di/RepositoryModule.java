package com.example.binbuddy.di;

import android.content.Context;

import com.example.binbuddy.data.dao.ProductDao;
import com.example.binbuddy.data.mapper.ProductMapper;
import com.example.binbuddy.data.mapper.WasteCategoryMapper;
import com.example.binbuddy.data.remote.OpenFoodFactsApi;
import com.example.binbuddy.data.repository.ProductRepositoryImpl;
import com.example.binbuddy.data.repository.UserProgressRepositoryImpl;
import com.example.binbuddy.data.repository.WasteCategoryRepositoryImpl;
import com.example.binbuddy.domain.repository.ProductRepository;
import com.example.binbuddy.domain.repository.UserProgressRepository;
import com.example.binbuddy.domain.repository.WasteCategoryRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Provides
    @Singleton
    public static WasteCategoryMapper provideWasteCategoryMapper() {
        return new WasteCategoryMapper();
    }

    @Provides
    @Singleton
    public static ProductMapper provideProductMapper() {
        return new ProductMapper();
    }

    @Binds
    @Singleton
    public abstract WasteCategoryRepository bindWasteCategoryRepository(
            WasteCategoryRepositoryImpl impl
    );

    @Provides
    @Singleton
    public static UserProgressRepository provideUserProgressRepository(
            @ApplicationContext Context context) {
        return new UserProgressRepositoryImpl(context);
    }

    @Provides
    @Singleton
    public static ProductRepository provideProductRepository(
            @ApplicationContext Context context,
            ProductDao productDao,
            OpenFoodFactsApi apiService,
            ProductMapper productMapper,
            WasteCategoryMapper wasteCategoryMapper) {
        return new ProductRepositoryImpl(
                context,
                productDao,
                apiService,
                productMapper,
                wasteCategoryMapper
        );
    }

    // TODO: Add other repository bindings when implementations are created
    // @Binds
    // @Singleton
    // public abstract ScanHistoryRepository bindScanHistoryRepository(ScanHistoryRepositoryImpl impl);
    //
    // @Binds
    // @Singleton
    // public abstract FavoriteProductRepository bindFavoriteProductRepository(FavoriteProductRepositoryImpl impl);
}
