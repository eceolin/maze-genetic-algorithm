package puc.ia.maze

import java.io.File

object MazeLoader {


    fun load(): Array<IntArray> {

        var count = 0;

        var maze: Array<IntArray> = Array(0) { intArrayOf() }

        File("maze.txt").forEachLine {

            if (count == 0) {
                maze = createMaze(it)
            } else {
                fillValuesOnMaze(count, it, maze)
            }

            count++
        }

        return maze;
    }

    private fun createMaze(line: String): Array<IntArray> = Array(line.toInt()) {
        IntArray(line.toInt())
    }

    private fun fillValuesOnMaze(cont: Int, line: String, maze: Array<IntArray>) {
        val fields = line.split(" ")

        for (i in fields.indices) {
            val value = when {
                fields[i] == "E" -> Start.startNumber
                fields[i] == "S" -> Start.endNumber
                else -> fields[i].toInt()
            }
            maze[cont - 1][i] = value
        }
    }
}