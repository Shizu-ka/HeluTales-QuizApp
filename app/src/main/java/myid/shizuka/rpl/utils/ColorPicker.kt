package myid.shizuka.rpl.utils

object ColorPicker {
    val colors = arrayOf(
        "#48B3AC",
        "#97E2C6",
        "#D36280",
        "#48B3AC",
        "#FA8056",
        "#818BCA",
        "#7D659F",
        "#51BAB3",
        "#4FB66C",
        "#E3AD17",
        "#627991",
        "#EF8EAD",
        "#B5BFC6"
    )
    var currentColorIndex = 0

    fun getColor(): String {
        val color = colors[currentColorIndex]
        currentColorIndex = (currentColorIndex + 1) % colors.size
        return color
    }

    fun resetColorIndex() {
        currentColorIndex = 0
    }
}