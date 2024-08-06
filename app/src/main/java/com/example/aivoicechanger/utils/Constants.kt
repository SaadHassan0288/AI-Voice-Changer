package com.example.aivoicechanger.utils

import android.content.Context
import com.example.aivoicechanger.R
import com.example.aivoicechanger.models.BackgroundModel
import com.example.aivoicechanger.models.GenericModel
import java.sql.Types.NULL
import java.text.SimpleDateFormat
import java.util.*

object Constants {

    val arrayFreeVoices = arrayListOf(
        GenericModel(R.drawable.normal_icon, "Normal", true, Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.baby_girl_icon, "Baby Girl", audioEffect = Pair(1.8f, 1.0f)),
        GenericModel(R.drawable.baby_boy_icon, "Baby Boy", audioEffect = Pair(1.5f, 1.0f)),
        GenericModel(R.drawable.woman_icon, "Women", audioEffect = Pair(1.2f, 1.0f)),
        GenericModel(R.drawable.man_icon, "Men", audioEffect = Pair(0.8f, 1.0f)),
        GenericModel(R.drawable.old_man_icon, "Old Men", audioEffect = Pair(0.6f, 1.0f)),
        GenericModel(R.drawable.nervous_icon, "Nervous", audioEffect = Pair(1.5f, 1.2f)),
        GenericModel(R.drawable.giant_icon, "Giant", audioEffect = Pair(0.5f, 0.8f)),
        GenericModel(R.drawable.dizzy_icon, "Dizzy", audioEffect = Pair(1.0f, 0.8f)),
        GenericModel(R.drawable.drunk_icon, "Drunk", audioEffect = Pair(0.8f, 0.7f)),
        GenericModel(R.drawable.chipmunk_icon, "Chipmunk", audioEffect = Pair(2.0f, 1.0f)),
        GenericModel(R.drawable.bee_icon, "Bee", audioEffect = Pair(1.8f, 1.2f)),
        GenericModel(R.drawable.sheep_icon, "Sheep", audioEffect = Pair(1.2f, 1.2f)),
        GenericModel(R.drawable.robot_icon, "Robot", audioEffect = Pair(1.0f, 0.5f)),
        GenericModel(R.drawable.lion_icon, "Lion", audioEffect = Pair(0.7f, 0.8f)),
        GenericModel(R.drawable.echo_icon, "Echo", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.monstor_icon, "Monster", audioEffect = Pair(0.5f, 0.6f)),
        GenericModel(R.drawable.church_icon, "Church", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.death_icon, "Death", audioEffect = Pair(0.3f, 0.7f)),
        GenericModel(R.drawable.party_icon, "Party", audioEffect = Pair(1.0f, 1.5f)),
        GenericModel(R.drawable.cave_icon, "Cave", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.multiple_person_icon, "Multiple", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.fast_icon, "Fast", audioEffect = Pair(1.0f, 1.5f)),
        GenericModel(R.drawable.slower_icon, "Slower", audioEffect = Pair(1.0f, 0.5f)),
        GenericModel(R.drawable.radio_icon, "Radio", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.base_boster_icon, "Base Booster", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.rock_music_icon, "Music Rock", audioEffect = Pair(1.0f, 1.0f)),
        GenericModel(R.drawable.under_water_icon, "Under Water", audioEffect = Pair(0.8f, 0.6f)),
        GenericModel(R.drawable.witch_icon, "Witch", audioEffect = Pair(1.2f, 1.3f))
    )

    val arrayFreeBG = arrayListOf(
        BackgroundModel(R.drawable.normal_icon, "Normal", true, audioEffect = NULL),
        BackgroundModel(R.drawable.sea_icon, "Sea", audioEffect = R.raw.sea_noise),
        BackgroundModel(R.drawable.rain_icon, "Heavy Rain", audioEffect = R.raw.rain_noise),
        BackgroundModel(R.drawable.wind_icon, "Wind", audioEffect = R.raw.wind_noise),
        BackgroundModel(R.drawable.summer_icon, "Summer", audioEffect = R.raw.summer_noise),
        BackgroundModel(R.drawable.children_icon, "Children", audioEffect = R.raw.children_noise),
        BackgroundModel(R.drawable.bar_icon, "Bar Noise", audioEffect = R.raw.bar_noise),
        BackgroundModel(R.drawable.firework_icon, "Fireworks", audioEffect = R.raw.fireworks_noise),
        BackgroundModel(R.drawable.noisy_icon, "Noisy Street", audioEffect = R.raw.bar_noise),
        BackgroundModel(R.drawable.dog_icon, "Dog", audioEffect = R.raw.dog_noise),
        BackgroundModel(R.drawable.cat_icon, "Cat", audioEffect = R.raw.cat_noise),
        BackgroundModel(R.drawable.bird_icon, "Birds", audioEffect = R.raw.birds_noise),
        BackgroundModel(R.drawable.wolf_icon, "Wolf", audioEffect = R.raw.wolf_noise),
        BackgroundModel(R.drawable.tiger_icon, "Tiger", audioEffect = R.raw.tiger_noise),
        BackgroundModel(R.drawable.train_icon, "Train", audioEffect = R.raw.train_noise),
        BackgroundModel(R.drawable.police_icon, "Police", audioEffect = R.raw.police_noise),
        BackgroundModel(R.drawable.door_icon, "Door bell", audioEffect = R.raw.doorbell_noise),
        BackgroundModel(R.drawable.snoring_icon, "Snoring", audioEffect = R.raw.malesnore_noise),
        BackgroundModel(R.drawable.fart_icon, "Fart", audioEffect = R.raw.fart_noise),
        BackgroundModel(R.drawable.notification_icon, "Alarm", audioEffect = R.raw.alarm_noise)
    )

    fun formatMilliseconds(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun getCurrentFormattedTime(): String {
        val dateFormat = SimpleDateFormat("MMM dd, yyyy h:mm a", Locale.getDefault())
        val currentDate = Date()
        return dateFormat.format(currentDate)
    }


    fun getVideoFileName(context: Context, extension: String = ".mp4"): String {
        // Get the current timestamp
        val timestamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())

        // Define the file name with timestamp
        val fileName = "VID_$timestamp$extension"

        return fileName
    }

    fun getAudioFileName(context: Context, extension: String = ".aac"): String {
        // Get the current timestamp
        val timestamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(System.currentTimeMillis())

        // Define the file name with timestamp
        val fileName = "AUD_$timestamp$extension"

        return fileName
    }
}