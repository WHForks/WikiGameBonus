package org.jetbrains.edu.kotlin.wikirace

import org.jsoup.nodes.Document

val forbiddenPrefixes = listOf(
    "File:",
    "Wikipedia:",
    "Help:",
    "Template:",
    "Category:",
    "Special:",
    "Portal:",
    "User:",
    "MediaWiki:",
    "Draft:",
    "TimedText:",
    "Module:",
    "Media:",
    "Template_talk:",
    "Talk:",
    "Main_Page"
)

fun Document.extractReferences(): List<String> = select("[href^=/wiki/]")
    .map { it.attr("abs:href") }
    .filter { link -> forbiddenPrefixes.all { !link.contains(it) } }
    .distinct()
