fun main(args: Array<String>) {
    if (args.size != 2 ) {
        throw RuntimeException("Expected 2 arguments.")
    }
    val graph = generateGraph(args[0].toInt())
    saveGraph(graph, args[1])
}