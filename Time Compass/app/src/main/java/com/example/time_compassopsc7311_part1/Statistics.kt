package com.example.time_compassopsc7311_part1

import DailyGoal
import DailyGoalList.dailyGoalList
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.time_compassopsc7311_part1.databinding.ActivityStatisticsBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class Statistics : AppCompatActivity(), View.OnClickListener, PopupMenu.OnMenuItemClickListener {

    private lateinit var popupMenu: PopupMenu
    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var startDateTv : TextView
    private lateinit var endDateTv : TextView
    private lateinit var databaseReference: FirebaseDatabase
    private lateinit var lineChart : LineChart
    private lateinit var barChart : BarChart
    private val daysOfTheWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        getDailyGoalsFromDb()
        /*
           This code was taken from an online blog
           Titled: MPAndroidChart - Change message "No chart data available
           Uploaded by: Ricky Dev
           Available at:https://stackoverflow.com/questions/30892275/mpandroidchart-change-message-no-chart-data-available
           Accessed: 1 June 2024
        */
        binding.lineChart1.setNoDataText("Please set a start and end date first then click on search icon")
        binding.barChart1.setNoDataText("Please set a start and end date first then click on search icon")

        startDateTv = binding.startDate
        endDateTv = binding.endDate
        startDateTv.setOnClickListener(this)
        endDateTv.setOnClickListener(this)

        /*
            The code below was inspired by a Stack overflow post
            Titled: Checking if string is empty in Kotlin
            Uploaded by: Pixel Elephant
            Available at: https://stackoverflow.com/questions/45336954/checking-if-string-is-empty-in-kotlin
            Accessed: 2 June 2024
        */
        binding.filterIcon.setOnClickListener {
            if (startDateTv.text.isEmpty() && endDateTv.text.isEmpty()) {
                Toast.makeText(this, "Please select a date range for both fields to generate graph", Toast.LENGTH_SHORT).show()
            }
            else if (startDateTv.text.isEmpty() && endDateTv.text.isNotEmpty()) {
                Toast.makeText(this, "Please select a start date to generate graph", Toast.LENGTH_SHORT).show()
            }
            else if (startDateTv.text.isNotEmpty() && endDateTv.text.isEmpty()) {
                Toast.makeText(this, "Please select an end date to generate graph", Toast.LENGTH_SHORT).show()
            }
            else {
                getFilteredDataFromDb()
            }
        }

        // Get references to views using view binding
        val bottomNavigationView = binding.bottomNavigationView
        val fabPopupTray = binding.fabPopupTray

        // This makes the nav bar show what page we are on.
        bottomNavigationView.menu.findItem(R.id.bell_icon)?.isChecked = true

        // Initialize PopupMenu
        popupMenu = PopupMenu(this, fabPopupTray)
        popupMenu.inflate(R.menu.popup_menu)

        // Set Listeners
        fabPopupTray.setOnClickListener(this)
        popupMenu.setOnMenuItemClickListener(this)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.profile_icon -> {
                    // Proceed to Profile page
                    navigateToProfile()
                    true
                }
                R.id.bell_icon -> {
                    // Stay on Page
                    true
                }
                R.id.home_icon -> {
                    // Proceed to home page
                    navigateToHome()
                    finish()
                    true
                }
                R.id.controller_icon -> {
                    // Proceed to game page
                    navigateToGame()
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fabPopupTray -> {
                popupMenu.show()
            }
            R.id.startDate -> {
                datePicker(startDateTv)
            }
            R.id.endDate -> {
                datePicker(endDateTv)
            }
        }
    }

    // Menu items for Floating Action Button (plus sign)
    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.addTask -> {
                // Proceed to add a task
                val intent = Intent(this, AddTask::class.java)
                startActivity(intent)
                return true
            }
            R.id.addCategory -> {
                // Proceed to add a category
                val intent = Intent(this, AddCategory::class.java)
                startActivity(intent)
                return true
            }
            else -> return false
        }
    }

    // Methods to navigate to different pages
    private fun navigateToStats() {
        // Proceed to Stats page
        val intent = Intent(this, Statistics::class.java)
        startActivity(intent)
    }

    private fun navigateToHome() {
        // Proceed to home page
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToGame() {
        // Proceed to game page
        val intent = Intent(this, Game::class.java)
        startActivity(intent)
    }

    private fun navigateToProfile() {
        // Proceed to profile page
        val intent = Intent(this, Profile::class.java)
        startActivity(intent)
    }
    /*
        The next set of functions were inspired by a GitHub repository
        Titled: MPAndroidChart
        Uploaded by: PhilJay
        Available at: https://github.com/PhilJay/MPAndroidChart
        Accessed: 31 May 2024
    */
    private fun initialiseLineChart(entryList: List<DailyGoal>) {
        lineChart = binding.lineChart1

        lineChart.description.isEnabled = false
        lineChart.axisRight.setDrawLabels(false)

        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        xAxis.setLabelCount(7)
        xAxis.granularity = 1f
        xAxis.axisLineColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        val yAxis = lineChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.WHITE
        yAxis.labelCount = 10
        yAxis.textColor = Color.WHITE
        yAxis.setDrawGridLines(false)
        yAxis.axisMinimum = 0f

        setGraphEntriesUsingDb(entryList)
    }

    private fun setGraphEntriesUsingDb(entryList: List<DailyGoal>){
        val entries1 = ArrayList<Entry>()
        val entries2 = ArrayList<Entry>()
        val entries3 = ArrayList<Entry>()
        var positionInGraph = 0f

        for (element in entryList){
            entries1.add(Entry(positionInGraph, element.minValue.toFloat()))
            entries2.add(Entry(positionInGraph, element.maxValue.toFloat()))
            entries3.add(Entry(positionInGraph, splitAppUsageIntoHours(element.appUsageTime)))

            positionInGraph++
        }
        setLineChartDataSet(entries1, entries2, entries3)
    }

    private fun setLineChartDataSet(entries1 : ArrayList<Entry>, entries2 : ArrayList<Entry>, entries3 : ArrayList<Entry>) {
        val dataSet1 = LineDataSet(entries1, "Minimum daily goals in hours")
        dataSet1.color = ContextCompat.getColor(this, R.color.red)
        dataSet1.valueTextColor = Color.WHITE
        dataSet1.valueTextSize = 14f
        dataSet1.lineWidth = 3f
        dataSet1.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        val dataSet2 = LineDataSet(entries2, "Maximum daily goals in hours")
        dataSet2.color = ContextCompat.getColor(this, R.color.purple)
        dataSet2.valueTextColor = Color.WHITE
        dataSet2.valueTextSize = 14f
        dataSet2.lineWidth = 3f
        dataSet2.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        val dataSet3 = LineDataSet(entries3, "Daily hours spent in app")
        dataSet3.color = ContextCompat.getColor(this, R.color.blue)
        dataSet3.valueTextColor = Color.WHITE
        dataSet3.valueTextSize = 14f
        dataSet3.lineWidth = 3f
        dataSet3.mode = LineDataSet.Mode.HORIZONTAL_BEZIER

        lineChart.legend.isWordWrapEnabled = true

        lineChart.legend.maxSizePercent = 0.60f

        val lineData = LineData(dataSet1, dataSet2, dataSet3)

        lineChart.data = lineData

        lineChart.description.isEnabled = false

        lineChart.legend.textColor = Color.WHITE




        //lineChart.legend.textSize = 14f

        lineChart.setNoDataText("Not enough information available to create graph!")

        lineChart.animateX(300)

        lineChart.invalidate()
    }

    /*
        The next set of functions were taken from an online blog
        Titled: Android – Create Group BarChart with Kotlin
        Uploaded by: Chaitanyamunje
        Available at: https://www.geeksforgeeks.org/android-create-group-barchart-with-kotlin/
        Accessed: 2 June 2024
    */
    private fun initialiseBarChart(entryList: List<DailyGoal>) {
        barChart = binding.barChart1
        barChart.axisRight.setDrawLabels(false)

        addEntriesForBarChart(entryList)

        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                //Logger.log("WeeklyDistance", "value = " + (value.toInt()))
                if ((value.toInt()) > -1 && (value.toInt()) < 7) {
                    return daysOfTheWeek[value.toInt()]
                }
                return ""
            }
        }
        //xAxis.valueFormatter = IndexAxisValueFormatter(daysOfTheWeek)
        xAxis.setLabelCount(7)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true
        xAxis.axisLineColor = Color.WHITE
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)
        xAxis.setCenterAxisLabels(true)
        xAxis.axisMinimum = 0f
        xAxis.axisMaximum = barChart.highestVisibleX + 0.25f

        val yAxis = barChart.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.axisLineWidth = 2f
        yAxis.axisLineColor = Color.WHITE
        yAxis.labelCount = 10
        yAxis.textColor = Color.WHITE
        yAxis.setDrawGridLines(false)
        yAxis.axisMinimum = 0f
    }

    private fun addEntriesForBarChart(entryList: List<DailyGoal>) {
        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()
        val entries3 = ArrayList<BarEntry>()
        var positionInGraph = 0f

        for (element in entryList){
            entries1.add(BarEntry(positionInGraph, element.minValue.toFloat()))
            entries2.add(BarEntry(positionInGraph, element.maxValue.toFloat()))
            entries3.add(BarEntry(positionInGraph, splitAppUsageIntoHours(element.appUsageTime)))

            positionInGraph++
        }

        setBarChartDataSet(entries1, entries2, entries3)
    }

    private fun setBarChartDataSet(entries1 : ArrayList<BarEntry>, entries2 : ArrayList<BarEntry>, entries3 : ArrayList<BarEntry>){
        val dataSet1 = BarDataSet(entries1, "Minimum daily goals in hours")
        dataSet1.color = ContextCompat.getColor(this, R.color.red)
        dataSet1.valueTextColor = Color.WHITE
        dataSet1.valueTextSize = 14f

        val dataSet2 = BarDataSet(entries2, "Maximum daily goals in hours")
        dataSet2.color = ContextCompat.getColor(this, R.color.purple)
        dataSet2.valueTextColor = Color.WHITE
        dataSet2.valueTextSize = 14f

        val dataSet3 = BarDataSet(entries3, "Daily hours spent in app")
        dataSet3.color = ContextCompat.getColor(this, R.color.blue)
        dataSet3.valueTextColor = Color.WHITE
        dataSet3.valueTextSize = 14f

        val barData = BarData(dataSet1, dataSet2, dataSet3)

        val barSpace = 0.06f

        val groupSpace = 0.5f

        barData.barWidth = 0.10f

        barChart.data = barData

        barChart.setFitBars(true)

        barChart.description.isEnabled = false

        barChart.legend.textColor = Color.WHITE

        barChart.legend.isWordWrapEnabled = true

        barChart.legend.maxSizePercent = 0.65f
//        barChart.legend.textSize = 14f

        barChart.setVisibleXRangeMaximum(7f)

        barChart.setNoDataText("Daily hours spent not recorded! Please use the app for at least an hour")

        barChart.isHorizontalScrollBarEnabled = true

        barChart.animateY(2000)

        barChart.groupBars(0f, groupSpace, barSpace)

        barChart.invalidate()
    }

    /*
        This code was taken from an online blog
        Titled: Setting different colors to bars in MPAndroidChart Bar Chart
        Uploaded by: Sauvik
        Available at: https://stackoverflow.com/questions/46947275/setting-different-colors-to-bars-in-mpandroidchart-bar-chart
        Accessed: 31 May 2024
    */
