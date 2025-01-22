package com.facedetection.di

import com.facedetection.data.repository.FaceRepositoryImpl
import com.facedetection.data.repository.FaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFaceRepository(): FaceRepository {
        return FaceRepositoryImpl()
    }
}