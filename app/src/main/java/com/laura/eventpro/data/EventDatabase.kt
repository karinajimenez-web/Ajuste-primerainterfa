package com.laura.eventpro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ServiceEntity::class, ReservationEntity::class, ReservationServiceCrossRef::class],
    version = 1,
    exportSchema = false
)
abstract class EventDatabase : RoomDatabase() {
    abstract fun serviceDao(): ServiceDao
    abstract fun reservationDao(): ReservationDao

    companion object {
        @Volatile private var INSTANCE: EventDatabase? = null

        fun get(context: Context): EventDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, EventDatabase::class.java, "eventpro.db")
                .addCallback(SeedCallback)
                .build()
                .also { INSTANCE = it }
        }

        // Datos iniciales: solo se insertan la primera vez que se crea la base de datos.
        private val SeedCallback = object : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                val seed = listOf(
                    arrayOf<Any>("Decoración", "Ambientación y decoración temática", 450000, "Decoración", 0),
                    arrayOf<Any>("Fotografía", "Registro fotográfico del evento", 350000, "Producción", 0),
                    arrayOf<Any>("Catering", "Servicio de alimentos para invitados", 28000, "Alimentación", 1),
                    arrayOf<Any>("Sonido y DJ", "Música, sonido e iluminación básica", 500000, "Producción", 0),
                    arrayOf<Any>("Planeación de boda", "Acompañamiento y coordinación", 900000, "Planeación", 0),
                    arrayOf<Any>("Evento empresarial", "Organización de reuniones corporativas", 750000, "Empresarial", 0),
                    arrayOf<Any>("Mesa de postres", "Estación dulce para los asistentes", 220000, "Alimentación", 0),
                    arrayOf<Any>("Alquiler de mobiliario", "Mesas y sillas para el evento", 300000, "Logística", 0)
                )
                seed.forEach {
                    db.execSQL("INSERT INTO services (name, description, price, category, perGuest) VALUES (?, ?, ?, ?, ?)", it)
                }
            }
        }
    }
}
