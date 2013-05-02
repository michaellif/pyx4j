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

import com.pyx4j.forms.client.ui.CPersonalIdentityField;
import com.pyx4j.forms.client.ui.NPersonalIdentityField;
import com.pyx4j.widgets.client.dialog.MessageDialog;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.domain.payment.AccountNumberIdentity;

public class AccountNumberField extends CPersonalIdentityField<AccountNumberIdentity> {

    public AccountNumberField() {
        super(AccountNumberIdentity.class, "X xxxx;XX xxxx;XXX xxxx;XXXX xxxx;X XXXX xxxx;XX XXXX xxxx;XXX XXXX xxxx;XXXX XXXX xxxx", null);
    }

    @Override
    protected NPersonalIdentityField<AccountNumberIdentity> createWidget() {
        return new NPersonalIdentityField<AccountNumberIdentity>(this, VistaImages.INSTANCE.collapse()) {
            @Override
            public void onToggle() {
                if (isToggledOn()) {
                    MessageDialog.info("La-La-La!");
                }
                super.onToggle();
            }
        };
    }
}
