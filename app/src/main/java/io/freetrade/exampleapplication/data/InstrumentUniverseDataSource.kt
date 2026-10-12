package io.freetrade.exampleapplication.data

interface InstrumentUniverseDataSource {

    fun getAvailableTickers(): List<String>
}
