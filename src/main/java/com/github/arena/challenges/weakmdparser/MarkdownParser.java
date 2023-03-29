package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {
    private boolean activeList = false;

    String parse(String parsedString) {
        StringBuilder result = new StringBuilder();
        MarkdownIterator iterator = new MarkdownIterator(parsedString);
        while (iterator.hasNext()){
            result.append(iterator.next());
        }
        return result.toString();
    }
}
