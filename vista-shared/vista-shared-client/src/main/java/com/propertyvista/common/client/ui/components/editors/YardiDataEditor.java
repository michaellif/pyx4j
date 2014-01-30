/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 21, 2013
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.domain.tenant.lease.extradata.YardiLeaseChargeData;

public class YardiDataEditor extends CEntityForm<YardiLeaseChargeData> {

    private static final I18n i18n = I18n.get(YardiDataEditor.class);

    public YardiDataEditor() {
        super(YardiLeaseChargeData.class);
        setViewable(true);
        inheritViewable(false);
    }

    public YardiDataEditor(IEditableComponentFactory factory) {
        super(YardiLeaseChargeData.class, factory);
        setViewable(true);
        inheritViewable(false);
    }

    @Override
    public IsWidget createContent() {
        TwoColumnFlexFormPanel panel = new TwoColumnFlexFormPanel();

        int row = -1;
        panel.setH3(++row, 0, 2, i18n.tr("Yardi Data"));

        panel.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().chargeCode()), 15).build());

        return panel;
    }
}
