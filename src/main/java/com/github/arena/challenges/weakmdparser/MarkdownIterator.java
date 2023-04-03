package com.github.arena.challenges.weakmdparser;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class MarkdownIterator implements Iterator<String> {
    private final String[] lines;
    private boolean activeList;
    private int currentLine;

    public MarkdownIterator(String parsedString) {
        this.lines = parsedString.split("\n");
        this.activeList = false;
        this.currentLine = -1;
    }

    public MarkdownIterator(String[] lines) {
        this.lines = lines;
        this.activeList = false;
        this.currentLine = -1;
    }

    @Override
    public boolean hasNext() {
        return this.currentLine < this.lines.length - 1;
    }

    @Override
    public String next() throws NoSuchElementException{
        if (!hasNext()){
            throw new NoSuchElementException();
        }
        this.currentLine += 1;
        parseCurrentLine();
        return lines[currentLine];
    }

    private void parseCurrentLine() {
        parseHeader();
        parseList();
        parseParagraph();
        parseListContainer();
        parseFontStyles();
    }

    private void parseHeader() {
        String parsedLine = lines[currentLine];
        int hashCount = countLeadingChars(lines[currentLine], '#');
        if (hashCount == 0) {
            return;
        }
        String skipHashes = parsedLine.substring(hashCount + 1);
        lines[currentLine] = "<h" + hashCount + ">" + skipHashes + "</h" + hashCount + ">";
    }

    private void parseList() {
        String parsedLine = lines[currentLine];
        if (parsedLine.startsWith("*")) {
            String skipAsterisk = parsedLine.substring(2);
            lines[currentLine] = "<li>" + skipAsterisk + "</li>";
        }
    }

    private void parseParagraph() {
        String parsedLine = lines[currentLine];
        if (!(isHeader(parsedLine) || isList(parsedLine))){
            lines[currentLine] = "<p>" + parsedLine + "</p>";
        }
    }

    private void parseListContainer() {
        String parsedLine = lines[currentLine];
        if (isList(parsedLine)){
            if (!activeList){
                activeList = true;
                lines[currentLine] = "<ul>" + parsedLine;
            }
            if (hasNext()) {
                if (!isList(lines[currentLine+1])) {
                    lines[currentLine] = parsedLine + "</ul>";
                }
            }else{
                lines[currentLine] = parsedLine + "</ul>";
            }
        } else{
            activeList = false;
        }
    }

    private void parseFontStyles() {
        parseBold();
        parseItalic();
    }

    private void parseBold() {
        String boldFontRegEx = "__(.+)__";
        String taggedWithStrong = "<strong>$1</strong>";
        lines[currentLine] = lines[currentLine].replaceAll(boldFontRegEx, taggedWithStrong);
    }

    private void parseItalic() {
        String italicFontRegEx = "_(.+)_";
        String taggedWithEm = "<em>$1</em>";
        lines[currentLine] = lines[currentLine].replaceAll(italicFontRegEx, taggedWithEm);
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
