/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;

import com.propertyvista.domain.payment.CashInfo;

public class CashInfoEditor extends CForm<CashInfo> {

    public CashInfoEditor() {
        super(CashInfo.class);
    }

    @Override
    protected IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, inject(proto().receivedAmount(), new FieldDecoratorBuilder(15).build()));
        panel.setWidget(++row, 0, inject(proto().changeAmount(), new FieldDecoratorBuilder(5).build()));
        panel.setWidget(++row, 0, inject(proto().notes(), new FieldDecoratorBuilder().build()));

        return panel;
    }
}
