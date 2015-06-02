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
 * Created on Jan 27, 2015
 * @author ernestog
 */
package com.pyx4j.server.mail;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;

public class HtmlUtilsTest {

    // HTML with inner link
    private static final String HTML_INNER_LINK = "<!DOCTYPE html><html><body><a href=\"http://www.w3schools.com\">This is a link</a></body></html>";

    private static final String TEXT_INNER_LINK = "This is a link [link to: http://www.w3schools.com]";

    // HTML with regular link
    private static final String HTML_REGULAR_LINK = "<!DOCTYPE html><html><body>Go to here: <a href=\"http://www.w3schools.com\">http://www.w3schools.com</a></body></html>";

    private static final String TEXT_REGULAR_LINK = "Go to here: http://www.w3schools.com";

    // HTML with inner link
    private static final String HTML_INNER_MAILTO = "<!DOCTYPE html><html><body>Any question? Let us know <a href=\"http://www.w3schools.com\">here</a></body></html>";

    private static final String TEXT_INNER_MAILTO = "Any question? Let us know here [link to: http://www.w3schools.com]";

    // HTML with regular link
    private static final String HTML_REGULAR_MAILTO = "<!DOCTYPE html><html><body>Any question? Let us know here: <a href=\"http://www.w3schools.com\">support@propertyvista.com</a></body></html>";

    private static final String TEXT_REGULAR_MAILTO = "Any question? Let us know here: support@propertyvista.com";

    // HTML with images

    private static final String HTML_WITH_IMAGE = "<!DOCTYPE html><html><body><img src=\"w3schools.jpg\" alt=\"W3Schools.com\" width=\"104\" height=\"142\"></body></html>";

    private static final String TEXT_WITH_IMAGE = "{image: W3Schools.com}";

    // HTML with paragraphs
    private static final String HTML_WITH_PARAGRAPHS = "<!DOCTYPE html><html><body><p>This is a paragraph.</p><p>This is a paragraph.</p><p>This is a paragraph.</p></body></html>";

    @Test
    public void testGetTextFromHtml() {

        testNoHTMLTagsAndExpectedOutput(HTML_INNER_LINK, TEXT_INNER_LINK);

        testNoHTMLTagsAndExpectedOutput(HTML_REGULAR_LINK, TEXT_REGULAR_LINK);

        testNoHTMLTagsAndExpectedOutput(HTML_INNER_MAILTO, TEXT_INNER_MAILTO);

        testNoHTMLTagsAndExpectedOutput(HTML_REGULAR_MAILTO, TEXT_REGULAR_MAILTO);

        testNoHTMLTagsAndExpectedOutput(HTML_WITH_IMAGE, TEXT_WITH_IMAGE);

        testNoHtmlTagsAndLines(HTML_WITH_PARAGRAPHS, 6);
    }

    void testNoHtmlTagsAndLines(String htmlText, int minimumLines) {
        String plaintext = HtmlUtils.getPlainTextFromHtml(htmlText);
        assertNoHtmlTags(plaintext);
        assertMinimumLines(plaintext, minimumLines);
    }

    private void assertMinimumLines(String plaintext, int minimumLines) {
        int textLines = countLines(plaintext);
        Assert.assertTrue("More lines expected for converted html text. ", textLines >= minimumLines);
    }

    void testNoHTMLTagsAndExpectedOutput(String targetHtml, String resultText) {
        String plaintext = HtmlUtils.getPlainTextFromHtml(targetHtml);
        assertNoHtmlTags(plaintext);
        assertResultExpected(plaintext, resultText);
    }

    private void assertResultExpected(String plaintext, String resultText) {
        boolean matchesExpectedResult = plaintext.equals(resultText);
        Assert.assertTrue("Result text does not match with expected result", matchesExpectedResult);
    }

    private void assertNoHtmlTags(String plaintext) {
        boolean containHtmlTags = hasHtmlTags(plaintext);
        Assert.assertTrue("HTML element found in resultText: '" + plaintext + "'", !containHtmlTags);
    }

    private boolean hasHtmlTags(String text) {
        boolean hasHtmlTags = false;
        Elements htmlElements = Jsoup.parse(text).select("body");
        for (Element e : htmlElements) {
            if (e.tagName() != "body") {
                hasHtmlTags = true;
            }
        }
        return hasHtmlTags;
    }

    private static int countLines(String text) {
        String[] lines = text.split("\r\n|\r|\n");
        return lines.length;
    }
}
