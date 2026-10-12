package io.freetrade.exampleapplication.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.freetrade.exampleapplication.data.FakeInstrumentUniverseDataSource
import io.freetrade.exampleapplication.data.FakeMarketDataSource
import io.freetrade.exampleapplication.data.InstrumentUniverseDataSource
import io.freetrade.exampleapplication.data.MarketDataSource

@Module
@InstallIn(SingletonComponent::class)
abstract class FakeDataModule {

    @Binds
    abstract fun bindMarketDataSource(impl: FakeMarketDataSource): MarketDataSource

    @Binds
    abstract fun bindInstrumentUniverseDataSource(
        impl: FakeInstrumentUniverseDataSource
    ): InstrumentUniverseDataSource
}
