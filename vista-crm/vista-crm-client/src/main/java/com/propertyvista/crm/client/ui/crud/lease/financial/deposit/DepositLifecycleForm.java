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
package com.propertyvista.crm.client.ui.crud.lease.financial.deposit;

import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.IPrimeFormView;

import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.dto.DepositLifecycleDTO;

public class DepositLifecycleForm extends CrmEntityForm<DepositLifecycleDTO> {

    private static final I18n i18n = I18n.get(DepositLifecycleForm.class);

    public DepositLifecycleForm(IPrimeFormView<DepositLifecycleDTO, ?> view) {
        super(DepositLifecycleDTO.class, view);

        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Dual, proto().deposit().billableItem()).decorate();

        formPanel.append(Location.Left, proto().deposit().type()).decorate().componentWidth(150);
        formPanel.append(Location.Right, proto().status()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().deposit().amount()).decorate().componentWidth(100);
        formPanel.append(Location.Right, proto().currentAmount()).decorate().componentWidth(100);

        formPanel.append(Location.Left, proto().depositDate()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().refundDate()).decorate().componentWidth(120);

        formPanel.append(Location.Dual, proto().deposit().description()).decorate();

        formPanel.br();
        formPanel.h2(proto().interestAdjustments().getMeta().getCaption());
        formPanel.append(Location.Dual, proto().interestAdjustments(), new DepositInterestAdjustmentFolder(isEditable()));

        // tweaking:
        get(proto().deposit().billableItem()).setViewable(true);
        get(proto().deposit().type()).setViewable(true);
        get(proto().deposit().amount()).setViewable(true);
        get(proto().deposit().description()).setViewable(true);

        get(proto().status()).setViewable(true);
        get(proto().depositDate()).setViewable(true);
        get(proto().refundDate()).setViewable(true);
        get(proto().currentAmount()).setViewable(true);

        selectTab(addTab(formPanel, i18n.tr("General")));
        setTabBarVisible(false);
    }
}