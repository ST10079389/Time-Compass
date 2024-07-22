object CategoryList{
    val categoryList = mutableListOf<Category>()

}
data class Category(
    val categoryID: String ="",
    val userID: String ="",
    val categoryName: String ="",
    val color: String ="",
)