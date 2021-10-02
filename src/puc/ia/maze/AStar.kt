package puc.ia.maze

import java.util.*
import kotlin.math.abs


class Node(coordinate: Pair<Int, Int>) : AStar(), Comparable<Node> {
    var costToStart = 0
    private var costToEnd = 0
    var coordinate: Pair<Int, Int>
    var previousNode: Node?
    private val cost: Int
        get() = costToStart + costToEnd

    fun setCostToEnd(costToEnd: Int) {
        this.costToEnd = costToEnd
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val node = o as Node
        return coordinate == node.coordinate
    }

    override fun hashCode(): Int {
        return Objects.hash(coordinate)
    }

    override fun toString(): String {
        return coordinate.toString()
    }

    override operator fun compareTo(node: Node): Int {
        return node.cost - cost
    }

    init {
        this.coordinate = coordinate
        previousNode = null
    }
}

object Star {
    @JvmStatic
    fun main(args: Array<String>) {

        val maze: Array<IntArray> = MazeLoader.load()

        val aStar = AStar()

        aStar.findPath(maze)
            .forEach { println("${it.coordinate.first} | ${it.coordinate.second}") }
    }
}

open class AStar {

    private val startCoordinate: Pair<Int, Int> = Pair(0, 0)
    private val finalCoordinate: Pair<Int, Int> = Pair(11, 11)
    private val availableNodes = ArrayList<Node>()
    private val visitedNodes = ArrayList<Node>()

    fun findPath(maze: Array<IntArray>): ArrayList<Node> {
        val startPosition = Start.searchPosition(maze, Start.startNumber)
        val endPosition = Start.searchPosition(maze, Start.endNumber)

        val startNode = Node(startPosition)
        val finalNode = Node(endPosition)

        startNode.costToStart = getCostToStart(startCoordinate)
        startNode.setCostToEnd(getCostToEnd(startCoordinate))
        finalNode.costToStart = getCostToStart(finalCoordinate)
        finalNode.setCostToEnd(getCostToEnd(finalCoordinate))

        var currentNode = startNode
        availableNodes.add(startNode)

        while (availableNodes.isNotEmpty()) {
            availableNodes.sort()
            currentNode = availableNodes[0]

            if (currentNode === finalNode) {
                break
            }

            availableNodes.remove(currentNode)
            visitedNodes.add(currentNode)

            val neighbourNodes = getNodeNeighbours(currentNode, 12)

            for (node in neighbourNodes) {
                if (maze[node.coordinate.first][node.coordinate.second] == 1 || visitedNodes.contains(node))
                    continue
                if (availableNodes.contains(node)) {
                    val newCostToStart = currentNode.costToStart + getCostToStart(node.coordinate)
                    if (newCostToStart < node.costToStart) {
                        node.previousNode = currentNode
                        node.costToStart = newCostToStart
                        node.setCostToEnd(getCostToEnd(node.coordinate))
                    }
                } else {
                    val costToEnd = getCostToEnd(node.coordinate)
                    val costToStart = getCostToStart(node.coordinate)
                    node.setCostToEnd(costToEnd)
                    node.costToStart = costToStart
                    node.previousNode = currentNode
                    availableNodes.add(node)
                }
            }
        }
        return getFinalPath(currentNode)
    }

    fun getFinalPath(currentNode: Node): ArrayList<Node> {
        var currentNode = currentNode
        val finalPath = ArrayList<Node>()
        finalPath.add(currentNode)
        while (currentNode.previousNode != null) {
            finalPath.add(currentNode.previousNode!!)
            currentNode = currentNode.previousNode!!
        }
        finalPath.reverse()
        return finalPath
    }

    private fun getNodeCost(originCoordinate: Pair<Int, Int>, destinyCoordinate: Pair<Int, Int>): Int {
        val distanceX: Int = abs(destinyCoordinate.first - originCoordinate.first)
        val distanceY: Int = abs(destinyCoordinate.second - originCoordinate.second)
        return (distanceX + distanceY) * 10
    }

    private fun getCostToStart(originCoordinate: Pair<Int, Int>): Int {
        return getNodeCost(originCoordinate, startCoordinate)
    }

    private fun getCostToEnd(originCoordinate: Pair<Int, Int>): Int {
        return getNodeCost(originCoordinate, finalCoordinate)
    }

    fun getNodeNeighbours(node: Node, mazeSize: Int): ArrayList<Node> {
        var neighbourX: Int
        var neighbourY: Int
        val neighbours = ArrayList<Node>()
        val directions = listOf(0,1,2,3)
        for (direction in directions) {
            neighbourX = node.coordinate.first
            neighbourY = node.coordinate.second
            when (direction) {
                0 -> {
                    neighbourX--
                    if (neighbourX > 0) {
                        neighbours.add(Node(Pair(neighbourX, neighbourY)))
                    }
                }
                1 -> {
                    neighbourX++
                    if (neighbourX < mazeSize) {
                        neighbours.add(Node(Pair(neighbourX, neighbourY)))
                    }
                }
                2 -> {
                    neighbourY--
                    if (neighbourY > 0) {
                        neighbours.add(Node(Pair(neighbourX, neighbourY)))
                    }
                }
                3 -> {
                    neighbourY++
                    if (neighbourY < mazeSize) {
                        neighbours.add(Node(Pair(neighbourX, neighbourY)))
                    }
                }
            }
        }
        return neighbours
    }
}
