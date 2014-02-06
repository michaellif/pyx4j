/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-28
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.biz.tenant.insurance.errors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CfcApiException extends Exception {

    private static final long serialVersionUID = 487468899592231236L;

    private final String code;

    public CfcApiException(String errorCodeLine) {
        super(parseCfcErrorMessage(errorCodeLine));
        code = parseCfcErrorCode(errorCodeLine);
    }

    public static boolean isCfcErrorCodeLine(String errorCodeLine) {
        return errorCodeLine.startsWith("ER");
    }

    public static String parseCfcErrorCode(String errorCodeLine) {
        if (!isCfcErrorCodeLine(errorCodeLine)) {
            throw new IllegalArgumentException("this is not a proper cfc error message: '" + errorCodeLine + "'");
        }
        String code = null;

        Pattern p = Pattern.compile("(ER\\d*)(.*)");
        Matcher m = p.matcher(errorCodeLine);

        if (m.matches()) {
            code = errorCodeLine.substring(m.start(1), m.start(1) + m.end(1));
        } else {
            code = errorCodeLine.length() > 2 ? errorCodeLine.substring(2) : "";
        }
        return code.trim();
    }

    public static String parseCfcErrorMessage(String errorCodeLine) {
        if (!isCfcErrorCodeLine(errorCodeLine)) {
            throw new IllegalArgumentException("this is not a proper cfc error message: '" + errorCodeLine + "'");
        }
        String message = null;

        Pattern p = Pattern.compile("(ER\\d*)(.*)");
        Matcher m = p.matcher(errorCodeLine);

        if (m.matches()) {
            message = errorCodeLine.substring(m.start(2));
        } else {
            message = errorCodeLine.length() > 2 ? errorCodeLine.substring(2) : "";
        }
        return message.trim();
    }

    public String getErrorCode() {
        return code;
    }

}
