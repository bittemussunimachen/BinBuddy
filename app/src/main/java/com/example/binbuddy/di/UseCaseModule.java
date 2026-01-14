package com.example.binbuddy.di;

import com.example.binbuddy.domain.repository.WasteCategoryRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module for providing Use Cases.
 * Use cases will be created here as they are implemented.
 */
@Module
@InstallIn(SingletonComponent.class)
public class UseCaseModule {

    // TODO: Add use case providers as they are implemented
    // Example:
    // @Provides
    // @Singleton
    // public static GetWasteCategoryUseCase provideGetWasteCategoryUseCase(
    //         WasteCategoryRepository repository) {
    //     return new GetWasteCategoryUseCase(repository);
    // }
}
