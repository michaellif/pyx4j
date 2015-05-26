/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on Apr 7, 2015
 * @author vlads
 */
package com.pyx4j.i18n.gettext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import com.pyx4j.commons.CommonsStringUtils;

public class MessageFormatTokenizer {

    private static final char DELIM_START = '{';

    private static final char DELIM_STOP = '}';

    private final I18nConstantsHelper i18nConstantsHelper;

    private final String format;

    private int scanPos = 0;

    private int recursionLevel = 0;

    public interface I18nConstantsHelper {

        public String translateConstant(final String text);

    }

    private static Collection<Character> trimSpaces = Arrays.asList(' ', ',', '`', '\'', '\"', '=', '-', '_', '\n');

    public static class NoSpaceI18nConstantsHelper implements I18nConstantsHelper {

        protected final I18nConstantsHelper i18nConstantsHelper;

        private final boolean translateAlways;

        public NoSpaceI18nConstantsHelper(I18nConstantsHelper i18nConstantsHelper) {
            this(i18nConstantsHelper, true);
        }

        public NoSpaceI18nConstantsHelper(I18nConstantsHelper i18nConstantsHelper, boolean translateAlways) {
            this.i18nConstantsHelper = i18nConstantsHelper;
            this.translateAlways = translateAlways;
        }

        protected boolean translateAlways() {
            return translateAlways;
        }

        @Override
        public String translateConstant(String text) {
            if (!canTranslae(text)) {
                return text;
            }
            StringBuilder prefix = new StringBuilder();
            StringBuilder suffix = new StringBuilder();
            StringBuilder tranlatable = new StringBuilder();

            for (char c : text.toCharArray()) {
                if (trimSpaces.contains(c) || c == '.') {
                    prefix.append(c);
                } else {
                    break;
                }
            }
            tranlatable.append(text.substring(prefix.length()));

            for (int i = tranlatable.length() - 1; i >= 0; i--) {
                char c = tranlatable.charAt(i);
                if (trimSpaces.contains(c)) {
                    suffix.append(c);
                } else {
                    break;
                }
            }
            String toTranslate = tranlatable.subSequence(0, tranlatable.length() - suffix.length()).toString();
            if (toTranslate.length() == 0) {
                return prefix.toString() + suffix.reverse().toString();
            } else {
                String partTranslated = i18nConstantsHelper.translateConstant(toTranslate);
                if (partTranslated == null) {
                    if (translateAlways()) {
                        return text;
                    } else {
                        return null;
                    }
                } else {
                    return prefix.toString() + partTranslated + suffix.reverse().toString();
                }
            }
        }
    }

    public static class SplitLinesTranslatorHelper extends NoSpaceI18nConstantsHelper {

        public SplitLinesTranslatorHelper(I18nConstantsHelper i18nConstantsHelper) {
            super(i18nConstantsHelper);
        }

        @Override
        public String translateConstant(String text) {
            StringBuilder translated = new StringBuilder();
            //for (String part : text.split("\n"))  ->  will not split "line\n"
            StringTokenizer t = new StringTokenizer(text, "\n", true);
            while (t.hasMoreTokens()) {
                String part = t.nextToken();
                if (part.equals("\n")) {
                    translated.append("\n");
                } else {
                    String partTranslated = super.translateConstant(part);
                    if (partTranslated == null) {
                        if (translateAlways()) {
                            partTranslated = part;
                        } else {
                            return null;
                        }
                    }
                    translated.append(partTranslated);
                }
            }
            return translated.toString();
        }

    };

    private static class TranslatorCounter implements I18nConstantsHelper {

        int translateCallCount = 0;

        @Override
        public String translateConstant(String text) {
            translateCallCount++;
            return text;
        }
    }

    private static enum QuotedString {

        Start,

        Continue,

        Ending
    }

    public static String translate(I18nConstantsHelper i18nConstantsHelper, String formatPattern) {
        return new MessageFormatTokenizer(i18nConstantsHelper, formatPattern).pars();
    }

    public static String translateTrimSpaces(I18nConstantsHelper i18nConstantsHelper, String formatPattern) {
        return translate(new SplitLinesTranslatorHelper(i18nConstantsHelper), formatPattern);
    }

    public static boolean hasTranslatable(String formatPattern) {
        TranslatorCounter translatorCounter = new TranslatorCounter();
        translateTrimSpaces(translatorCounter, formatPattern);
        return translatorCounter.translateCallCount > 0;
    }

    public MessageFormatTokenizer(I18nConstantsHelper i18nConstantsHelper, String formatPattern) {
        assert formatPattern != null : "Null format pattern is unexpected";
        this.i18nConstantsHelper = i18nConstantsHelper;
        this.format = formatPattern;
        this.scanPos = 0;
        this.recursionLevel = 0;
    }

