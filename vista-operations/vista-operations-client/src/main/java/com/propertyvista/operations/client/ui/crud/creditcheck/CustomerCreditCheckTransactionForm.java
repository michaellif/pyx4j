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
package com.propertyvista.operations.client.ui.crud.creditcheck;

import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.backoffice.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.ui.backoffice.prime.form.IForm;

import com.propertyvista.domain.pmc.Pmc;
import com.propertyvista.domain.pmc.PmcPaymentMethod;
import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.rpc.OperationsSiteMap;
import com.propertyvista.operations.rpc.dto.CustomerCreditCheckTransactionDTO;

public class CustomerCreditCheckTransactionForm extends OperationsEntityForm<CustomerCreditCheckTransactionDTO> {

    private static final I18n i18n = I18n.get(CustomerCreditCheckTransactionForm.class);

    public CustomerCreditCheckTransactionForm(IForm<CustomerCreditCheckTransactionDTO> view) {
        super(CustomerCreditCheckTransactionDTO.class, view);

        FormPanel formPanel = new FormPanel(this);
        formPanel.append(Location.Left, inject(proto().pmc(), new CEntityCrudHyperlink<Pmc>(new OperationsSiteMap.Management.PMC())));

        formPanel.h1(i18n.tr("Details"));
        formPanel.append(Location.Left, proto().amount()).decorate();
        formPanel.append(Location.Left, proto().tax()).decorate();
        formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<PmcPaymentMethod>()).decorate();
        formPanel.append(Location.Left, proto().status()).decorate();
        formPanel.append(Location.Left, proto().transactionAuthorizationNumber()).decorate();
        formPanel.append(Location.Left, proto().transactionDate()).decorate();

        setTabBarVisible(false);
        selectTab(addTab(formPanel, i18n.tr("General")));
    }
}