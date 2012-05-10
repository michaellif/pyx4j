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

import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.payment.EcheckInfo;

public class EcheckInfoEditor extends CEntityDecoratableForm<EcheckInfo> {

    private static final I18n i18n = I18n.get(EcheckInfoEditor.class);

    public EcheckInfoEditor() {
        super(EcheckInfo.class);
    }

    public EcheckInfoEditor(IEditableComponentFactory factory) {
        super(EcheckInfo.class, factory);
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel panel = new FormFlexPanel();

        int row = -1;
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().nameOn()), 30).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().bankId()), 5).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().branchTransitNumber()), 10).build());
        panel.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accountNo()), 20).build());

        return panel;
    }
}