//    private fun createGraphColorsArray() : ArrayList<Int> {
//        val colors = ArrayList<Int>()
//
//        colors.add(ContextCompat.getColor(this, R.color.red))
//        colors.add(ContextCompat.getColor(this, R.color.purple))
//        colors.add(ContextCompat.getColor(this, R.color.blue))
//
//        return colors
//    }

    /*
        The some of the code for this function was taken from an online blog
        Titled: Retrieving Data
        Uploaded by: Google
        Available at: https://firebase.google.com/docs/database/admin/retrieve-data
        Accessed: 29 May 2024
     */
    private fun getFilteredDataFromDb() {
        //use this code when you can select the start and end dates
        val startDate = binding.startDate.text.toString()
        val endDate = binding.endDate.text.toString()

        // Check if start date and end date are not empty
        if (startDate.isNotEmpty() && endDate.isNotEmpty()) {
            val startDateMillis = getDateInMillis(startDate)
            val endDateMillis = getDateInMillis(endDate)

            // Check if start date is before end date
            if (startDateMillis <= endDateMillis) {
                //setting the display
                val firebaseAuth = FirebaseAuth.getInstance().currentUser
                val userID = firebaseAuth?.uid.toString()
                databaseReference = FirebaseDatabase.getInstance()

                val dailyGoalRef = databaseReference.getReference("DailyGoals").orderByChild("userID").equalTo(userID)
                dailyGoalList.clear()

                dailyGoalRef.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (dailyGoalShot in snapshot.children) {
                            val dailyGoal = dailyGoalShot.getValue(DailyGoal::class.java)
                            if (dailyGoal != null) {
                                dailyGoalList.add(dailyGoal)

                            }
                        }

                        val filteredDailyGoals = dailyGoalList.filter { daily ->
                            val dailyGoalDateMillis = getDateInMillis(daily.currentDate)
                            dailyGoalDateMillis in startDateMillis..endDateMillis
                        }

                        if (filteredDailyGoals.isEmpty()) {
                            binding.lineChart1.clear()
                            binding.barChart1.clear()
                            binding.lineChart1.setNoDataText("No daily goals with this data range exist, please set a different date range")
                            binding.barChart1.setNoDataText("No daily goals with this data range available, please set a different date range")
                            binding.lineChart1.invalidate()
                            binding.barChart1.invalidate()
                        }
                        else if(filteredDailyGoals.size < 3){
                            initialiseBarChart(filteredDailyGoals)
                            binding.lineChart1.clear()
                            binding.lineChart1.setNoDataText("Insufficient data. Set min and max values and use app for at least 3 days")
                            binding.lineChart1.invalidate()
                        }
                        else if(filteredDailyGoals.any {it.appUsageTime == "00:00:00"})
                        {
                            Toast.makeText(this@Statistics, "One or more daily goals do not have daily app usage recorded!", Toast.LENGTH_LONG).show()
                            initialiseLineChart(filteredDailyGoals)
                            initialiseBarChart(filteredDailyGoals)
                        }
                        else {
                            initialiseLineChart(filteredDailyGoals)
                            initialiseBarChart(filteredDailyGoals)
                            Toast.makeText(this@Statistics, "Charts updated", Toast.LENGTH_LONG).show()
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@Statistics, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                Toast.makeText(this@Statistics, "Start date cannot be after end date", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            Toast.makeText(this@Statistics, "Please select a start and end date", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getDateInMillis(dateString: String): Long {
        val pattern = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            val date = sdf.parse(dateString)
            date?.time ?: 0
        } catch (e: ParseException) {
            e.printStackTrace()
            0
        }
    }

    /*
        A part of this code was taken from a Stack overflow post
        Uploaded by: JK Ly
        Titled: How could I split a String into an array in Kotlin?
        Available at: https://stackoverflow.com/questions/46038476/how-could-i-split-a-string-into-an-array-in-kotlin
        Accessed: 30 May 2024
     */
    private fun splitAppUsageIntoHours (appUsage: String) : Float{
        val parts = appUsage.split(":")
        val hours = parts[0].toFloat()
        val minutes = parts[1].toFloat() / 60
        val seconds = parts[2].toFloat() / 3600
        val total = hours + minutes + seconds
        return total
    }

    /*
        This code was taken from an online blog
        Titled: DatePicker in Kotlin
        Uploaded by: Praveenruhil
        Available at: https://www.geeksforgeeks.org/datepicker-in-kotlin/
        Accessed: 1 June 2024
     */
    private fun datePicker(textView: TextView) {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                val date = "$year-${monthOfYear + 1}-$dayOfMonth"
                textView.text = date
            },
            year,
            month,
            day
        )
        datePickerDialog.setOnShowListener {
            val dateLayout = it as DatePickerDialog
            val okButton = dateLayout.getButton(DatePickerDialog.BUTTON_POSITIVE)
            okButton.setTextColor(Color.BLACK)
        }
        datePickerDialog.show()
    }

//    private fun getDailyGoalsFromDb() {
//        val firebaseAuth = FirebaseAuth.getInstance().currentUser
//        val userID = firebaseAuth?.uid.toString()
//        databaseReference = FirebaseDatabase.getInstance()
//
//        val dailyGoalRef = databaseReference.getReference("DailyGoals").orderByChild("userID").equalTo(userID)
//        dailyGoalList.clear()
//
//        dailyGoalRef.addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (dailyGoalShot in snapshot.children) {
//                    val dailyGoal = dailyGoalShot.getValue(DailyGoal::class.java)
//                    if (dailyGoal != null) {
//                        dailyGoalList.add(dailyGoal)
//                    }
//                }
//                initialiseLineChart(dailyGoalList)
//                initialiseBarChart(dailyGoalList)
//            }
//            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@Statistics, "Failed to retrieve daily goals.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

}
