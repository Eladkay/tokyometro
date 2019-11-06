package eladkay.tokyometro

import java.io.File
import java.util.*

class LineStation(val name: String, var number: Int = -1, val distance: Double = -1.0) {
    override fun toString(): String {
        return name
    }
}

class Line(val name: String, val character: Char, val stations: MutableList<LineStation> = mutableListOf()) {
    override fun toString(): String {
        return buildString {
            append("\n$name\n")
            for((i, station) in stations.withIndex()) append("       ${station.number}. ${station.name}\n")
            append("\n")
        }
    }
}
val lines = mutableListOf<Line>()
fun main(args: Array<String>) {
    File("lines.met").readLines().map { it.split(" ") }.mapTo(lines) { Line(it[1], it[0].toCharArray()[0]) }
    File("stations.met").readLines().map { it.split(" ") }.forEach {
        for(c in it[1]) lines.firstOrNull { it.character == c }?.stations?.add(LineStation(it[0])) ?: throw Exception(c.toString())
    }
    readWriteLineSequence()
    //println(lines)
}

fun readWriteLineSequence() {
    val scanner = Scanner(System.`in`)
    for(line in lines) {
        val file = File("lines/${line.name}.met")
        if(file.exists()) {
            print("hmm")
            val stations = file.readLines()
            for((i, station) in stations.withIndex()) {
                println("$i $station")
                line.stations.first { it.name.toLowerCase().trim() == station.toLowerCase().trim() }.number = i+1
            }
            line.stations.sortBy { it.number }
            continue
        }
        val symbol = line.character.toUpperCase()
        var i = 1
        fun getSymbol() = "$symbol${if(i > 9) i.toString() else "0$i"}"
        val size = line.stations.size
        val stationList = mutableListOf<LineStation>()
        while(i <= size) {
            inner@while(true) {
                print(getSymbol() + "? ")
                val stName = scanner.nextLine()
                val stations = line.stations.filter { it.name.toLowerCase().startsWith(stName.toLowerCase().replace("$", "")) }
                if (stations.size == 1) {
                    stationList.add(LineStation(stations[0].name, i))
                    break@inner
                } else {
                    if(stName.endsWith("$")) {
                        stationList.add(LineStation(stations.firstOrNull { it.name.toLowerCase() == stName.toLowerCase().replace("$", "") }?.name ?: throw Exception(stations.toString()), i))
                        break@inner
                    }
                    println("more? ")
                }
            }
            i++
        }

        file.createNewFile()
        file.writeText(buildString {
            stationList.forEach { append("${it.name}\n") }
        })
    }
}