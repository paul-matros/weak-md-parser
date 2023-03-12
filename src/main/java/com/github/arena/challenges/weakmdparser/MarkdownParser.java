package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {
    boolean activeList = false;

    String parse(String markdown) {
        String[] lines = markdown.split("\n");
        String result = "";


        for (String currentLine : lines) {

            currentLine = parseHeader(currentLine);
            currentLine = parseList(currentLine);
            if (!(isHeader(currentLine) || isList(currentLine))) {
                currentLine = parseParagraph(currentLine);
            }
            currentLine = parseFontStyles(currentLine);

            if (isList(currentLine) && !activeList) {
                activeList = true;
                result = result + "<ul>" ;}
            if (!isList(currentLine) && activeList) {
                activeList = false;
                result = result + "</ul>";
            }
            result = result + currentLine;
        }

        if (activeList) {
            result = result + "</ul>";
        }

        return result;
    }

    private boolean isList(String markdown) {
        return markdown.matches("(<li>).*");
    }

    private boolean isHeader(String markdown) {
        return markdown.matches("(<h).*");
    }

    protected String parseHeader(String markdown) {
        int count = 0;

        for (int i = 0; i < markdown.length() && markdown.charAt(i) == '#'; i++) {
            count++;
        }

        if (count == 0) {
            return markdown;
        }

        return "<h" + Integer.toString(count) + ">" + markdown.substring(count + 1) + "</h" + Integer.toString(count) + ">";
    }

    public String parseList(String markdown) {
        if (markdown.startsWith("*")) {
            String skipAsterisk = markdown.substring(2);
            return "<li>" + skipAsterisk + "</li>";
        }
        return markdown;
    }

    public String parseParagraph(String markdown) {
        return "<p>" + markdown + "</p>";
    }

    public String parseFontStyles(String markdown) {
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
}
