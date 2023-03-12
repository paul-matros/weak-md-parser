package com.github.arena.challenges.weakmdparser;

public class MarkdownParser {
    boolean activeList = false;
    String parse(String markdown) {
        String[] lines = markdown.split("\n");
        String result = "";


        for (String currentLine:lines) {

            String theLine = parseHeader(currentLine);

            if (theLine == null) {
                theLine = parseLine(currentLine);
            }

            if (theLine == null) {
                theLine = parseParagraph(currentLine);
            }

            if (theLine.matches("(<li>).*")
                 /*   && !theLine.matches("(<h).*")
                    && !theLine.matches("(<p>).*")*/
                    && !activeList) {
                activeList = true;
                result = result + "<ul>";
                result = result + theLine;
            } else if (!theLine.matches("(<li>).*") && activeList) {
                activeList = false;
                result = result + "</ul>";
                result = result + theLine;
            } else {
                result = result + theLine;
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
            return null;
        }

        return "<h" + Integer.toString(count) + ">" + markdown.substring(count + 1) + "</h" + Integer.toString(count) + ">";
    }

    public String parseLine(String markdown) {
        if (markdown.startsWith("*")) {
            String skipAsterisk = markdown.substring(2);
            String listItemString = parseFontStyles(skipAsterisk);
            return "<li>" + listItemString + "</li>";
        }

        return null;
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
