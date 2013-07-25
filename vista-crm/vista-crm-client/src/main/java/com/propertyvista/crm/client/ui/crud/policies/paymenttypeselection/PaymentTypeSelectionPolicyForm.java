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

import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Layout;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
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
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createMiscPoliciesTab());
    }

    private TwoColumnFlexFormPanel createMiscPoliciesTab() {
        BasicFlexFormPanel accepted = new BasicFlexFormPanel();

        String hdrW = "120px";
        String contW = "120px";
        String lblW = "120px";
        int col = -1;
        accepted.setH4(0, ++col, 1, i18n.tr("Accepted:"));
        accepted.getWidget(0, col).setWidth(hdrW);

        accepted.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().acceptedCash())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        accepted.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().acceptedCheck())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        accepted.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().acceptedEcheck())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        accepted.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().acceptedEFT())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());
        accepted.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().acceptedCreditCard())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        accepted.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().acceptedInterac())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());

        BasicFlexFormPanel residentPortal = new BasicFlexFormPanel();
        col = -1;
        residentPortal.setH4(0, ++col, 1, i18n.tr("Resident Portal:"));
        residentPortal.getWidget(0, col).setWidth(hdrW);

        residentPortal.setWidget(0, ++col, new HTML());
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, new HTML());
        residentPortal.getWidget(0, col).setWidth(lblW);

        residentPortal.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().residentPortalEcheck())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());
        residentPortal.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().residentPortalEFT())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        residentPortal.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().residentPortalCreditCard())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());
        residentPortal.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().residentPortalInterac())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());

        BasicFlexFormPanel cashEquivalent = new BasicFlexFormPanel();
        col = -1;
        cashEquivalent.setH4(0, ++col, 1, i18n.tr("Cash Equivalent:"));
        cashEquivalent.getWidget(0, col).setWidth(hdrW);

        cashEquivalent.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().cashEquivalentCash())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        cashEquivalent.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().cashEquivalentCheck())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        cashEquivalent.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().cashEquivalentEcheck())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());
        cashEquivalent.setWidget(
                0,
                ++col,
                new FormDecoratorBuilder(inject(proto().cashEquivalentEFT())).contentWidth(contW).labelWidth(lblW).labelAlignment(Alignment.left)
                        .layout(Layout.vertical).build());
        cashEquivalent.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().cashEquivalentCreditCard())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());
        cashEquivalent.setWidget(0, ++col, new FormDecoratorBuilder(inject(proto().cashEquivalentInterac())).contentWidth(contW).labelWidth(lblW)
                .labelAlignment(Alignment.left).layout(Layout.vertical).build());

        // put all together:
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Selection"));
        int row = -1;

        main.setWidget(++row, 0, 2, accepted);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, residentPortal);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, cashEquivalent);

        return main;
    }
}
