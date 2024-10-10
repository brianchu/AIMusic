package com.example.musicplayer

import android.content.Context
import com.example.musicplayer.util.ConfigManager
import com.example.musicplayer.viewmodel.AudioRecorder
import com.example.musicplayer.viewmodel.ChatCompletionHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class HiltModule {

    @Provides
    @Singleton
    fun provideAudioRecorder(@ApplicationContext context: Context) = AudioRecorder(context)
}

@Module
@InstallIn(ViewModelComponent::class)
class ViewModuleModule {

    @Provides
    fun provideChatCompletionHelper(configManager: ConfigManager) : ChatCompletionHelper {
        return ChatCompletionHelper(configManager)
    }
}