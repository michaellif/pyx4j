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

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.WidgetDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.common.client.ui.decorations.FormDecoratorBuilder;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;
import com.propertyvista.misc.VistaTODO;

public class PaymentTypeSelectionPolicyForm extends PolicyDTOTabPanelBasedForm<PaymentTypeSelectionPolicyDTO> {

    private static final I18n i18n = I18n.get(PaymentTypeSelectionPolicyForm.class);

    private static final String hdrW = "120px";

    private static final String contW = "120px";

    private static final String lblW = "120px";

    public PaymentTypeSelectionPolicyForm(IForm<PaymentTypeSelectionPolicyDTO> view) {
        super(PaymentTypeSelectionPolicyDTO.class, view);
    }

    @Override
    protected List<TwoColumnFlexFormPanel> createCustomTabPanels() {
        return Arrays.asList(createMiscPoliciesTab());
    }

    private TwoColumnFlexFormPanel createMiscPoliciesTab() {
        BasicFlexFormPanel accepted = new BasicFlexFormPanel();
        int col = -1;
        accepted.setH4(0, ++col, 1, i18n.tr("Accepted:"));
        accepted.getWidget(0, col).setWidth(hdrW);

        accepted.setWidget(0, ++col, aligned(proto().acceptedCash()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedCheck()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedEcheck()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedDirectBanking()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedCreditCardMasterCard()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedCreditCardVisa()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedVisaDebit()).build());
        accepted.setWidget(0, ++col, aligned(proto().acceptedInterac()).build());

        final String convenienceMarker = " *";

        BasicFlexFormPanel residentPortal = new BasicFlexFormPanel();
        col = -1;
        residentPortal.setH4(0, ++col, 1, i18n.tr("Resident Portal:"));
        residentPortal.getWidget(0, col).setWidth(hdrW);

        residentPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, aligned(proto().residentPortalEcheck()).build());
        residentPortal.setWidget(0, ++col, aligned(proto().residentPortalDirectBanking()).build());
        residentPortal.setWidget(
                0,
                ++col,
                aligned(proto().residentPortalCreditCardMasterCard()).customLabel(
                        proto().residentPortalCreditCardMasterCard().getMeta().getCaption() + convenienceMarker).build());
        residentPortal.setWidget(0, ++col,
                aligned(proto().residentPortalCreditCardVisa()).customLabel(proto().residentPortalCreditCardVisa().getMeta().getCaption() + convenienceMarker)
                        .build());
        residentPortal.setWidget(
                0,
                ++col,
                aligned(proto().residentPortalVisaDebit()).customLabel(
                        proto().residentPortalVisaDebit().getMeta().getCaption() + (VistaTODO.visaDebitHasConvenienceFee ? convenienceMarker : "")).build());
        residentPortal.setWidget(0, ++col, aligned(proto().residentPortalInterac()).build());

        BasicFlexFormPanel prospectPortal = new BasicFlexFormPanel();
        col = -1;
        prospectPortal.setH4(0, ++col, 1, i18n.tr("Prospect Portal:"));
        prospectPortal.getWidget(0, col).setWidth(hdrW);

        prospectPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        prospectPortal.getWidget(0, col).setWidth(lblW);
        prospectPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        prospectPortal.getWidget(0, col).setWidth(lblW);
        prospectPortal.setWidget(0, ++col, aligned(proto().prospectEcheck()).build());
        prospectPortal.setWidget(0, ++col, new HTML());
        prospectPortal.setWidget(0, ++col,
                aligned(proto().prospectCreditCardMasterCard()).customLabel(proto().prospectCreditCardMasterCard().getMeta().getCaption() + convenienceMarker)
                        .build());
        prospectPortal.setWidget(0, ++col,
                aligned(proto().prospectCreditCardVisa()).customLabel(proto().prospectCreditCardVisa().getMeta().getCaption() + convenienceMarker).build());
        prospectPortal.setWidget(
                0,
                ++col,
                aligned(proto().prospectVisaDebit()).customLabel(
                        proto().prospectVisaDebit().getMeta().getCaption() + (VistaTODO.visaDebitHasConvenienceFee ? convenienceMarker : "")).build());
        prospectPortal.setWidget(0, ++col, new HTML());

        BasicFlexFormPanel cashEquivalent = new BasicFlexFormPanel();
        col = -1;
        cashEquivalent.setH4(0, ++col, 1, i18n.tr("Cash Equivalent:"));
        cashEquivalent.getWidget(0, col).setWidth(hdrW);

        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentCash()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentCheck()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentEcheck()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentDirectBanking()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentCreditCardMasterCard()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentCreditCardVisa()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentVisaDebit()).build());
        cashEquivalent.setWidget(0, ++col, aligned(proto().cashEquivalentInterac()).build());

        // put all together:
        TwoColumnFlexFormPanel main = new TwoColumnFlexFormPanel(i18n.tr("Selection"));
        int row = -1;

        main.setWidget(++row, 0, 2, accepted);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, residentPortal);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, prospectPortal);
        main.setHR(++row, 0, 2);
        main.setWidget(++row, 0, 2, cashEquivalent);
        main.setHR(++row, 0, 2);
        main.setBR(++row, 0, 2);
        main.setWidget(++row, 0, 2,
                new HTML(i18n.tr("Note: If payment marked with {0} is not selected, a Web Payment Fee will apply to the Resident.", convenienceMarker)));
        main.getWidget(row, 0).setStyleName(VistaTheme.StyleName.InfoMessage.name());

        return main;
    }

    private WidgetDecorator.Builder aligned(IPrimitive<Boolean> check) {
        return new FormDecoratorBuilder(inject(check), lblW, contW, contW).labelPosition(LabelPosition.top).labelAlignment(Alignment.center)
                .useLabelSemicolon(false).componentAlignment(Alignment.center);
    }
}
