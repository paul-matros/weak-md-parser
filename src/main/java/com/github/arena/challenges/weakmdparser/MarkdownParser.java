package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {
    boolean activeList = false;
    String parse(String markdown) {
        String[] lines = markdown.split("\n");
        String result = "";


        for (String currentLine:lines) {

            currentLine = parseHeader(currentLine);
            currentLine = parseList(currentLine);
            if (
                    !(currentLine.matches("(<li>).*"))
                            &&
                            !(currentLine.matches("(<h).*"))
            )
            currentLine = parseParagraph(currentLine);


            if (currentLine.matches("(<li>).*")
                 /*   && !theLine.matches("(<h).*")
                    && !theLine.matches("(<p>).*")*/
                    && !activeList) {
                activeList = true;
                result = result + "<ul>";
                result = result + currentLine;
            } else if (!currentLine.matches("(<li>).*") && activeList) {
                activeList = false;
                result = result + "</ul>";
                result = result + currentLine;
            } else {
                result = result + currentLine;
            }
        }

        if (activeList) {
            result = result + "</ul>";
        }

        return result;
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
            String listItemString = parseFontStyles(skipAsterisk);
            return "<li>" + listItemString + "</li>";
        }

        return markdown;
    }

    public String parseParagraph(String markdown) {
        return "<p>" + parseFontStyles(markdown) + "</p>";
    }

    public String parseFontStyles(String markdown) {
        String result = parseBold(markdown);
        return parseItalic(result);
    }
    private String parseBold(String markdown){
        String lookingFor = "__(.+)__";
        String update = "<strong>$1</strong>";
        return markdown.replaceAll(lookingFor, update);
    }
    private String parseItalic(String markdown){
        String lookingFor = "_(.+)_";
        String update = "<em>$1</em>";
        return markdown.replaceAll(lookingFor, update);
    }
}
