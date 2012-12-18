/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-17
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.settings.creditchecks;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;
import com.pyx4j.site.client.ui.crud.misc.CEntityCrudHyperlink;

import com.propertyvista.common.client.ui.components.editors.NameEditor;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.CustomerCreditCheckDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;

public class CustomerCreditCheckForm extends CrmEntityForm<CustomerCreditCheckDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckForm.class);

    public CustomerCreditCheckForm(IFormView<CustomerCreditCheckDTO> view) {
        super(CustomerCreditCheckDTO.class, view);

        FormFlexPanel content = new FormFlexPanel();
        int row = -1;
        content.setH1(++row, 0, 1, i18n.tr("Screene"));
        content.setWidget(++row, 0, inject(proto().screening().screene().person().name(), new NameEditor()));

        content.setH1(++row, 0, 1, i18n.tr("Details"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheckDate())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(
                inject(proto().createdBy(), new CEntityCrudHyperlink<Employee>(new CrmSiteMap.Organization.Employee()))).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amountChecked())).build());

        content.setH1(++row, 0, 1, i18n.tr("Results From Equifax"));
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().riskCode())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheckResult())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amountApproved())).build());
        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().reason())).build());

        selectTab(addTab(content));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        CreditCheckResult creditCheckResult = getValue().creditCheckResult().getValue();

        get(proto().creditCheckResult()).setVisible(creditCheckResult != CreditCheckResult.Accept);
        get(proto().amountApproved()).setVisible(creditCheckResult == CreditCheckResult.Accept);
        get(proto().reason()).setVisible(creditCheckResult != CreditCheckResult.Accept);
    }
}
