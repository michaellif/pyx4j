/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.common.client.ui.validators;

import java.text.ParseException;

import com.pyx4j.forms.client.ui.IFormat;

public class YouTubeVideoIdFormat implements IFormat<String> {

    @Override
    public String format(String value) {
        return value;
    }

    static String extract(String value, String prefix) {
        int idx = value.indexOf(prefix);
        if (idx >= 0) {
            int start = idx + prefix.length();
            if ((value.length() - start) >= 11) {
                return value.substring(start, start + 11);
            }
        }
        return null;
    }

    @Override
    public String parse(String value) throws ParseException {
        if (value == null) {
            return null;
        }
        for (String p : new String[] { "watch?v=", "watch#!v=", "/vi/", "/?v=", "/v/" }) {
            String v = extract(value, p);
            if (v != null) {
                value = v;
                break;
            }
        }
        value = value.trim();
        if (!value.matches("[a-zA-Z0-9_-]{11}")) {
            return null;
        }
        return value;
    }

}
