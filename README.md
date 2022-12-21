# Parallel & Concurrent Test

## Task description

You may have heard about the game “Wikipedia race,” where you click on links in Wikipedia articles with the goal of
getting from one article to another in as few clicks as possible.
Playing the game is fun – and it’s even more fun when you win!
In this task, you need to programmatically find a path from a start page to a destination page as fast as possible.

## Implementation Details

Your solution should be a console app with the following arguments:

- `search-depth` – How many transitions from the start page are allowed.
  Remember to check if the depth is > 0 and inform the user if the input is incorrect.
- Optional `max-threads` – How many threads to use, the default value is one.
  Remember to check if the number of threads is > 0 and inform the user if the input is incorrect.
- Optional `start` and `final` – If provided, then the solution should start searching from the Wikipedia page with the
  given title, otherwise it should start from any Wikipedia article.
  For example, if `AVL tree` is given as the `start` argument,
  your search should start from `https://en.wikipedia.org/wiki/AVL_tree`.
  The same applies to the `final` argument.
  **OR** your application can allow for multiple races, and wait for user input to get `start` and `final`.

The output of the app is a sequence of pages in the found path, or a message saying that no path was found within the
search depth.
Consider displaying the progress so that the user knows that the application has not frozen.

Feel free to use:

- [Clikt](https://ajalt.github.io/clikt/) or any other library to handle the command-line arguments
- [Jsoup](https://jsoup.org/),
  or [Ktor](https://ktor.io/docs/request.html) + [kotlin serialization](https://github.com/Kotlin/kotlinx.serialization),
  or any other library to get and parse pages

You can work with Wikipedia as is, or via
their [API](https://en.wikipedia.org/wiki/Special:ApiSandbox#action=parse&format=json&page=Pet_door&prop=text&disabletoc=1&formatversion=2).

To extract links from a Wiki page, you need to extract all internal (referring to any Wiki page) links.
For `Jsoup` it can be achieved via `html.select("[href^=/wiki/]").map { it.attr("href") }`.
You have to exclude the page itself, the main wiki page, and non-articles, which include special prefixes:

```kotlin
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
    "Talk:"
)
```

Read more in the javadoc of [WikiRacer](./src/main/kotlin/org/jetbrains/edu/kotlin/wikirace/WikiRacer.kt) interface,
which you have to implement.

### Grades

25% for passing tests, even if the implementation is synchronous.

50% for a multithreaded version. You can use either threads or coroutines.

25% for CLI.

**If you have two red builds, you will receive half of the available points.**

**NOTE**: Wikipedia pages get updated more frequently than tests to this task,
so [line 24 here](./src/test/kotlin/ParsingTest.kt) might be wrong.

* To run all tests locally: `./gradlew test`

* To run Detekt locally: `./gradlew detekt`

* To run Diktat locally: `./gradlew diktat`
