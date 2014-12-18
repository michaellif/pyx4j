/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 */
package com.propertyvista.operations.client.ui.crud.tenantsure;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.backoffice.ui.prime.CEntityCrudHyperlink;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.operations.client.ui.crud.OperationsEntityForm;
import com.propertyvista.operations.domain.tenantsure.TenantSureSubscribers;
import com.propertyvista.operations.rpc.dto.TenantSureDTO;

public class TenantSureForm extends OperationsEntityForm<TenantSureDTO> {

    private static final I18n i18n = I18n.get(TenantSureForm.class);

    public TenantSureForm(IPrimeFormView<TenantSureDTO, ?> view) {
        super(TenantSureDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().pmc().name()).decorate().customLabel("PMC:");

        formPanel.append(Location.Left, proto().propertyCode()).decorate();
        formPanel.append(Location.Left, proto().propertySuspended()).decorate();
        formPanel.append(Location.Left, proto().certificateNumber()).decorate();
        formPanel.append(Location.Left, proto().policy().tenant().customer().person().name()).decorate();

        formPanel.h3(i18n.tr("Status"));
        formPanel.append(Location.Left, proto().policy().status()).decorate();
        formPanel.append(Location.Left, proto().policy().cancellation()).decorate();
        formPanel.append(Location.Left, proto().policy().cancellationDate()).decorate();

        formPanel.append(Location.Left, proto().policy().renewalOf(),
                new CEntityCrudHyperlink<>(AppPlaceEntityMapper.resolvePlace(TenantSureSubscribers.class))).decorate();
        formPanel.append(Location.Left, proto().policy().renewal(), new CEntityCrudHyperlink<>(AppPlaceEntityMapper.resolvePlace(TenantSureSubscribers.class)))
                .decorate();

        formPanel.h3(i18n.tr("Operational"));
        formPanel.append(Location.Left, proto().policy().paymentSchedule()).decorate();
        formPanel.append(Location.Left, proto().policy().paymentDay()).decorate();
        formPanel.append(Location.Left, proto().policy().totalMonthlyPayable()).decorate();
        formPanel.append(Location.Left, proto().policy().annualPremium()).decorate();

        formPanel.h3(i18n.tr("Coverage"));
        formPanel.append(Location.Left, proto().policy().certificate().inceptionDate()).decorate();
        formPanel.append(Location.Left, proto().policy().certificate().expiryDate()).decorate();
        formPanel.append(Location.Left, proto().policy().certificate().liabilityCoverage()).decorate();
        formPanel.append(Location.Left, proto().policy().contentsCoverage()).decorate();

        selectTab(addTab(formPanel, i18n.tr("Details")));

        // --------------------------------------------------------------------------------------------

        addTab(((TenantSureViewerView) getParentView()).getTransactionListerView(), i18n.tr("Transactions"));
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().policy().cancellation()).setVisible(!getValue().policy().cancellation().isNull());
        get(proto().policy().cancellationDate()).setVisible(!getValue().policy().cancellationDate().isNull());
        get(proto().policy().renewalOf()).setVisible(!getValue().policy().renewalOf().isNull());
        get(proto().policy().renewal()).setVisible(!getValue().policy().renewal().isNull());
    }
}
