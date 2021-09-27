package puc.ia.maze

import java.util.*
import kotlin.collections.ArrayList

private const val initialScore = 0

object PopulationLoader {

    private val rand = Random()
    private const val totalDirections = 4

    fun loadFirstPopulation(populationSize: Int, chromosomeSize: Int): ArrayList<Individual> {

        val individuals = ArrayList<Individual>()

        for (i in 0 until populationSize) {

            val movements = ArrayList<Int>()

            for (j in 0 until chromosomeSize) {
                movements.add(rand.nextInt(totalDirections))
            }

            individuals.add(Individual(movements, ArrayList(), 0))

        }

        return individuals
    }

    fun mutate(maze: Array<IntArray>, population: ArrayList<Individual>, size: Int): ArrayList<Individual> {
        for (i in 1 until size) {
            for (j in 1 until population[i].coordinates.size) {
                var actualPosition = population[i].coordinates[j]

                if (!isValidMovement(maze, actualPosition, population[i].coordinates, j)) {
                    val movement = rand.nextInt(4)
                    //println("$j | $movement")
                    population[i].chromossomes[j] = movement
                }
            }

            //population[i].coordinates.forEachIndexed { a,b -> println("$a [x: ${b.first}, y: ${b.second}] |") }

            population[i] =
                population[i].copy(coordinates = arrayListOf(), score = 99999)

        }

        return population
    }

    private fun isValidMovement(
        maze: Array<IntArray>,
        position: Pair<Int, Int>,
        coordinates: ArrayList<Pair<Int, Int>>,
        index: Int
    ): Boolean {
        val isInvalidCoordinate = !Start.isValidCoordinate(maze, position)
        val isWall = kotlin.runCatching {
            maze[position.first][position.second] !in arrayOf(
                Start.freeNumber,
                Start.startNumber,
                Start.endNumber
            )
        }.getOrElse { false }

        val willFormCycle = coordinates
            .filterIndexed { i, pair -> i < index && pair == position }
            .isNotEmpty()


        return !isInvalidCoordinate && !isWall && !willFormCycle
    }

    fun crossover(population: ArrayList<Individual>, populationSize: Int, best: Individual): ArrayList<Individual> {
        val intermediatePopulation = ArrayList<Individual>()
        intermediatePopulation.add(best)

        for (i in 0 until (populationSize / 2) - 1) {
            val parents = generateRandomParents(population)

            val child1 = Individual(chromossomes = ArrayList(), coordinates = ArrayList())
            val child2 = Individual(chromossomes = ArrayList(), coordinates = ArrayList())

            intermediatePopulation.addAll(generateChildren(population, parents, child1, child2))
        }

        return intermediatePopulation
    }

    private fun generateChildren(
        population: ArrayList<Individual>,
        parents: Pair<Int, Int>,
        child1: Individual,
        child2: Individual
    ): ArrayList<Individual> {
        val chromosomeSize = population[parents.first].chromossomes.size

        for (j in 0 until chromosomeSize) {
            val maskBit = rand.nextInt(2)

            if (maskBit == 0) {
                child1.chromossomes.add(population[parents.second].chromossomes[j])
                child2.chromossomes.add(population[parents.first].chromossomes[j])
            } else {
                child1.chromossomes.add(population[parents.first].chromossomes[j])
                child2.chromossomes.add(population[parents.second].chromossomes[j])
            }
        }

        return arrayListOf(child1, child2)
    }

    private fun generateRandomParents(population: ArrayList<Individual>): Pair<Int, Int> {
        val father = tournament(population)
        var mother = tournament(population)

        while (father == mother) {
            mother = tournament(population)
        }

        return Pair(father, mother)
    }

    private fun tournament(population: ArrayList<Individual>): Int {
        val rand1 = rand.nextInt(population.size)
        var rand2 = rand.nextInt(population.size)

        while (rand1 == rand2) {
            rand2 = rand.nextInt(population.size)
        }

        return when {
            population[rand1].score < population[rand2].score -> rand1
            else -> rand2
        }
    }

}

data class Individual(
    var chromossomes: ArrayList<Int>,
    var coordinates: ArrayList<Pair<Int, Int>>,
    var score: Int = 999999
)