/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-05-02
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import com.google.gwt.user.client.Command;

import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.NPersonalIdentityField;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.payment.AccountNumberIdentity;
import com.propertyvista.shared.util.AccountNumberFormatter;

public abstract class AccountNumberField extends CPersonalIdentityField<AccountNumberIdentity> {

    public AccountNumberField() {
        super(AccountNumberIdentity.class, new AccountNumberFormatter());

        NPersonalIdentityField<AccountNumberIdentity> field = new NPersonalIdentityField<AccountNumberIdentity>(this);

        final Button actionButton = new Button(VistaImages.INSTANCE.triggerDown());
        actionButton.setCommand(new Command() {

            @Override
            public void execute() {
                onRevealNumber();
            }
        });

        field.setActionButton(actionButton);

        setNativeWidget(field);
    }

    public abstract void onRevealNumber();
}
