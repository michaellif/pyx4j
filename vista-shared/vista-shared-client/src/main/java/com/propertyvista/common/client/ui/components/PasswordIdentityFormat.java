/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 28, 2013
 * @author vlads
 */
package com.propertyvista.common.client.ui.components;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.forms.client.ui.CPersonalIdentityField.IPersonalIdentityFormat;

import com.propertyvista.domain.security.PasswordIdentity;

public class PasswordIdentityFormat implements IPersonalIdentityFormat<PasswordIdentity> {

    public PasswordIdentityFormat() {
        super();
    }

    @Override
    public String format(PasswordIdentity value) {
        if (value == null) {
            return "";
        }
        if (!value.newNumber().isNull()) {
            return value.newNumber().getValue();
        } else if (!value.obfuscatedNumber().isNull()) {
            return PasswordIdentity.obfuscatedValue;
        } else {
            return "";
        }
    }

    @Override
    public String obfuscate(String data) {
        if (CommonsStringUtils.isEmpty(data)) {
            return PasswordIdentity.obfuscatedNull;
        } else {
            return PasswordIdentity.obfuscatedValue;
        }
    }

}
