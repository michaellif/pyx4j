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
import com.propertyvista.domain.payment.DirectDebitInfo;

public class DirectDebitInfoEditor extends CEntityDecoratableForm<DirectDebitInfo> {

    public DirectDebitInfoEditor() {
        super(DirectDebitInfo.class);

        setEditable(false);
        inheritEditable(false);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();

        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().nameOn()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().traceNumber()), 20).build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().locationCode()), 20).build());

        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().nameOn()).setVisible(!getValue().nameOn().isNull());
        get(proto().traceNumber()).setVisible(!getValue().traceNumber().isNull());
        get(proto().locationCode()).setVisible(!getValue().locationCode().isNull());
    }
}
