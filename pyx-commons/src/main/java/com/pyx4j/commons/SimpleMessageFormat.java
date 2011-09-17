/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on 2010-05-13
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.commons;

import java.util.Date;

/**
 * Minimal implementation of java.text.MessageFormat to get going in GWT and null safe on server.
 */
public class SimpleMessageFormat {

    private static final char DELIM_START = '{';

    private static final char DELIM_STOP = '}';

    private SimpleMessageFormat() {
    }

    private static enum QuotedString {

        Start,

        Continue,

        Ending
    }

    public static String format(final String pattern, Object... arguments) {
        StringBuilder result = new StringBuilder();

        QuotedString quotedString = null;
        boolean formatElement = false;

        StringBuilder formatPattern = null;

        int formatRecursion = 0;
        char c;
        nextChar: for (int index = 0; index < pattern.length(); index++) {
            c = pattern.charAt(index);
            if (quotedString != null) {
                switch (quotedString) {
                case Start:
                    if (c == '\'') {
                        result.append(c);
                        quotedString = null;
                    } else {
                        result.append(c);
                        quotedString = QuotedString.Continue;
                    }
                    continue nextChar;
                case Continue:
                    if (c == '\'') {
                        quotedString = QuotedString.Ending;
                    } else {
                        result.append(c);
                    }
                    continue nextChar;
                case Ending:
                    if (c == '\'') {
                        // Double quote inside the string
                        result.append(c);
                        quotedString = QuotedString.Continue;
                        continue nextChar;
                    } else {
                        quotedString = null;
                    }
                    break;
                }
            }
            if (formatElement) {
                if (c == DELIM_STOP) {
                    if (formatRecursion > 0) {
                        formatRecursion--;
                        formatPattern.append(c);
                    } else {
                        format(result, formatPattern.toString(), arguments);
                        formatElement = false;
                        formatPattern = null;
                    }
                } else {
                    formatPattern.append(c);
                    if (c == DELIM_START) {
                        formatRecursion++;
                    }
                }
                continue;
            }

            switch (c) {
            case '\'':
                quotedString = QuotedString.Start;
                break;
            case DELIM_START:
                formatPattern = new StringBuilder();
                formatElement = true;
                break;
            default:
                result.append(c);
                break;
            }
        }

        return result.toString();
    }

    private static void format(StringBuilder result, final String formatPattern, Object... arguments) {
        int comaIdx = formatPattern.indexOf(',');
        int argumentIndex;
        String formatType = null;
        String formatStyle = null;
        if (comaIdx > 0) {
            argumentIndex = Integer.valueOf(formatPattern.substring(0, comaIdx));
            int comaTypeIdx = formatPattern.indexOf(',', comaIdx + 1);
            if (comaTypeIdx > 0) {
                formatType = formatPattern.substring(comaIdx + 1, comaTypeIdx);
                formatStyle = formatPattern.substring(comaTypeIdx + 1, formatPattern.length());
            } else {
                formatType = formatPattern.substring(comaIdx + 1, formatPattern.length());
            }
        } else {
            argumentIndex = Integer.valueOf(formatPattern);
        }
        Object value = arguments[argumentIndex];
        Object formatedValue = null;
        if (formatType == null) {
            if (value == null) {
                formatedValue = "";
            } else {
                formatedValue = value;
            }
        } else {
            if (formatType.equals("number")) {
                if (value == null) {
                    formatedValue = "";
                } else {
                    if ("integer".equals(formatStyle)) {
                        formatStyle = "#,###";
                    }
                    formatedValue = SimpleNumberFormatImpl.format(formatStyle, toDouble(value));
                }
            } else if (formatType.equals("size")) {
                if (value == null) {
                    formatedValue = "";
                } else {
                    long length = Double.valueOf(toDouble(value)).longValue();
                    if (length < 1024) {
                        //TODO use i18n
                        formatedValue = String.valueOf(length) + " B";
                    } else if (length < 1024 * 1024) {
                        formatedValue = String.valueOf(length / 1024) + " KB";
                    } else {
                        formatedValue = String.valueOf(length / (1024 * 1024)) + " MB";
                    }

                }
            } else if (formatType.equals("date")) {
                formatedValue = SimpleDateFormatImpl.format(formatStyle, (Date) value);
            } else if (formatType.equals("time")) {
                formatedValue = SimpleDateFormatImpl.formatTime(formatStyle, (Date) value);
            } else if (formatType.equals("choice")) {
                String[] choices = formatStyle.split("\\|");
                Double selectorValue = null;
                if (value == null) {
                    selectorValue = Double.NaN;
                } else {
                    selectorValue = toDouble(value);
                }
                String prevFormat = null;
                for (String choice : choices) {
                    int comparatorIdx = choice.indexOf('#');
                    if (comparatorIdx > 0) {
                        String choiceValueText = choice.substring(0, comparatorIdx);
                        String choiceFormat = choice.substring(comparatorIdx + 1, choice.length());
                        if (choiceValueText.equals("null")) {
                            if (value == null) {
                                formatedValue = format(choiceFormat, arguments);
                                break;
                            } else {
                                prevFormat = choiceFormat;
                                continue;
                            }
                        } else if (choiceValueText.equals("!null")) {
                            if (value != null) {
                                formatedValue = format(choiceFormat, arguments);
                                break;
                            } else {
                                prevFormat = choiceFormat;
                                continue;
                            }
                        }

                        double choiceValue = Double.valueOf(choiceValueText).doubleValue();
                        if (selectorValue == choiceValue) {
                            formatedValue = format(choiceFormat, arguments);
                            break;
                        } else if (selectorValue < choiceValue) {
                            formatedValue = format(prevFormat == null ? choiceFormat : prevFormat, arguments);
                            break;
                        } else {
                            prevFormat = choiceFormat;
                            continue;
                        }
                    } else {
                        comparatorIdx = choice.indexOf('<');
                        if (comparatorIdx > 0) {
                            double choiceValue = Double.valueOf(choice.substring(0, comparatorIdx)).doubleValue();
                            String choiceFormat = choice.substring(comparatorIdx + 1, choice.length());
                            if (selectorValue <= choiceValue) {
                                formatedValue = format(prevFormat == null ? choiceFormat : prevFormat, arguments);
                                break;
                            } else {
                                prevFormat = choiceFormat;
                            }
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
                if (formatedValue == null) {
                    if (prevFormat != null) {
                        formatedValue = format(prevFormat, arguments);
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            } else {
                formatedValue = value;
            }
        }
        result.append(formatedValue);
    }

    private static double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException("number expected instead of " + value.getClass());
        }
    }

}
