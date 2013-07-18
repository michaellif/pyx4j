/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-07-18
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.activity.security;

import com.pyx4j.forms.client.validators.password.DefaultPasswordStrengthRule;
import com.pyx4j.forms.client.validators.password.HasDescription;

public final class CrmUserPasswordRule extends DefaultPasswordStrengthRule implements HasDescription {

    @Override
    public String getDescription() {
        return PasswordChangeActivity.i18n.tr(//@formatter:off
                "Password Guidelines:\n" +
                "(1) Use 8 to 20 characters.\n" +
                "(2) Don't use your name or email address.\n" +
                "(3) Use a mix of lowercase and uppercase letters, numbers, and symbols.\n" +
                "(4) Make your password hard to guess."
        );//@formatter:on
    }

}