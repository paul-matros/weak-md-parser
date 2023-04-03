package com.github.arena.challenges.weakmdparser;

import java.util.Iterator;

public class MarkdownIterator implements Iterator {
    private final String[] lines;
    private boolean activeList;
    private int currentLine;

    public MarkdownIterator(String parsedString) {
        this.lines = parsedString.split("\n");
        this.activeList = false;
        this.currentLine = -1;
    }

    public MarkdownIterator(String lines[]) {
        this.lines = lines;
        this.activeList = false;
        this.currentLine = -1;
    }

    @Override
    public boolean hasNext() {
        return this.currentLine < this.lines.length - 1;
    }

    @Override
    public Object next() {
        this.currentLine += 1;
        return parseLine(lines[currentLine]);
    }

    private String parseLine(String parsedLine) {
        parsedLine = parseHeader(parsedLine);
        parsedLine = parseList(parsedLine);
        parsedLine = parseParagraph(parsedLine);
        parsedLine = parseListContainer(parsedLine);
        return parseFontStyles(parsedLine);
    }

    private String parseHeader(String parsedLine) {
        int hashCount = countLeadingChars(parsedLine, '#');
        if (hashCount == 0) {
            return parsedLine;
        }
        String skipHashes = parsedLine.substring(hashCount + 1);
        return "<h" + hashCount + ">" + skipHashes + "</h" + hashCount + ">";
    }

    private String parseList(String parsedLine) {
        if (parsedLine.startsWith("*")) {
            String skipAsterisk = parsedLine.substring(2);
            return "<li>" + skipAsterisk + "</li>";
        }
        return parsedLine;
    }

    private String parseParagraph(String parsedLine) {
        if (!(isHeader(parsedLine) || isList(parsedLine)))
            return "<p>" + parsedLine + "</p>";
        return parsedLine;
    }

    private String parseListContainer(String parsedLine) {
        if (!activeList) {
            if (isList(parsedLine)) {
                activeList = true;
                return "<ul>" + parsedLine;
            }
        } else {
            activeList = false;
            return parsedLine + "</ul>";
        }
        return parsedLine;
    }

    private String parseFontStyles(String parsedLine) {
        return parseItalic(parseBold(parsedLine));
    }

    private String parseBold(String parsedLine) {
        String boldFontRegEx = "__(.+)__";
        String taggedWithStrong = "<strong>$1</strong>";
        return parsedLine.replaceAll(boldFontRegEx, taggedWithStrong);
    }

    private String parseItalic(String parsedLine) {
        String italicFontRegEx = "_(.+)_";
        String taggedWithEm = "<em>$1</em>";
        return parsedLine.replaceAll(italicFontRegEx, taggedWithEm);
    }

    private int countLeadingChars(String string, char character) {
        int count = 0;
        for (int i = 0; i < string.length() && string.charAt(i) == character; i++) {
            count++;
        }
        return count;
    }

    private boolean isList(String parsedLine) {
        return parsedLine.matches("(<li>).*");
    }

    private boolean isHeader(String parsedLine) {
        return parsedLine.matches("(<h).*");
    }
}
