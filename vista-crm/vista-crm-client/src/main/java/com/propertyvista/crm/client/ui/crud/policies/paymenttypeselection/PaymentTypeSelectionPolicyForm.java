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

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.DualColumnForm;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.theme.VistaTheme;
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
        addTab(createMiscPoliciesTab(), i18n.tr("General"));

    }

    private IsWidget createMiscPoliciesTab() {
        BasicFlexFormPanel accepted = new BasicFlexFormPanel();
        int col = -1;
        accepted.setH4(0, ++col, 1, i18n.tr("Accepted:"));
        accepted.getWidget(0, col).setWidth(hdrW);

        accepted.setWidget(0, ++col, inject(proto().acceptedCash(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedCheck(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedEcheck(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedDirectBanking(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedCreditCardMasterCard(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedCreditCardVisa(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedVisaDebit(), decorator()));
        accepted.setWidget(0, ++col, inject(proto().acceptedInterac(), decorator()));

        final String convenienceMarker = " *";

        BasicFlexFormPanel residentPortal = new BasicFlexFormPanel();
        col = -1;
        residentPortal.setH4(0, ++col, 1, i18n.tr("Resident Portal:"));
        residentPortal.getWidget(0, col).setWidth(hdrW);

        residentPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, inject(proto().residentPortalEcheck(), decorator()));
        residentPortal.setWidget(0, ++col, inject(proto().residentPortalDirectBanking(), decorator()));
        residentPortal.setWidget(
                0,
                ++col,
                inject(proto().residentPortalCreditCardMasterCard(), decorator(proto().residentPortalCreditCardMasterCard().getMeta().getCaption()
                        + convenienceMarker)));
        residentPortal.setWidget(0, ++col,
                inject(proto().residentPortalCreditCardVisa(), decorator(proto().residentPortalCreditCardVisa().getMeta().getCaption() + convenienceMarker)));
        residentPortal.setWidget(
                0,
                ++col,
                inject(proto().residentPortalVisaDebit(), decorator(proto().residentPortalVisaDebit().getMeta().getCaption()
                        + (VistaTODO.visaDebitHasConvenienceFee ? convenienceMarker : ""))));
        residentPortal.setWidget(0, ++col, inject(proto().residentPortalInterac(), decorator()));

        BasicFlexFormPanel prospectPortal = new BasicFlexFormPanel();
        col = -1;
        prospectPortal.setH4(0, ++col, 1, i18n.tr("Prospect Portal:"));
        prospectPortal.getWidget(0, col).setWidth(hdrW);

        prospectPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        prospectPortal.getWidget(0, col).setWidth(lblW);
        prospectPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        prospectPortal.getWidget(0, col).setWidth(lblW);
        prospectPortal.setWidget(0, ++col, inject(proto().prospectEcheck(), decorator()));
        prospectPortal.setWidget(0, ++col, new HTML());
        prospectPortal.getWidget(0, col).setWidth(lblW);
        prospectPortal.setWidget(0, ++col,
                inject(proto().prospectCreditCardMasterCard(), decorator(proto().prospectCreditCardMasterCard().getMeta().getCaption() + convenienceMarker)));
        prospectPortal.setWidget(0, ++col,
                inject(proto().prospectCreditCardVisa(), decorator(proto().prospectCreditCardVisa().getMeta().getCaption() + convenienceMarker)));
        prospectPortal.setWidget(
                0,
                ++col,
                inject(proto().prospectVisaDebit(), decorator(proto().prospectVisaDebit().getMeta().getCaption()
                        + (VistaTODO.visaDebitHasConvenienceFee ? convenienceMarker : ""))));
        prospectPortal.setWidget(0, ++col, new HTML());

        BasicFlexFormPanel cashEquivalent = new BasicFlexFormPanel();
        col = -1;
        cashEquivalent.setH4(0, ++col, 1, i18n.tr("Cash Equivalent:"));
        cashEquivalent.getWidget(0, col).setWidth(hdrW);

        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentCash(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentCheck(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentEcheck(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentDirectBanking(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentCreditCardMasterCard(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentCreditCardVisa(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentVisaDebit(), decorator()));
        cashEquivalent.setWidget(0, ++col, inject(proto().cashEquivalentInterac(), decorator()));

        // put all together:
        DualColumnForm formPanel = new DualColumnForm(this);

        formPanel.append(Location.Left, accepted);
        formPanel.hr();
        formPanel.append(Location.Left, residentPortal);
        formPanel.hr();
        formPanel.append(Location.Left, prospectPortal);
        formPanel.hr();
        formPanel.append(Location.Left, cashEquivalent);
        formPanel.hr();
        formPanel.br();
        HTML html;
        formPanel.append(Location.Left,
                html = new HTML(i18n.tr("Note: If payment marked with {0} is not selected, a Web Payment Fee will apply to the Resident.", convenienceMarker)));
        html.setStyleName(VistaTheme.StyleName.InfoMessage.name());

        return formPanel;
    }

    private FieldDecorator decorator(String customLabel) {
        return new FieldDecoratorBuilder(lblW, contW).labelPosition(LabelPosition.top).labelAlignment(Alignment.center).useLabelSemicolon(false)
                .componentAlignment(Alignment.center).customLabel(customLabel).build();
    }

    private FieldDecorator decorator() {
        return decorator(null);
    }
}
