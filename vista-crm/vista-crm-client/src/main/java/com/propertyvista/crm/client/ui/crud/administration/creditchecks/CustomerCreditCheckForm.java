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
package com.propertyvista.crm.client.ui.crud.administration.creditchecks;

import com.google.gwt.user.client.Command;

import com.pyx4j.entity.core.EntityFactory;
import com.pyx4j.forms.client.ui.CEntityHyperlink;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.backoffice.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.crm.rpc.dto.tenant.CustomerCreditCheckDTO;
import com.propertyvista.domain.company.Employee;
import com.propertyvista.domain.person.Name;
import com.propertyvista.domain.tenant.CustomerCreditCheck.CreditCheckResult;
import com.propertyvista.dto.LeaseApplicationDTO;

public class CustomerCreditCheckForm extends CrmEntityForm<CustomerCreditCheckDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckForm.class);

    public CustomerCreditCheckForm(IForm<CustomerCreditCheckDTO> view) {
        super(CustomerCreditCheckDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().screening().screene().person().name(), new CEntityHyperlink<Name>(new Command() {
            @Override
            public void execute() {
                CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(LeaseApplicationDTO.class);
                place.formListerPlace().queryArg(
                        EntityFactory.getEntityPrototype(LeaseApplicationDTO.class).leaseParticipants().$().customer().customerId().getPath().toString(),
                        getValue().screening().screene().customerId().getValue().toString());
                AppSite.getPlaceController().goTo(place);
            }
        })).decorate().customLabel(i18n.tr("Customer"));

        formPanel.h1(i18n.tr("Details"));
        formPanel.append(Location.Left, proto().creditCheckDate()).decorate();
        formPanel.append(Location.Left, proto().createdBy(), new CEntityCrudHyperlink<Employee>(new CrmSiteMap.Organization.Employee())).decorate();
        formPanel.append(Location.Left, proto().amountChecked()).decorate();
        formPanel.append(Location.Left, proto().transaction().status()).decorate();

        formPanel.h1(i18n.tr("Results From Equifax"));
        formPanel.append(Location.Left, proto().riskCode()).decorate();
        formPanel.append(Location.Left, proto().creditCheckResult()).decorate();
        formPanel.append(Location.Left, proto().amountApproved()).decorate();
        formPanel.append(Location.Left, proto().reason()).decorate();

        formPanel.h1(i18n.tr("Fees"));
        formPanel.append(Location.Left, proto().transaction().amount()).decorate();
        formPanel.append(Location.Left, proto().transaction().tax()).decorate();
        formPanel.append(Location.Left, proto().transactionRef()).decorate();

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
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
