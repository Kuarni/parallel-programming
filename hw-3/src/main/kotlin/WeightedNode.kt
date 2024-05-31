data class Edge(val data: String, val distance: Int) : Comparable<Edge> {
    override fun compareTo(other: Edge) = this.distance.compareTo(other.distance)
}

infix fun String.dist(weight: Int) = Edge(this, weight)

typealias WeightedGraph = HashMap<String, Array<Edge>>
