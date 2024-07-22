/*
    The code for this class was taken from an online blog
    Titled: Use classes and objects in Kotlin
    Uploaded by: Google Developers Training team
    Available at: https://developer.android.com/codelabs/basic-android-kotlin-compose-classes-and-objects
    Accessed: 24 May 2024
*/
object DailyGoalList{
    val dailyGoalList = mutableListOf<DailyGoal>()
}

class DailyGoal(
    var userID: String = "", // Assuming these are the properties of DailyGoal
    var currentDate: String = "",
    var minValue: Double = 0.0,
    var maxValue: Double = 0.0,
    var appUsageTime: String = ""
) {
    // No-argument constructor
    constructor() : this("", "", 0.0, 0.0, "")
}