    private String pars() {
        StringBuilder result = new StringBuilder();
        StringBuilder text = new StringBuilder();
        StringBuilder formatPattern = new StringBuilder();

        QuotedString quotedString = null;

        nextChar: for (; scanPos < format.length(); scanPos++) {
            char c = format.charAt(scanPos);
            if (quotedString != null) {
                if (recursionLevel == 0) {
                    text.append(c);
                } else {
                    formatPattern.append(c);
                }
                switch (quotedString) {
                case Start:
                    if (c == '\'') {
                        quotedString = null;
                    } else {
                        quotedString = QuotedString.Continue;
                    }
                    continue nextChar;
                case Continue:
                    if (c == '\'') {
                        quotedString = QuotedString.Ending;
                    }
                    continue nextChar;
                case Ending:
                    if (c == '\'') {
                        // Double quote inside the string
                        quotedString = QuotedString.Continue;
                        continue nextChar;
                    } else {
                        quotedString = null;
                    }
                    break;
                }
            }

            switch (c) {
            case '\'':
                quotedString = QuotedString.Start;
                if (recursionLevel == 0) {
                    text.append(c);
                } else {
                    formatPattern.append(c);
                }
                break;
            case DELIM_START:
                StringBuilder addTo;
                if (recursionLevel == 0) {
                    addTo = result;
                } else {
                    addTo = formatPattern;
                }
                addTo.append(translateConstant(text.toString()));
                text = new StringBuilder();

                addTo.append(c);

                scanPos++;
                recursionLevel++;
                String res = pars();
                recursionLevel--;
                if (recursionLevel == 0) {
                    res = parsPatternFormat(res);
                }

                addTo.append(res);
                addTo.append(DELIM_STOP);
                break;
            case DELIM_STOP:
                result.append(formatPattern);
                break nextChar;
            default:
                if (recursionLevel == 0) {
                    text.append(c);
                } else {
                    formatPattern.append(c);
                }
                break;
            }
        }

        result.append(translateConstant(text.toString()));
        return result.toString();
    }

    public static boolean canTranslae(String str) {
        String tr = str.trim();
        if (tr.length() == 0) {
            return false;
        } else if (tr.matches("[`'\"=,. _\\-!?]*")) {
            return false;
        } else {
            return true;
        }
    }

    private String translateConstant(final String text) {
        if (!CommonsStringUtils.isStringSet(text)) {
            return "";
        } else if (!canTranslae(text)) {
            return text;
        } else {
            return i18nConstantsHelper.translateConstant(text);
        }
    }

    private String parsPatternFormat(final String formatPattern) {
        int comaIdx = formatPattern.indexOf(',');
        String formatType = null;
        String formatStyle = null;
        if (comaIdx > 0) {
            int comaTypeIdx = formatPattern.indexOf(',', comaIdx + 1);
            if (comaTypeIdx > 0) {
                formatType = formatPattern.substring(comaIdx + 1, comaTypeIdx);
                formatStyle = formatPattern.substring(comaTypeIdx + 1, formatPattern.length());
                comaIdx = comaTypeIdx;
            } else {
                formatType = formatPattern.substring(comaIdx + 1, formatPattern.length());
            }
        }
        if (formatType == null) {
            return formatPattern;
        } else {
            if (formatType.equals("choice")) {
                StringBuilder result = new StringBuilder(formatPattern.substring(0, comaIdx + 1));
                String[] choices = splitNestedChoices(formatStyle);
                for (int i = 0; i < choices.length; i++) {
                    if (i != 0) {
                        result.append('|');
                    }
                    String choice = choices[i];
                    int comparatorIdx = choice.indexOf('#');
                    if (comparatorIdx > 0) {
                        String choiceValueText = choice.substring(0, comparatorIdx);
                        result.append(choiceValueText).append('#');
                        String choiceResult = choice.substring(comparatorIdx + 1, choice.length());
                        if (choiceResult.indexOf(DELIM_START) == -1) {
                            choiceResult = translateConstant(choiceResult);
                        } else {
                            choiceResult = translate(i18nConstantsHelper, choiceResult);
                        }
                        result.append(choiceResult);
                    } else {
                        comparatorIdx = choice.indexOf('<');
                        if (comparatorIdx > 0) {
                            String choiceValueText = choice.substring(0, comparatorIdx);
                            result.append(choiceValueText).append('<');
                            String choiceResult = choice.substring(comparatorIdx + 1, choice.length());
                            if (choiceResult.indexOf(DELIM_START) == -1) {
                                choiceResult = translateConstant(choiceResult);
                            } else {
                                choiceResult = translate(i18nConstantsHelper, choiceResult);
                            }
                            result.append(choiceResult);
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
                return result.toString();
            } else {
                return formatPattern;
            }
        }
    }

    private static String[] splitNestedChoices(final String pattern) {
        List<String> choices = new ArrayList<>();
        StringBuilder text = new StringBuilder();
        int nested = 0;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (nested == 0 && c == '|') {
                choices.add(text.toString());
                text = new StringBuilder();
            } else {
                text.append(c);
                if (c == '{') {
                    nested++;
                } else if (c == '}') {
                    nested--;
                }
            }
        }
        if (text.length() > 0) {
            choices.add(text.toString());
        }
        return choices.toArray(new String[choices.size()]);
    }

}