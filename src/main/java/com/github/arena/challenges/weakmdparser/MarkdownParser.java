package com.github.arena.challenges.weakmdparser;

/**
 I couldn't help myself but to try implement a design pattern. I am still thinking it wasn't worth my time
 */
public class MarkdownParser {
    private boolean activeList = false;

    String parse(String parsedString) {

        TagParser parser = new HeaderParser();
        parser.setNextParser(new ListParser())
                .setNextParser(new ParagraphParser())
                .setNextParser(new UListParser())
                .setNextParser(new FontStyleParser());
        String[] lines = parsedString.split("\n");
        StringBuilder result = new StringBuilder();
        for (String currentLine : lines) {
            result.append(parser.parse(currentLine));
        }
        return result.toString();
    }

    public abstract class TagParser {

        private TagParser next;

        public TagParser setNextParser(TagParser next) {
            this.next = next;
            return next;
        }

        public abstract String parse(String parsedLine);

        protected String nextParser(String parsedLine) {
            if (next == null) {
                return parsedLine;
            }
            return next.parse(parsedLine);
        }

    }
    public class ListParser extends TagParser {

        @Override
        public String parse(String parsedLine) {
            if (parsedLine.startsWith("*")) {
                String skipAsterisk = parsedLine.substring(2);
                parsedLine = "<li>" + skipAsterisk + "</li>";
            }
            return nextParser(parsedLine);
        }

    }
    public class UListParser extends TagParser {

        @Override
        public String parse(String parsedLine) {
            if (!activeList) {
                if (isList(parsedLine)) {
                    activeList = true;
                    parsedLine = "<ul>" + parsedLine;
                }
            } else {
                activeList = false;
                parsedLine = parsedLine + "</ul>";
            }
            return nextParser(parsedLine);
        }

    }
    public class ParagraphParser extends TagParser {

        @Override
        public String parse(String parsedLine) {
            if (!(isHeader(parsedLine) || isList(parsedLine)))
                parsedLine = "<p>" + parsedLine + "</p>";

            return nextParser(parsedLine);
        }
        private boolean isHeader(String parsedLine) {
            return parsedLine.matches("(<h).*");
        }
    }
    public class FontStyleParser extends TagParser {

        @Override
        public String parse(String parsedLine) {
            return nextParser(parseItalic(parseBold(parsedLine)));
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

    }
    public class HeaderParser extends TagParser {

        @Override
        public String parse(String parsedLine) {
            int count = 0;

            for (int i = 0; i < parsedLine.length() && parsedLine.charAt(i) == '#'; i++) {
                count++;
            }

            if (count != 0) {
                parsedLine = "<h" + count + ">" + parsedLine.substring(count + 1) + "</h" + count + ">";
            }


            return nextParser(parsedLine);
        }

    }
    private boolean isList(String parsedLine) {
        return parsedLine.matches("(<li>).*");
    }
}
