import android.net.Uri
import java.net.URI

data class Tasks(
    val taskID: String? = null,
    val userID: String? = null,
    val taskName: String? = null,
    val description: String? = null,
    val category: String? = null,
    val taskDate : String? = null,
    val startTime : String? = null,
    val endTime : String? = null,
    val timeDifferenceSeconds : Long? = null,
    val taskImg : String? = null
)