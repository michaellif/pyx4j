/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-01-21
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.legal.LegalStatus;

public class LegalStatusForm extends CEntityForm<LegalStatus> {

    private static final I18n i18n = I18n.get(LegalStatusForm.class);

    public LegalStatusForm() {
        super(LegalStatus.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel panel = new BasicFlexFormPanel();
        int row = -1;
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().status())).componentWidth("200px").build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().setOn())).componentWidth("200px").build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().setBy().name())).customLabel(i18n.tr("Set By")).componentWidth("200px").build());
        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().details())).componentWidth("200px").build());
        return panel;
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);
        boolean isNew = getValue().getPrimaryKey() == null;
        if (isNew) {
            get(proto().setOn()).setVisible(false);
            get(proto().setBy().name()).setVisible(false);
        }
    }

}
