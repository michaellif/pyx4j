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
import com.pyx4j.forms.client.ui.panels.TwoColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.BasicCFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.IForm;
import com.pyx4j.site.rpc.CrudAppPlace;

import com.propertyvista.common.client.ui.components.folders.PapCoveredItemFolder;
import com.propertyvista.crm.client.ui.crud.CrmEntityForm;
import com.propertyvista.crm.rpc.dto.financial.AutoPayDTO;
import com.propertyvista.domain.payment.AutopayAgreement;
import com.propertyvista.domain.payment.LeasePaymentMethod;
import com.propertyvista.domain.security.VistaCrmBehavior;
import com.propertyvista.domain.security.common.AbstractPmcUser;
import com.propertyvista.domain.tenant.lease.Tenant;

public class PapForm extends CrmEntityForm<AutoPayDTO> {

    private static final I18n i18n = I18n.get(PapForm.class);

    public PapForm(IForm<AutoPayDTO> view) {
        super(AutoPayDTO.class, view);
        createTabs();
    }

    public void createTabs() {
        BasicCFormPanel formPanel = new BasicCFormPanel(this);

        formPanel.append(Location.Left, proto().id(), new CNumberLabel()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().tenant(), new CEntityLabel<Tenant>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>()).decorate().componentWidth(250);

        formPanel.append(Location.Left, proto().effectiveFrom(), new CDateLabel()).decorate().componentWidth(120);
        formPanel.append(Location.Left, proto().expiredFrom(), new CDateLabel()).decorate().componentWidth(120);

        formPanel.append(Location.Left, proto().createdBy(), new CEntityLabel<AbstractPmcUser>()).decorate().componentWidth(250);
        formPanel.append(Location.Left, proto().creationDate()).decorate().componentWidth(180);

        formPanel.append(Location.Right, proto().updatedByTenant()).decorate().componentWidth(120);
        formPanel.append(Location.Right, proto().updatedBySystem()).decorate().componentWidth(120);

        formPanel.append(Location.Right, proto().updated()).decorate().componentWidth(180);
        formPanel.append(Location.Right, proto().isDeleted()).decorate().componentWidth(70);
        CLabel<Key> reviewOfPapLink = new CLabel<Key>();
        formPanel.append(Location.Right, proto().reviewOfPap().id(), reviewOfPapLink).decorate().componentWidth(150).customLabel(i18n.tr("Reviewed AutoPay"));
        reviewOfPapLink.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AutopayAgreement.class, getValue().reviewOfPap().getPrimaryKey());
                AppSite.getPlaceController().goTo(place);
            }
        });
        formPanel.append(Location.Full, proto().coveredItems(), new PapCoveredItemFolder());

        selectTab(addTab(formPanel, i18n.tr("AutoPay")));
        setTabBarVisible(false);
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        get(proto().updated()).setVisible(!getValue().updated().isNull());

        boolean support = SecurityController.checkBehavior(VistaCrmBehavior.PropertyVistaSupport);
        get(proto().updatedByTenant()).setVisible(support);
        get(proto().updatedBySystem()).setVisible(support);

//        get(proto().createdBy()).setVisible(!getValue().createdBy().isNull());
    }
}