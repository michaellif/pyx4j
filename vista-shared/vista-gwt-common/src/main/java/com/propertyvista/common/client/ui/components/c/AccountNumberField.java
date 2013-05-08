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
import com.pyx4j.widgets.client.ToggleButton;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.payment.AccountNumberIdentity;

public abstract class AccountNumberField extends CPersonalIdentityField<AccountNumberIdentity> {

    public AccountNumberField() {
        super(AccountNumberIdentity.class, "X xxxx;XX xxxx;XXX xxxx;XXXX xxxx;X XXXX xxxx;XX XXXX xxxx;XXX XXXX xxxx;XXXX XXXX xxxx", null);
    }

    @Override
    protected NPersonalIdentityField<AccountNumberIdentity> createWidget() {

        final ToggleButton triggerButton = new ToggleButton(VistaImages.INSTANCE.triggerDown());
        triggerButton.setCommand(new Command() {

            @Override
            public void execute() {
                onRevealNumber();
            }
        });

        return new NPersonalIdentityField<AccountNumberIdentity>(this, triggerButton);
    }

    public abstract void onRevealNumber();
}
