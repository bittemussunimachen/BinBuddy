package com.example.binbuddy.di;

import com.example.binbuddy.data.mapper.WasteCategoryMapper;
import com.example.binbuddy.data.repository.WasteCategoryRepositoryImpl;
import com.example.binbuddy.domain.repository.WasteCategoryRepository;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class RepositoryModule {

    @Provides
    @Singleton
    public static WasteCategoryMapper provideWasteCategoryMapper() {
        return new WasteCategoryMapper();
    }

    @Binds
    @Singleton
    public abstract WasteCategoryRepository bindWasteCategoryRepository(
            WasteCategoryRepositoryImpl impl
    );

    // TODO: Add other repository bindings when implementations are created
    // @Binds
    // @Singleton
    // public abstract ProductRepository bindProductRepository(ProductRepositoryImpl impl);
    //
    // @Binds
    // @Singleton
    // public abstract ScanHistoryRepository bindScanHistoryRepository(ScanHistoryRepositoryImpl impl);
    //
    // @Binds
    // @Singleton
    // public abstract FavoriteProductRepository bindFavoriteProductRepository(FavoriteProductRepositoryImpl impl);
}
