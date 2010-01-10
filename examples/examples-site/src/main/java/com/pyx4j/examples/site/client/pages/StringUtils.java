package com.pyx4j.examples.site.client.pages;

public class StringUtils {

    private static String[][] htmlEscape = { { "&lt;", "<" }, { "&gt;", ">" }, { "&amp;", "&" }, { "&quot;", "\"" }, { "&agrave;", "�" }, { "&Agrave;", "�" },
            { "&acirc;", "�" }, { "&auml;", "�" }, { "&Auml;", "�" }, { "&Acirc;", "�" }, { "&aring;", "�" }, { "&Aring;", "�" }, { "&aelig;", "�" },
            { "&AElig;", "�" }, { "&ccedil;", "�" }, { "&Ccedil;", "�" }, { "&eacute;", "�" }, { "&Eacute;", "�" }, { "&egrave;", "�" }, { "&Egrave;", "�" },
            { "&ecirc;", "�" }, { "&Ecirc;", "�" }, { "&euml;", "�" }, { "&Euml;", "�" }, { "&iuml;", "�" }, { "&Iuml;", "�" }, { "&ocirc;", "�" },
            { "&Ocirc;", "�" }, { "&ouml;", "�" }, { "&Ouml;", "�" }, { "&oslash;", "�" }, { "&Oslash;", "�" }, { "&szlig;", "�" }, { "&ugrave;", "�" },
            { "&Ugrave;", "�" }, { "&ucirc;", "�" }, { "&Ucirc;", "�" }, { "&uuml;", "�" }, { "&Uuml;", "�" }, { "&nbsp;", " " }, { "&copy;", "\u00a9" },
            { "&reg;", "\u00ae" }, { "&euro;", "\u20a0" } };

    public static final String unescapeHTML(String s, int start) {
        int i, j, k, l;

        i = s.indexOf("&", start);
        start = i + 1;
        if (i > -1) {
            j = s.indexOf(";", i);
            /*
             * we don't want to start from the beginning the next time, to handle the case
             * of the & thanks to Pieter Hertogh for the bug fix!
             */
            if (j > i) {
                // ok this is not most optimized way to
                // do it, a StringBuffer would be better,
                // this is left as an exercise to the reader!
                String temp = s.substring(i, j + 1);
                // search in htmlEscape[][] if temp is there
                k = 0;
                while (k < htmlEscape.length) {
                    if (htmlEscape[k][0].equals(temp))
                        break;
                    else
                        k++;
                }
                if (k < htmlEscape.length) {
                    s = s.substring(0, i) + htmlEscape[k][1] + s.substring(j + 1);
                    return unescapeHTML(s, start); // recursive call
                }
            }
        }
        return s;
    }
}
