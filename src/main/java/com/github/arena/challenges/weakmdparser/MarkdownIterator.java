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
        return parseCurrentLine();
    }

    private String parseCurrentLine() {
        lines[currentLine] = parseHeader();
        lines[currentLine] = parseList();
        lines[currentLine] = parseParagraph();
        lines[currentLine] = parseListContainer();
        lines[currentLine] = parseFontStyles();
        return lines[currentLine];
    }

    private String parseHeader() {
        String parsedLine = lines[currentLine];
        int hashCount = countLeadingChars(parsedLine, '#');
        if (hashCount == 0) {
            return parsedLine;
        }
        String skipHashes = parsedLine.substring(hashCount + 1);
        return "<h" + hashCount + ">" + skipHashes + "</h" + hashCount + ">";
    }

    private String parseList() {
        String parsedLine = lines[currentLine];
        if (parsedLine.startsWith("*")) {
            String skipAsterisk = parsedLine.substring(2);
            return "<li>" + skipAsterisk + "</li>";
        }
        return parsedLine;
    }

    private String parseParagraph() {
        String parsedLine = lines[currentLine];
        if (!(isHeader(parsedLine) || isList(parsedLine)))
            return "<p>" + parsedLine + "</p>";
        return parsedLine;
    }

    private String parseListContainer() {
        String parsedLine = lines[currentLine];
        if (isList(parsedLine)){
            if (!activeList){
                activeList = true;
                parsedLine = "<ul>" + parsedLine;
            }
            if (hasNext()) {
                if (!isList(lines[currentLine+1])) {
                    parsedLine = parsedLine + "</ul>";
                }
            }else{
                parsedLine = parsedLine + "</ul>";
            }
        } else{
            activeList = false;
        }
        return parsedLine;
    }

    private String parseFontStyles() {
        lines[currentLine] = parseBold();
        lines[currentLine] = parseItalic();
        return lines[currentLine];
    }

    private String parseBold() {
        String parsedLine = lines[currentLine];
        String boldFontRegEx = "__(.+)__";
        String taggedWithStrong = "<strong>$1</strong>";
        return parsedLine.replaceAll(boldFontRegEx, taggedWithStrong);
    }

    private String parseItalic() {
        String parsedLine = lines[currentLine];
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
        return parsedLine.matches("(<li>).*") || parsedLine.matches("(\\*).*");
    }

    private boolean isHeader(String parsedLine) {
        return parsedLine.matches("(<h).*");
    }
}
