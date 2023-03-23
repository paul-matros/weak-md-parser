package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {
    private boolean activeList = false;

    String parse(String parsedString) {
        String[] lines = parsedString.split("\n");
        StringBuilder result = new StringBuilder();
        for (String currentLine : lines) {
            result.append(parseLine(currentLine));
        }
        return result.toString();
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

    private int countLeadingChars(String string, char character){
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
