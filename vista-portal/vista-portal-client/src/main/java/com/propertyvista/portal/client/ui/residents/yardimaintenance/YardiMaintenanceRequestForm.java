/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 6, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.residents.yardimaintenance;

import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.VistaEditorsComponentFactory;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.dto.YardiServiceRequestDTO;

public class YardiMaintenanceRequestForm extends CEntityDecoratableForm<YardiServiceRequestDTO> {

    private static final I18n i18n = I18n.get(YardiMaintenanceRequestForm.class);

    public YardiMaintenanceRequestForm() {
        super(YardiServiceRequestDTO.class, new VistaEditorsComponentFactory());
    }

    @Override
    public IsWidget createContent() {
        FormFlexPanel content = new FormFlexPanel();

        int row = -1;

        content.setBR(++row, 0, 1);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().category()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().subCategory()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requestDescriptionBrief()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().requestDescriptionFull()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().priority()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().accessNotes()), 25).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().permissionToEnter()), 25).build());

        get(proto().permissionToEnter()).setNote(i18n.tr("To allow our service personnel to enter your apartment"));
        get(proto().accessNotes()).setNote(i18n.tr("Special instructions for entering the apartment"));
        get(proto().priority()).setNote(i18n.tr("Please rate the problem in terms of urgency"));

        return content;
    }
}
