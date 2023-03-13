package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {
    private boolean activeList = false;

    String parse(String markdown) {
        String[] lines = markdown.split("\n");
        StringBuilder result = new StringBuilder();
        for (String currentLine : lines) {
            result.append(parseLine(currentLine));
        }
        return result.toString();
    }

    private String parseLine(String currentLine) {
        currentLine = parseHeader(currentLine);
        currentLine = parseList(currentLine);
        currentLine = parseParagraph(currentLine);
        currentLine = parseListContainer(currentLine);
        return parseFontStyles(currentLine);
    }

    private String parseListContainer(String currentLine) {
        if (!activeList) {
            if (isList(currentLine)) {
                activeList = true;
                return "<ul>" + currentLine;
            }
        } else {
            activeList = false;
            return currentLine + "</ul>";
        }
        return currentLine;
    }

    private String parseHeader(String markdown) {
        int count = 0;

        for (int i = 0; i < markdown.length() && markdown.charAt(i) == '#'; i++) {
            count++;
        }

        if (count == 0) {
            return markdown;
        }

        return "<h" + count + ">" + markdown.substring(count + 1) + "</h" + count + ">";
    }

    private String parseList(String markdown) {
        if (markdown.startsWith("*")) {
            String skipAsterisk = markdown.substring(2);
            return "<li>" + skipAsterisk + "</li>";
        }
        return markdown;
    }

    private String parseParagraph(String markdown) {
        if (!(isHeader(markdown) || isList(markdown)))
            return "<p>" + markdown + "</p>";
        return markdown;
    }

    private String parseFontStyles(String markdown) {
        String result = parseBold(markdown);
        return parseItalic(result);
    }

    private String parseBold(String markdown) {
        String lookingFor = "__(.+)__";
        String update = "<strong>$1</strong>";
        return markdown.replaceAll(lookingFor, update);
    }

    private String parseItalic(String markdown) {
        String lookingFor = "_(.+)_";
        String update = "<em>$1</em>";
        return markdown.replaceAll(lookingFor, update);
    }

    private boolean isList(String markdown) {
        return markdown.matches("(<li>).*");
    }

    private boolean isHeader(String markdown) {
        return markdown.matches("(<h).*");
    }
}
