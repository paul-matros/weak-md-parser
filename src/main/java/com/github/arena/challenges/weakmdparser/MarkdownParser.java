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

    private String parseHeader(String parsedLine) {
        int count = 0;

        for (int i = 0; i < parsedLine.length() && parsedLine.charAt(i) == '#'; i++) {
            count++;
        }

        if (count == 0) {
            return parsedLine;
        }

        return "<h" + count + ">" + parsedLine.substring(count + 1) + "</h" + count + ">";
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

    private String parseFontStyles(String parsedLine) {
        String result = parseBold(parsedLine);
        return parseItalic(result);
    }

    private String parseBold(String parsedLine) {
        String lookingFor = "__(.+)__";
        String update = "<strong>$1</strong>";
        return parsedLine.replaceAll(lookingFor, update);
    }

    private String parseItalic(String parsedLine) {
        String lookingFor = "_(.+)_";
        String update = "<em>$1</em>";
        return parsedLine.replaceAll(lookingFor, update);
    }

    private boolean isList(String parsedLine) {
        return parsedLine.matches("(<li>).*");
    }

    private boolean isHeader(String parsedLine) {
        return parsedLine.matches("(<h).*");
    }
}
