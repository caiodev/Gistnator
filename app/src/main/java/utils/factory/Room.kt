package utils.factory

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

object Room {

    inline fun <reified T : RoomDatabase> provideRoomInstance(
        context: Context,
        databaseName: String
    ) =
        Room.databaseBuilder(context, T::class.java, databaseName)
            .build()
}