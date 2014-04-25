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
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppPlaceEntityMapper;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
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
        TwoColumnFlexFormPanel content = new TwoColumnFlexFormPanel();

        int row = -1;
        content.setWidget(++row, 0, inject(proto().id(), new CNumberLabel(), new FieldDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().tenant(), new CEntityLabel<Tenant>(), new FieldDecoratorBuilder(22).build()));
        content.setWidget(++row, 0, inject(proto().paymentMethod(), new CEntityLabel<LeasePaymentMethod>(), new FieldDecoratorBuilder(22).build()));

        content.setWidget(++row, 0, inject(proto().effectiveFrom(), new CDateLabel(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().expiredFrom(), new CDateLabel(), new FieldDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().createdBy(), new CEntityLabel<AbstractPmcUser>(), new FieldDecoratorBuilder(22).build()));
        content.setWidget(++row, 0, inject(proto().creationDate(), new FieldDecoratorBuilder(15).build()));

        content.setWidget(++row, 0, inject(proto().updatedByTenant(), new FieldDecoratorBuilder(10).build()));
        content.setWidget(++row, 0, inject(proto().updatedBySystem(), new FieldDecoratorBuilder(10).build()));

        content.setWidget(++row, 0, inject(proto().updated(), new FieldDecoratorBuilder(15).build()));
        content.setWidget(++row, 0, inject(proto().isDeleted(), new FieldDecoratorBuilder(5).build()));
        CLabel<Key> reviewOfPapLink;
        content.setWidget(
                ++row,
                0,
                reviewOfPapLink = inject(proto().reviewOfPap().id(), new CLabel<Key>(),
                        new FieldDecoratorBuilder(5).customLabel(i18n.tr("Reviewed AutoPay")).build()));
        reviewOfPapLink.setNavigationCommand(new Command() {
            @Override
            public void execute() {
                CrudAppPlace place = AppPlaceEntityMapper.resolvePlace(AutopayAgreement.class, getValue().reviewOfPap().getPrimaryKey());
                AppSite.getPlaceController().goTo(place);
            }
        });
        content.setWidget(++row, 0, 2, inject(proto().coveredItems(), new PapCoveredItemFolder()));

        selectTab(addTab(content, i18n.tr("AutoPay")));
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