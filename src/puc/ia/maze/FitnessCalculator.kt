package puc.ia.maze

object FitnessCalculator {

    const val invalidIndexPoints = 40
    const val wallsPoints = 20
    const val cyclesPoints = 5
    const val hasNotFoundExitPoints = 200

    fun calculate(maze: Array<IntArray>, individual: Individual): Individual {
        val visitedCoordinates = HashSet<Pair<Int, Int>>()
        var points = 0

        for (c in individual.coordinates) {

            if (!Start.isValidCoordinate(maze, c)) {
                points += invalidIndexPoints
                continue
            }

            val isVisited = visitedCoordinates.contains(c)

            if (!isVisited) {
                visitedCoordinates.add(c)
            }

            var currentMazePosition = maze[c.first][c.second]

            val total = when {
                1 == currentMazePosition -> wallsPoints
                isVisited -> cyclesPoints
                else -> 0
            }

            points += total
        }

        val exitPosition = Start.searchPosition(maze, Start.endNumber)

        val hasFoundExit = individual.coordinates.any { it == exitPosition }

        if (!hasFoundExit)
            points += hasNotFoundExitPoints

        return individual.copy(score = points)
    }
}