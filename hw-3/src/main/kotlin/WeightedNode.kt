import java.io.Serializable

data class Edge(val data: String, val distance: Int) : Comparable<Edge>, Serializable {
    override fun compareTo(other: Edge) = this.distance.compareTo(other.distance)
    override fun toString() = "($data, $distance)"
}

infix fun String.dist(weight: Int) = Edge(this, weight)

typealias WeightedGraph = HashMap<String, Array<Edge>>
