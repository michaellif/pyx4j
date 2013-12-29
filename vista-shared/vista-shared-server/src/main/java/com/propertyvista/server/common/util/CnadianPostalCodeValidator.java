/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.server.common.util;

public class CnadianPostalCodeValidator {

    private final String original;

    private final String normalized;

    private final String formatted;

    private final boolean valid;

    public CnadianPostalCodeValidator(String postalCode) {
        original = postalCode;
        normalized = original.replaceAll("\\s", "").toUpperCase();
        valid = normalized.matches("^([A-Z][0-9]){3}$");
        formatted = valid ? normalized.replaceFirst("^(...)", "$1 ") : original;
    }

    public String original() {
        return original;
    }

    public boolean isValid() {
        return valid;
    }

    public String format() {
        return formatted;
    }
}
