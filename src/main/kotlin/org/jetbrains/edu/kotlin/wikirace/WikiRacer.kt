package org.jetbrains.edu.kotlin.wikirace

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
    fun getReferences(page: String): List<String>

    companion object {
        /**
         * @param maxThreads The maximum number of threads to use.
         */
        fun get(maxThreads: Int): WikiRacer = TODO()
    }
}
