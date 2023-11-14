package com.example.navian.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navian.Achievement
import com.example.navian.R
import com.example.navian.services.readObservations

@Composable
fun AchievementScreen(navController: NavController) {
    val observations = readObservations()
    val observationsCount = observations.size
    AchievementManager.checkAchievements(observationsCount)
    val earnedAchievements = AchievementManager.achievements.filter { it.isEarned }

    Scaffold(
        topBar = { TopAppBar("Achievements") },
        bottomBar = { HomeBottomAppBar(navController) },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            items(earnedAchievements) { achievement -> AchievementCard(achievement = achievement) }
        }
    }
}

@Composable
fun AchievementCard(achievement: Achievement) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = achievement.title, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = achievement.description, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = achievement.iconResId as Int),
                contentDescription = null, // decorative
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

object AchievementManager {
    val achievements: List<Achievement> = listOf(
        Achievement(0, "Newcomer", "Used Navian for the First Time!", R.drawable.baseline_emoji_events_24),
        Achievement(1, "First Observation", "Logged your first bird observation!", R.drawable.baseline_emoji_events_24),
        Achievement(2, "Birdwatcher", "Logged 10 bird observations!", R.drawable.baseline_emoji_events_24)
        // Add more achievements here...
    )

    fun checkAchievements(observationsCount: Int) {
        achievements.forEach { achievement ->
            if (!achievement.isEarned) {
                when (achievement.id) {
                    0 -> earnAchievement(achievement, observationsCount >= 0)
                    1 -> earnAchievement(achievement, observationsCount >= 1)
                    2 -> earnAchievement(achievement, observationsCount >= 10)
                    // Add more conditions for other achievements...
                }
            }
        }
    }

    private fun earnAchievement(achievement: Achievement, condition: Boolean) {
        if (condition) {
            achievement.isEarned = true
            // Trigger logic for displaying earned achievement
            // You might want to show a notification or update UI
        }
    }
}
