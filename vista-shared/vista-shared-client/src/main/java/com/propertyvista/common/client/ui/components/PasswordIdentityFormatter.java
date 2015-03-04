/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 4, 2015
 * @author stanp
 */
package com.propertyvista.common.client.ui.components;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.PersonalIdentityFormatter;

import com.propertyvista.domain.security.PasswordIdentity;

public class PasswordIdentityFormatter extends PersonalIdentityFormatter {

    public PasswordIdentityFormatter() {
        super("");
    }

    @Override
    public String format(String input, boolean obfuscate) {
        return obfuscate ? obfuscate(input) : input;
    }

    @Override
    public String obfuscate(String input) {
        if (CommonsStringUtils.isEmpty(input)) {
            return PasswordIdentity.obfuscatedNull;
        } else {
            return PasswordIdentity.obfuscatedValue;
        }
    }

    @Override
    public boolean isValidInput(String input) {
        // TODO - may want to enforce min length, mixed case, and mixed alphanumeric chars 
        return true;
    }

    @Override
    public String inputFilter(String input) {
        // no filtering
        return input;
    }
}
