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
package com.propertyvista.crm.client.ui.crud.financial.paps;

import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PapForm extends CrmEntityForm<AutopayAgreement> {

    private static final I18n i18n = I18n.get(PapForm.class);

    public PapForm(IForm<AutopayAgreement> view) {
        super(AutopayAgreement.class, view);
        createTabs();
    }

    public void createTabs() {
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel(i18n.tr("General"));

        int row = -1;
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().id(), new CNumberLabel()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().tenant(), new CEntityLabel<Tenant>()), 22).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()), 22).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().effectiveFrom(), new CDateLabel()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().expiredFrom(), new CDateLabel()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().createdBy(), new CEntityLabel<AbstractPmcUser>()), 22).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().creationDate(), new CDateLabel()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updatedByTenant(), new CDateLabel()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updatedBySystem(), new CDateLabel()), 10).build());

        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().updated(), new CDateLabel()), 10).build());
        content.setWidget(++row, 0, new FormDecoratorBuilder(inject(proto().isDeleted()), 10).build());

        content.setWidget(++row, 0, 2, inject(proto().coveredItems(), new PapCoveredItemFolder()));

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

//        get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
    }
}