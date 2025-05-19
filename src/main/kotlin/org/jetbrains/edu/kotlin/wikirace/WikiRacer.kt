package org.jetbrains.edu.kotlin.wikirace

import io.ktor.client.HttpClient
import io.ktor.client.request.request
import io.ktor.client.statement.bodyAsText
import io.ktor.util.collections.ConcurrentMap
import org.jsoup.Jsoup

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

interface WikiRacer {
    /**
     * @param startPage The starting page of the search.
     * @param destinationPage The destination page to be reached.
     * @param searchDepth The maximum depth of the search. If the destination page is not found within this depth, the
     * search should stop and return `WikiPath.NOT_FOUND`.
     */
    fun race(startPage: String, destinationPage: String, searchDepth: Int): WikiPath

    /**
     * Returns a list of all wikipedia articles references from the given page.
     * Remember to create the full link, e.g. `/wiki/JetBrains -> https://en.wikipedia.org/wiki/JetBrains`
     *
     * @param page Wikipedia page
     *
     * @see <a href="https://jsoup.org/cookbook/input/load-document-from-url">Jsoup documentation</a>
     * You can use `Jsoup.connect` to get a document
     * @see <a href="https://www.tabnine.com/code/java/methods/org.jsoup.nodes.Element/select">Example</a>
     * You can use `html.select` to find the references
     */
    suspend fun getReferences(page: String): List<String>

    companion object {
        /**
         * @param maxThreads The maximum number of threads to use.
         */
        fun get(maxThreads: Int): WikiRacer = AsyncRacer(maxThreads)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class AsyncRacer(maxThreads: Int) : WikiRacer {
    val dispatcher = Dispatchers.IO.limitedParallelism(maxThreads)
    val client = HttpClient()

    override fun race(
        startPage: String, destinationPage: String, searchDepth: Int
    ): WikiPath {
        val visitedPages = ConcurrentMap<String, String>()  // page -> predecessor

        runBlocking {
            var level = Channel<String>(Channel.UNLIMITED)

            withContext(dispatcher) {
                level.send(startPage)
                level.close()
                visitedPages.putIfAbsent(startPage, startPage)

                repeat(searchDepth) {
                    if (destinationPage in visitedPages) {
                        return@withContext
                    }

                    level = processLevel(level, destinationPage, visitedPages)
                    level.close()

                    println("Level $it: ${visitedPages.size} pages visited")
                }
            }
        }

        val path = mutableListOf<String>()
        var page = destinationPage
        while (page != startPage) {
            path.add(page)
            page = visitedPages[page] ?: return WikiPath.NOT_FOUND
        }
        path.add(startPage)
        return WikiPath(path.reversed())
    }

    private suspend fun processLevel(
        level: Channel<String>,
        destinationPage: String,
        visitedPages: ConcurrentMap<String, String>
    ): Channel<String> {
        val newLevel = Channel<String>(Channel.UNLIMITED)
        for (pages in level.toList().chunked(128)) {
            coroutineScope {
                for (page in pages) {
                    if (destinationPage in visitedPages) {
                        return@coroutineScope
                    }
                    launch {
                        val references = getReferences(page).filter { ref -> ref !in visitedPages }
                        for (reference in references) {
                            newLevel.send(reference)
                            visitedPages.putIfAbsent(reference, page)
                        }
                    }
                }
            }
        }
        return newLevel
    }

    override suspend fun getReferences(page: String): List<String> {
        val url = if (page.startsWith("https://")) page else "https://$page"

        val html = client.request(url).bodyAsText()
        val document = Jsoup.parse(html, url)
        return document.extractReferences()
    }
}
