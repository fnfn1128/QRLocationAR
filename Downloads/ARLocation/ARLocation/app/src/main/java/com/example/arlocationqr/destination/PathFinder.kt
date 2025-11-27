package com.example.arlocationqr.destination

import com.example.arlocationqr.entity.Location
import com.example.arlocationqr.entity.PathEdge
import kotlin.math.min

class PathFinder(
    private val locations: Map<String, Location>,
    private val edges: List<PathEdge>
) {

    fun findShortestPath(startId: String, endId: String): List<Location> {

        val graph = mutableMapOf<String, MutableList<Pair<String, Double>>>()

        for (edge in edges) {
            graph.computeIfAbsent(edge.fromId) { mutableListOf() }
                .add(edge.toId to edge.distance)

            graph.computeIfAbsent(edge.toId) { mutableListOf() }
                .add(edge.fromId to edge.distance)
        }

        val dist = locations.keys.associateWith { Double.MAX_VALUE }.toMutableMap()
        val prev = mutableMapOf<String, String?>()
        val visited = mutableSetOf<String>()

        dist[startId] = 0.0

        while (visited.size < locations.size) {

            val current = dist
                .filter { it.key !in visited }
                .minByOrNull { it.value }?.key ?: break

            if (current == endId) break

            visited.add(current)

            val neighbors = graph[current] ?: continue
            for ((next, cost) in neighbors) {
                val newDist = dist[current]!! + cost
                if (newDist < dist[next]!!) {
                    dist[next] = newDist
                    prev[next] = current
                }
            }
        }

        val path = mutableListOf<String>()
        var curr: String? = endId
        while (curr != null) {
            path.add(curr)
            curr = prev[curr]
        }

        return path.reversed().mapNotNull { locations[it] }
    }
}
