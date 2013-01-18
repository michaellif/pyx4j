/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-25
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.deposit;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.DepositLifecycleDTO;

public class DepositLifecycleForm extends CrmEntityForm<DepositLifecycleDTO> {

    private static final I18n i18n = I18n.get(DepositLifecycleForm.class);

    public DepositLifecycleForm(IFormView<DepositLifecycleDTO> view) {
        super(DepositLifecycleDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deposit().billableItem()), 30).build());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deposit().type()), 12).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().status()), 9).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deposit().amount()), 7).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().currentAmount()), 7).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().depositDate()), 9).build());
        content.setWidget(row, 1, new DecoratorBuilder(inject(proto().refundDate()), 9).build());

        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().deposit().description()), 30).build());
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        content.setBR(++row, 0, 2);
        content.setH2(++row, 0, 2, proto().interestAdjustments().getMeta().getCaption());
        content.setWidget(++row, 0, inject(proto().interestAdjustments(), new DepositInterestAdjustmentFolder(isEditable())));
        content.getFlexCellFormatter().setColSpan(row, 0, 2);

        // tweaking:
        get(proto().deposit().billableItem()).setViewable(true);
        get(proto().deposit().type()).setViewable(true);
        get(proto().deposit().amount()).setViewable(true);
        get(proto().deposit().description()).setViewable(true);

        get(proto().status()).setViewable(true);
        get(proto().depositDate()).setViewable(true);
        get(proto().refundDate()).setViewable(true);
        get(proto().currentAmount()).setViewable(true);

        content.getColumnFormatter().setWidth(0, VistaTheme.columnWidth);

        selectTab(addTab(content));
    }
}