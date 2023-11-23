package myid.shizuka.rpl.utils

import myid.shizuka.rpl.R

object IconPicker {

    val icons = arrayOf(
        R.drawable.ic_icon_1,
        R.drawable.ic_javalooping,
        R.drawable.ic_java,
        R.drawable.ic_icon_4,
        R.drawable.ic_icon_5,
        R.drawable.ic_icon_6,
        R.drawable.ic_icon_7,
        R.drawable.ic_icon_8
    )
    private var currentIconIndex = 0

    fun getIcon(): Int {
        val icon = icons[currentIconIndex]
        currentIconIndex = (currentIconIndex + 1) % icons.size
        return icon
    }

    fun resetIconIndex() {
        currentIconIndex = 0
    }
}