package io.github.aedev.flow.data.audio.eq

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface EqualizerModule {
    @Binds
    fun bindPersistence(store: EqStateStore): EqStatePersistence
}
