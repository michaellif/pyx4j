/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2014
 * @author stanp
 * @version $Id$
 */
package com.pyx4j.commons;

/**
 * Utility class used by PersonalIdentityFormat implementation and by server-side modules
 */
public class PersonalIdentityFormatter {

    private final char FORMAT_CLEAR = 'x';

    private final char FORMAT_HIDDEN = 'X';

    private final String FORMAT_DELIM = ";";

    private final String[] patternArr;

    private final int[] dataLengthArr;

    public PersonalIdentityFormatter(String pattern) {
        // pattern is interpreted as follows:
        //   X - input character in this position will be translated to 'X' (hidden data)
        //   x - input character in this position will not be modified (open data)
        //   no other alphanumeric chars is allowed
        //   any non-alphanumeric chars will be treated as decorators and will not be modified
        // multiple applicable formats can be specified using ';' as a delimiter
        // Valid Pattern examples: 'XXX-XXX-xxx', 'XXXX XXXX XXXX xxxx', 'xxx XXX XXX xxx'
        if (!isPatternValid(pattern)) {
            throw new Error("Invalid identity format: " + pattern);
        }
        patternArr = pattern.split(FORMAT_DELIM);
        dataLengthArr = new int[patternArr.length];
        for (int idx = 0; idx < patternArr.length; idx++) {
            String pat = patternArr[idx];
            for (int pos = 0; pos < pat.length(); pos++) {
                char c = pat.charAt(pos);
                if (c == FORMAT_CLEAR || c == FORMAT_HIDDEN) {
                    dataLengthArr[idx] += 1;
                }
            }
        }
    }

    public String format(String input, boolean obfuscate) {
        return format(input, obfuscate, true);
    }

    public String obfuscate(String input) {
        return format(input, true, false);
    }

    private String format(String input, boolean obfuscate, boolean decorate) {
        if (input == null) {
            return "";
        }

        String data = inputFilter(input);
        String pattern = getPattern(input);
        if (pattern == null) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        for (int pos = 0, dataPos = 0; pos < pattern.length(); pos++) {
            char c = pattern.charAt(pos);
            if (c == FORMAT_CLEAR) {
                output.append(data.charAt(dataPos++));
            } else if (c == FORMAT_HIDDEN) {
                output.append(obfuscate ? FORMAT_HIDDEN : data.charAt(dataPos));
                dataPos++;
            } else if (decorate) {
                output.append(c);
            }
        }
        return output.toString();
    }

    public boolean isValidInput(String input) {
        return getPattern(input) != null;
    }

    private String getPattern(String input) {
        String data = inputFilter(input);
        int dataLength = data.length();
        int patternIdx = -1;
        for (int idx = 0; idx < patternArr.length; idx++) {
            if (dataLength == dataLengthArr[idx]) {
                patternIdx = idx;
                break;
            }
        }
        return patternIdx > -1 ? patternArr[patternIdx] : null;
    }

    public String inputFilter(String input) {
        // only alphanumeric chars allowed in user input
        return input.replaceAll("[\\W_]", "");
    }

    private boolean isPatternValid(String pattern) {
        // check for not allowed chars in the formatting pattern
        return pattern.matches("^[\\W_" + FORMAT_CLEAR + FORMAT_HIDDEN + "]*$");
    }
}
