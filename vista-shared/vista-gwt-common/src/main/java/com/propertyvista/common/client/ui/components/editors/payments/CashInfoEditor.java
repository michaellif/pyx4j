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
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.payment.CashInfo;

public class CashInfoEditor extends CEntityDecoratableForm<CashInfo> {

    public CashInfoEditor() {
        super(CashInfo.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().receivedAmount()), 15).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().changeAmount()), 5).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().notes())).build());

        return panel;
    }
}
