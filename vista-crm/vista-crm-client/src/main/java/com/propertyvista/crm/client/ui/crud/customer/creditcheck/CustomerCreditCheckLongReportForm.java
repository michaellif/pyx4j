/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-1-21
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.customer.creditcheck;

import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.crud.IFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckLongReportDTO;

public class CustomerCreditCheckLongReportForm extends CrmEntityForm<CustomerCreditCheckLongReportDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckLongReportForm.class);

    public CustomerCreditCheckLongReportForm(IFormView<CustomerCreditCheckLongReportDTO> view) {
        super(CustomerCreditCheckLongReportDTO.class, view);

        FormFlexPanel content = new FormFlexPanel(i18n.tr("General"));

        int row = -1;
//        content.setWidget(++row, 0, inject(proto().screening().screene().person().name(), new NameEditor(i18n.tr("Customer"))));

        content.setH1(++row, 0, 1, i18n.tr("QUICK SUMMARY"));
//        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().creditCheckDate())).build());
//        content.setWidget(++row, 0, new DecoratorBuilder(
//                inject(proto().createdBy(), new CEntityCrudHyperlink<Employee>(new CrmSiteMap.Organization.Employee()))).build());
//        content.setWidget(++row, 0, new DecoratorBuilder(inject(proto().amountChecked())).build());

        content.setH1(++row, 0, 1, i18n.tr("IDENTITY"));
        content.setH1(++row, 0, 1, i18n.tr("ACCOUNTS"));
        content.setH1(++row, 0, 1, i18n.tr("COURT JUDGEMENTS"));
        content.setH1(++row, 0, 1, i18n.tr("PROPOSALS AND BANKRUPTCIES"));
        content.setH1(++row, 0, 1, i18n.tr("EVICTIONS"));
        content.setH1(++row, 0, 1, i18n.tr("RENT HISTORY"));
        content.setH1(++row, 0, 1, i18n.tr("COLLECTIONS"));
        content.setH1(++row, 0, 1, i18n.tr("INQUIRIES"));

        selectTab(addTab(content));
    }
}
