import java.io.Serializable

data class Edge(val data: Int, val distance: Int) : Comparable<Edge>, Serializable {
    override fun compareTo(other: Edge) = this.distance.compareTo(other.distance)
    override fun toString() = "($data, $distance)"
}

infix fun Int.dist(weight: Int) = Edge(this, weight)

typealias WeightedGraph = HashMap<Int, Array<Edge>>
