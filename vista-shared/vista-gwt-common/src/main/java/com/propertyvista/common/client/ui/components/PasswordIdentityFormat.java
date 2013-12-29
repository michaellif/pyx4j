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
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.text.ParseException;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.CPersonalIdentityField.PersonalIdentityIFormat;

import com.propertyvista.domain.security.PasswordIdentity;

public class PasswordIdentityFormat implements PersonalIdentityIFormat<PasswordIdentity> {

    private final CPersonalIdentityField<PasswordIdentity> component;

    public PasswordIdentityFormat(CPersonalIdentityField<PasswordIdentity> component) {
        super();
        this.component = component;
    }

    @Override
    public String format(PasswordIdentity value) {
        if (value == null) {
            return "";
        }
        if (!value.newNumber().isNull()) {
            return value.newNumber().getValue();
        } else if (!value.obfuscatedNumber().isNull()) {
            return "***************";
        } else {
            return "";
        }
    }

    @Override
    public PasswordIdentity parse(String string) throws ParseException {
        PasswordIdentity value = component.getValue();
        if (CommonsStringUtils.isEmpty(string)) {
            // empty input means no change to the model object
            // TODO - need a way to clear value
            return value;
        } else {
            // not empty string could be either new user input or obfuscated value (formatted) of existing entity
            // check if we are parsing user input or obfuscated value
            boolean userInput = (value == null || value.obfuscatedNumber().isNull());
            // populate resulting value
            if (value == null) {
                value = EntityFactory.create(PasswordIdentity.class);
            }
            if (userInput) {
                // if no obfuscated value then we are getting new user input
                value.newNumber().setValue(string);
                value.obfuscatedNumber().setValue(null);
            } else {
                value.newNumber().setValue(null);
                value.obfuscatedNumber().setValue(string);
            }
            return value;
        }
    }

    @Override
    public String obfuscate(String data) {
        return "#####";
    }

}
