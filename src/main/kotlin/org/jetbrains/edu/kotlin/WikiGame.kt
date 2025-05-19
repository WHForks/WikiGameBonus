package org.jetbrains.edu.kotlin

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.required
import com.github.ajalt.clikt.parameters.types.int
import org.jetbrains.edu.kotlin.wikirace.WikiPath
import org.jetbrains.edu.kotlin.wikirace.WikiRacer

const val WIKI_PREFIX = "https://en.wikipedia.org/wiki/"

class WikiGame : CliktCommand() {
    val start: String by option("-s", "--start", help = "Start page").required()
    val finish: String by option("-f", "--finish", help = "Finish page").required()
    val searchDepth: Int by option("-d", "--depth", help = "Search depth").int().default(4)
    val threads: Int by option("-t", "--threads", help = "Number of threads").int().default(8)

    override fun run() {
        val wikiRacer = WikiRacer.get(threads)
        val path = wikiRacer.race(WIKI_PREFIX + start, WIKI_PREFIX + finish, searchDepth)
        if (path == WikiPath.NOT_FOUND) {
            echo("No path found")
        } else {
            echo("Path found: ${path.path.joinToString(" -> ")}")
            echo("Path length: ${path.steps}")
        }
    }
}
