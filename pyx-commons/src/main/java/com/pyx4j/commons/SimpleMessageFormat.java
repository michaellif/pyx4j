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

    private static String format = null;

    private static int scanPos = 0;

    private static int recursionLevel = 0;

    private SimpleMessageFormat() {
    }

    private static enum QuotedString {

        Start,

        Continue,

        Ending
    }

    public static String format(final String fmt, Object... arguments) {
        format = fmt;
        scanPos = 0;
        recursionLevel = 0;
        return argScan(arguments);
    }

    /*
     * Recursive scan for Argument format {...}; the resulting string is resolved by calling argFormat().
     * The inner arguments get resolved first, so there will be no curly brackets inside the parent's
     * format pattern when calling argFormat.
     */
    private static String argScan(Object... arguments) {
        StringBuilder result = new StringBuilder();
        StringBuilder formatPattern = new StringBuilder();
        boolean done = false;

        QuotedString quotedString = null;

        char c;
        nextChar: for (; scanPos < format.length(); scanPos++) {
            c = format.charAt(scanPos);
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

            switch (c) {
            case '\'':
                quotedString = QuotedString.Start;
                break;
            case DELIM_START:
                scanPos++;
                recursionLevel++;
                String res = argScan(arguments);
                recursionLevel--;
                if (recursionLevel == 0) {
                    result.append(res);
                } else {
                    formatPattern.append(res);
                }
                break;
            case DELIM_STOP:
                result.append(argFormat(formatPattern.toString(), arguments));
                done = true;
                break;
            default:
                if (recursionLevel == 0) {
                    result.append(c);
                } else {
                    formatPattern.append(c);
                }
                break;
            }
            if (done) {
                break;
            }
        }

        return result.toString();
    }

    /*
     * Format arguments according to pattern
     */
    private static String argFormat(final String formatPattern, Object... arguments) {
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
        Object arg = arguments[argumentIndex];
        Object formatedArg = null;
        if (formatType == null) {
            if (arg == null) {
                formatedArg = "";
            } else {
                formatedArg = arg;
            }
        } else {
            if (formatType.equals("number")) {
                if (arg == null) {
                    formatedArg = "";
                } else {
                    if ("integer".equals(formatStyle)) {
                        formatStyle = "#,###";
                    }
                    formatedArg = SimpleNumberFormatImpl.format(formatStyle, toDouble(arg));
                }
            } else if (formatType.equals("size")) {
                if (arg == null) {
                    formatedArg = "";
                } else {
                    long length = Double.valueOf(toDouble(arg)).longValue();
                    if (length < 1024) {
                        //TODO use i18n
                        formatedArg = String.valueOf(length) + " B";
                    } else if (length < 1024 * 1024) {
                        formatedArg = String.valueOf(length / 1024) + " KB";
                    } else {
                        formatedArg = String.valueOf(length / (1024 * 1024)) + " MB";
                    }

                }
            } else if (formatType.equals("duration")) {
                if (arg == null) {
                    formatedArg = "";
                } else {
                    long duration = Double.valueOf(toDouble(arg)).longValue();
                    formatedArg = TimeUtils.durationFormat(duration);
                }
            } else if (formatType.equals("date")) {
                formatedArg = SimpleDateFormatImpl.format(formatStyle, (Date) arg);
            } else if (formatType.equals("time")) {
                formatedArg = SimpleDateFormatImpl.formatTime(formatStyle, (Date) arg);
            } else if (formatType.equals("choice")) {
                String[] choices = formatStyle.split("\\|");
                Double selectorValue = null;
                if (arg == null) {
                    selectorValue = Double.NaN;
                } else {
                    selectorValue = toDouble(arg);
                }
                String prevResult = null;
                for (String choice : choices) {
                    int comparatorIdx = choice.indexOf('#');
                    if (comparatorIdx > 0) {
                        String choiceValueText = choice.substring(0, comparatorIdx);
                        String choiceResult = choice.substring(comparatorIdx + 1, choice.length());
                        if (choiceValueText.equals("null")) {
                            if (arg == null) {
                                formatedArg = choiceResult;
                                break;
                            } else {
                                prevResult = choiceResult;
                                continue;
                            }
                        } else if (choiceValueText.equals("!null")) {
                            if (arg != null) {
                                formatedArg = choiceResult;
                                break;
                            } else {
                                prevResult = choiceResult;
                                continue;
                            }
                        }

                        double choiceValue = Double.valueOf(choiceValueText).doubleValue();
                        if (selectorValue == choiceValue) {
                            formatedArg = choiceResult;
                            break;
                        } else if (selectorValue < choiceValue) {
                            formatedArg = (prevResult == null ? choiceResult : prevResult);
                            break;
                        } else {
                            prevResult = choiceResult;
                            continue;
                        }
                    } else {
                        comparatorIdx = choice.indexOf('<');
                        if (comparatorIdx > 0) {
                            double choiceValue = Double.valueOf(choice.substring(0, comparatorIdx)).doubleValue();
                            String choiceResult = choice.substring(comparatorIdx + 1, choice.length());
                            if (selectorValue <= choiceValue) {
                                formatedArg = (prevResult == null ? choiceResult : prevResult);
                                break;
                            } else {
                                prevResult = choiceResult;
                            }
                        } else {
                            throw new IllegalArgumentException();
                        }
                    }
                }
                if (formatedArg == null) {
                    if (prevResult != null) {
                        formatedArg = prevResult;
                    } else {
                        throw new IllegalArgumentException();
                    }
                }
            } else {
                formatedArg = arg;
            }
        }
        return formatedArg.toString();
    }

    private static double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new IllegalArgumentException("number expected instead of " + value.getClass());
        }
    }

}
