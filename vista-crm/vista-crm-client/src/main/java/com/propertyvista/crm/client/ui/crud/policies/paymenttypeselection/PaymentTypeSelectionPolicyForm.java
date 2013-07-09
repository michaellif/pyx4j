/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-02-12
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.paymenttypeselection;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.panels.FormFlexPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;

public class PaymentTypeSelectionPolicyForm extends PolicyDTOTabPanelBasedForm<PaymentTypeSelectionPolicyDTO> {

    private static final I18n i18n = I18n.get(PaymentTypeSelectionPolicyForm.class);

    public PaymentTypeSelectionPolicyForm(IForm<PaymentTypeSelectionPolicyDTO> view) {
        super(PaymentTypeSelectionPolicyDTO.class, view);
    }

    @Override
    protected List<FormFlexPanel> createCustomTabPanels() {
        return Arrays.asList(createMiscPoliciesTab());
    }

    private FormFlexPanel createMiscPoliciesTab() {
        FormFlexPanel accepted = new FormFlexPanel();
        int col = -1;
        accepted.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Accepted:") + "</i>"));
        accepted.getWidget(0, col).setWidth("10em");
        accepted.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);

        accepted.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().acceptedCash()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        accepted.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().acceptedCheck()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        accepted.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().acceptedEcheck()), 6).labelWidth(6).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        accepted.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().acceptedEFT()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        accepted.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().acceptedCreditCard()), 7).labelWidth(7).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        accepted.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().acceptedInterac()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());

        FormFlexPanel residentPortal = new FormFlexPanel();
        col = -1;
        residentPortal.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Resident Portal:") + "</i>"));
        residentPortal.getWidget(0, col).setWidth("10em");
        residentPortal.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);

        residentPortal.setWidget(0, ++col, new HTML());
        residentPortal.getWidget(0, col).setWidth("5em");
        residentPortal.setWidget(0, ++col, new HTML());
        residentPortal.getWidget(0, col).setWidth("5em");

        residentPortal.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().residentPortalEcheck()), 6).labelWidth(6).layout(Layout.vertical)
                .labelAlignment(Alignment.center).componentAlignment(Alignment.center).build());
        residentPortal.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().residentPortalEFT()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        residentPortal.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().residentPortalCreditCard()), 7).labelWidth(7).layout(Layout.vertical)
                .labelAlignment(Alignment.center).componentAlignment(Alignment.center).build());
        residentPortal.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().residentPortalInterac()), 5).labelWidth(5).layout(Layout.vertical)
                .labelAlignment(Alignment.center).componentAlignment(Alignment.center).build());

        FormFlexPanel cashEquivalent = new FormFlexPanel();
        col = -1;
        cashEquivalent.setWidget(0, ++col, new HTML("<i>" + i18n.tr("Cash Equivalent:") + "</i>"));
        cashEquivalent.getWidget(0, col).setWidth("10em");
        cashEquivalent.getCellFormatter().setAlignment(0, col, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_MIDDLE);

        cashEquivalent.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().cashEquivalentCash()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        cashEquivalent.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().cashEquivalentCheck()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        cashEquivalent.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().cashEquivalentEcheck()), 6).labelWidth(6).layout(Layout.vertical)
                .labelAlignment(Alignment.center).componentAlignment(Alignment.center).build());
        cashEquivalent.setWidget(0, ++col,
                new FormDecoratorBuilder(inject(proto().cashEquivalentEFT()), 5).labelWidth(5).layout(Layout.vertical).labelAlignment(Alignment.center)
                        .componentAlignment(Alignment.center).build());
        cashEquivalent.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().cashEquivalentCreditCard()), 7).labelWidth(7).layout(Layout.vertical)
                .labelAlignment(Alignment.center).componentAlignment(Alignment.center).build());
        cashEquivalent.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().cashEquivalentInterac()), 5).labelWidth(5).layout(Layout.vertical)
                .labelAlignment(Alignment.center).componentAlignment(Alignment.center).build());

        // put all together:
        FormFlexPanel main = new FormFlexPanel(i18n.tr("Selection"));
        int row = -1;

        main.setWidget(++row, 0, accepted);
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, residentPortal);
        main.setHR(++row, 0, 1);
        main.setWidget(++row, 0, cashEquivalent);

        return main;
    }
}
