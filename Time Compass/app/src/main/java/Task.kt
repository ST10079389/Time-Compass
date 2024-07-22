import android.net.Uri
import java.sql.Time
import java.util.Date

object TaskList{
    val taskList = mutableListOf<Task>()
}
data class Task(
    val taskID: String= "",
    val userID: String= "",
    val taskName: String= "",
    val description: String= "",
    val category: String= "",
    val taskDate : String= "",
    val startTime : String= "",
    val endTime : String= "",
    val timeDifferenceSeconds : Long= 0,
    val taskImg : String = ""
)