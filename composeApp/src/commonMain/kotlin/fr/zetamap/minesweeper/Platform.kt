package fr.zetamap.minesweeper

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform