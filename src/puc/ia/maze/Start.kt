package puc.ia.maze

import java.util.*
import kotlin.collections.ArrayList

object Start {

    const val populationSize = 5000
    const val numberOfIterations = 200000
    const val mutationSize = 1000

    const val freeNumber = 0
    const val startNumber = 2
    const val endNumber = 3

    const val up = 0
    const val down = 1
    const val left = 2
    const val right = 3

    @JvmStatic
    fun main(args: Array<String>) {

        println("Digite o modo: 1 - lento \\ 2 - rapido")

        val mode = Scanner(System.`in`).nextInt()

        val maze: Array<IntArray> = MazeLoader.load()

        val chromosomeSize = calculateChromosomeSize(maze)

        var population = PopulationLoader.loadFirstPopulation(populationSize, chromosomeSize)

        val startPosition = searchPosition(maze, startNumber)
        val endPosition = searchPosition(maze, endNumber)

        var solution: Individual?

        var cont = 0

        for (i in 0 until numberOfIterations) {
            val scoredPopulation = movementAgent(maze, population, startPosition, endPosition)

            solution = scoredPopulation.firstOrNull { it.score == 0 }

            if (solution != null) {
                println(solution)
                break
            }

            var best = scoredPopulation.minByOrNull { it.score }!!

            if (1 == mode) {
                println("actual score: ${best.score}")
                println("way: $solution")
            } else {
                if (cont == 10) {
                    println("way: $solution")
                    println("actual score: ${best.score}")
                    cont = 0
                }
            }

            cont++

            if (1 == mode) {
                println("crossover")
            }

            population = PopulationLoader.crossover(scoredPopulation, populationSize, best)

            population = movementAgent(maze, population, startPosition, endPosition)

            if (i % 2 == 0) {
                if (1 == mode) {
                    println("mutating")
                }
                population = PopulationLoader.mutate(maze, population, mutationSize)
            }
        }


    }

    private fun movementAgent(
        maze: Array<IntArray>,
        population: ArrayList<Individual>,
        startPosition: Pair<Int, Int>,
        endPosition: Pair<Int, Int>
    ): ArrayList<Individual> {

        var scoredPopulation = ArrayList<Individual>()

        for (i in 0 until population.size) {
            var actualCoordinate = startPosition
            var coordinates = ArrayList<Pair<Int, Int>>()
            coordinates.add(actualCoordinate)

            for (j in 1 until population[i].chromossomes.size) {
                actualCoordinate = move(population[i].chromossomes[j], actualCoordinate)
                coordinates.add(actualCoordinate)

                //encontrou a saida
                if (actualCoordinate == endPosition)
                    break
            }

            scoredPopulation.add(fitnessScore(maze, population[i].copy(coordinates = coordinates)))
        }

        return scoredPopulation;
    }

    private fun fitnessScore(maze: Array<IntArray>, individual: Individual): Individual =
        FitnessCalculator.calculate(maze, individual)

    fun isValidCoordinate(maze: Array<IntArray>, coordinate: Pair<Int, Int>) =
        (coordinate.first >= 0 && coordinate.first <= maze.size - 1) &&
                (coordinate.second >= 0 && coordinate.second <= maze.size - 1)

    fun move(direction: Int, actualCoordinate: Pair<Int, Int>): Pair<Int, Int> =
        when (direction) {
            up -> actualCoordinate.copy(first = actualCoordinate.first.dec())
            down -> actualCoordinate.copy(first = actualCoordinate.first.inc())
            left -> actualCoordinate.copy(second = actualCoordinate.second.dec())
            right -> actualCoordinate.copy(second = actualCoordinate.second.inc())
            else -> actualCoordinate
        }

    private fun calculateChromosomeSize(maze: Array<IntArray>): Int =
        maze.sumOf { it.count { e -> e == freeNumber || e == startNumber || e == endNumber } }

    fun searchPosition(maze: Array<IntArray>, desiredPosition: Int): Pair<Int, Int> =
        maze.mapIndexed { i, ints ->
            val j = ints.indexOfFirst { it == desiredPosition }
            Pair(i, j)
        }.first { it.second != -1 }


}