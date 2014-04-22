/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 3, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.leaseterms;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.policy.policies.domain.LegalTermsPolicyItem;

public class LegalTermsPolicyItemForm extends CForm<LegalTermsPolicyItem> {

    public LegalTermsPolicyItemForm(boolean isEditable) {
        super(LegalTermsPolicyItem.class);
        setEditable(isEditable);
    }

    @Override
    protected IsWidget createContent() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;

        content.setWidget(++row, 0, 2, inject(proto().enabled(), new FieldDecoratorBuilder(true).build()));
        content.setWidget(++row, 0, 2, inject(proto().caption(), new FieldDecoratorBuilder(true).build()));
        content.setWidget(++row, 0, 2, inject(proto().content(), new FieldDecoratorBuilder(true).build()));

        return content;
    }
}
