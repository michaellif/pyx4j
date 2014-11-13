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

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.Key;
import com.pyx4j.forms.client.ui.CDateLabel;
import com.pyx4j.forms.client.ui.CEntityLabel;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CNumberLabel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.backoffice.ui.prime.form.IFormView;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.AutoPayHistoryDTO;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.security.common.VistaBasicBehavior;
import com.propertyvista.domain.tenant.lease.Tenant;

public class AutoPayHistoryForm extends CrmEntityForm<AutoPayHistoryDTO> {

    private static final I18n i18n = I18n.get(AutoPayHistoryForm.class);

    public AutoPayHistoryForm(IFormView<AutoPayHistoryDTO, ?> view) {
        super(AutoPayHistoryDTO.class, view);
        createTabs();
    }

    public void createTabs() {
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().tenant(), new CEntityLabel<Tenant>()).decorate().componentWidth(200);
        formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()).decorate().componentWidth(200);

        formPanel.append(Location.Left, proto().effectiveFrom(), new CDateLabel()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().expiredFrom(), new CDateLabel()).decorate().componentWidth(120);

        CLabel<Key> reviewOfPapLink = new CLabel<Key>();
        formPanel.append(Location.Left, proto().reviewOfPap().id(), reviewOfPapLink).decorate().componentWidth(150).customLabel(i18n.tr("Reviewed AutoPay"));
        reviewOfPapLink.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AutopayAgreement.class, getValue().reviewOfPap().getPrimaryKey());
                AppSite.getPlaceController().goTo(place);
            }
        });

        formPanel.append(Location.Right, proto().updatedByTenant()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().updatedBySystem()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate().componentWidth(200);
        formPanel.append(Location.Right, proto().creationDate()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().isDeleted()).decorate().componentWidth(70);

        formPanel.append(Location.Dual, proto().comments()).decorate();
        formPanel.append(Location.Dual, proto().auditDetails()).decorate();
        formPanel.append(Location.Dual, proto().coveredItems(), new PapCoveredItemFolder());

        selectTab(addTab(formPanel, i18n.tr("AutoPay")));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().updated()).setVisible(!getValue().updated().isNull());
        get(proto().reviewOfPap().id()).setVisible(!getValue().reviewOfPap().isNull());

        boolean support = SecurityController.check(VistaBasicBehavior.PropertyVistaSupport);
        get(proto().updatedByTenant()).setVisible(support);
        get(proto().updatedBySystem()).setVisible(support);

//        get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
    }
}