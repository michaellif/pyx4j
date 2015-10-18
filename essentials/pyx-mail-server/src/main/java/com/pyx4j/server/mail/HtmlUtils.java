/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Jan 29, 2015
 * @author ernestog
 */
package com.pyx4j.server.mail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlUtils {
    /**
     * RFC 2822 complaint http://www.regular-expressions.info/email.html
     */
    public static final Pattern EMAIL_REGEXPR = Pattern.compile(
            "[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?");

    public static final Pattern URL_REGEXPR = Pattern.compile("\\b(https?|ftp|file|telnet|http|Unsure)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");

    static String getPlainTextFromHtml(String htmlBody) {
        String result = "";
        if (htmlBody == null) {
            return "";
        }

        Document document = Jsoup.parse(htmlBody);

        replaceInnerLinksAndImagesAndMarkBreakLines(document);

        result = document.outputSettings(new Document.OutputSettings().charset("UTF-8")).text();

        result = replaceMarkersWithBreakLines(result);

        result = StringEscapeUtils.unescapeHtml(result);

        return result.replaceAll("[^\\x00-\\x7F]", " "); // Remove extended chars above 127 in ASCII
    }

    private static String replaceMarkersWithBreakLines(String textBody) {
        // Patch since <br> tags replaced by jsoup text() function
        // includes an empty whitespace that we've got to eliminate
        String result = textBody.replaceAll(" \\\\n", "\n");
        result = result.replaceAll("\\\\n", "\n");
        return result;
    }

    private static void replaceInnerLinksAndImagesAndMarkBreakLines(Document document) {
        Elements elements = document.getAllElements();
        for (Element e : elements) {
            switch (e.tagName()) {
            case "table":
            case "br":
            case "tr":
                e.append("\\n");
                break;
            case "p":
                e.prepend("\\n\\n");
                break;
            case "a":
                if (innerTextIsUrl(e.text()) || innerTextIsEmail(e.text())) {
                    break;
                } else {
                    e.after(" [link to: " + e.attr("href") + "]");
                }
                break;
            case "img":
                e.after("{image: " + e.attr("alt") + "}");
                break;
            default:
                break;
            }
        }
    }

    private static boolean innerTextIsUrl(String text) {
        return matchRegExp(URL_REGEXPR, text);
    }

    private static boolean innerTextIsEmail(String text) {
        return matchRegExp(EMAIL_REGEXPR, text);
    }

    private static boolean matchRegExp(Pattern regExpr, String text) {
        Matcher matcher = regExpr.matcher(text);
        return matcher.find();
    }
}
