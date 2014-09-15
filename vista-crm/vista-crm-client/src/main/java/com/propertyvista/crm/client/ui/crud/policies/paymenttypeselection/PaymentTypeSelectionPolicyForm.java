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
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.Alignment;
import com.pyx4j.forms.client.ui.decorators.FieldDecorator.Builder.LabelPosition;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.backoffice.ui.prime.form.FieldDecoratorBuilder;
import com.pyx4j.site.client.backoffice.ui.prime.form.IForm;

import com.propertyvista.common.client.theme.VistaTheme;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.pmc.fee.AbstractPaymentSetup;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;
import com.propertyvista.misc.VistaTODO;

public class PaymentTypeSelectionPolicyForm extends PolicyDTOTabPanelBasedForm<PaymentTypeSelectionPolicyDTO> {

    private static final I18n i18n = I18n.get(PaymentTypeSelectionPolicyForm.class);

    private static final String hdrW = "120px";

    private static final String contW = "120px";

    private static final String lblW = "120px";

    private static final String convenienceMarker = " *";

    private HTML convenienceNoticeHtml;

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
        residentPortal.setWidget(0, ++col, inject(proto().residentPortalCreditCardMasterCard(), decorator()));
        residentPortal.setWidget(0, ++col, inject(proto().residentPortalCreditCardVisa(), decorator()));
        residentPortal.setWidget(0, ++col, inject(proto().residentPortalVisaDebit(), decorator()));
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
        prospectPortal.setWidget(0, ++col, inject(proto().prospectCreditCardMasterCard(), decorator()));
        prospectPortal.setWidget(0, ++col, inject(proto().prospectCreditCardVisa(), decorator()));
        prospectPortal.setWidget(0, ++col, inject(proto().prospectVisaDebit(), decorator()));
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
        FormPanel formPanel = new FormPanel(this);

        formPanel.append(Location.Left, accepted);
        formPanel.hr();
        formPanel.append(Location.Left, residentPortal);
        formPanel.hr();
        formPanel.append(Location.Left, prospectPortal);
        formPanel.hr();
        formPanel.append(Location.Left, cashEquivalent);
        formPanel.hr();
        formPanel.br();

        formPanel.append(
                Location.Left,
                convenienceNoticeHtml = new HTML(i18n.tr("Note: If payment marked with {0} is not selected, a Web Payment Fee will apply to the Resident.",
                        convenienceMarker)));
        convenienceNoticeHtml.setStyleName(VistaTheme.StyleName.InfoMessage.name());

        return formPanel;
    }

    private FieldDecorator decorator() {
        return new FieldDecoratorBuilder(lblW, contW).labelPosition(LabelPosition.top).labelAlignment(Alignment.center).useLabelSemicolon(false)
                .componentAlignment(Alignment.center).build();
    }

    @Override
    protected void onValueSet(boolean populate) {
        super.onValueSet(populate);

        setConvenienceMarker(proto().prospectCreditCardVisa(), pmcPaymentSetup().acceptedVisaConvenienceFee().getValue());
        setConvenienceMarker(proto().residentPortalCreditCardVisa(), pmcPaymentSetup().acceptedVisaConvenienceFee().getValue());

        setConvenienceMarker(proto().prospectCreditCardMasterCard(), pmcPaymentSetup().acceptedMasterCardConvenienceFee().getValue());
        setConvenienceMarker(proto().residentPortalCreditCardMasterCard(), pmcPaymentSetup().acceptedMasterCardConvenienceFee().getValue());

        if (VistaTODO.visaDebitHasConvenienceFee) {
            setConvenienceMarker(proto().prospectVisaDebit(), pmcPaymentSetup().acceptedVisaDebitConvenienceFee().getValue());
            setConvenienceMarker(proto().residentPortalCreditCardMasterCard(), pmcPaymentSetup().acceptedVisaDebitConvenienceFee().getValue());
        }

        convenienceNoticeHtml.setVisible(pmcPaymentSetup().acceptedVisaConvenienceFee().getValue()
                || pmcPaymentSetup().acceptedMasterCardConvenienceFee().getValue() || pmcPaymentSetup().acceptedVisaDebitConvenienceFee().getValue());

        setEnabled(membersEcheck(), pmcPaymentSetup().acceptedEcheck().getValue());
        setEnabled(membersDirectBanking(), pmcPaymentSetup().acceptedDirectBanking().getValue());
        setEnabled(membersCreditCardVisa(), pmcPaymentSetup().acceptedVisa().getValue());
        setEnabled(membersCreditCardMasterCard(), pmcPaymentSetup().acceptedMasterCard().getValue());
        setEnabled(membersVisaDebit(), pmcPaymentSetup().acceptedVisaDebit().getValue());
    }

    private AbstractPaymentSetup pmcPaymentSetup() {
        return getValue().pmcPaymentSetup();
    }

    List<IPrimitive<Boolean>> membersEcheck() {
        return Arrays.asList(proto().acceptedEcheck(), proto().residentPortalEcheck(), proto().prospectEcheck(), proto().cashEquivalentEcheck());
    }

    List<IPrimitive<Boolean>> membersDirectBanking() {
        return Arrays.asList(proto().acceptedDirectBanking(), proto().residentPortalDirectBanking(), proto().cashEquivalentDirectBanking());
    }

    List<IPrimitive<Boolean>> membersCreditCardVisa() {
        return Arrays.asList(proto().acceptedCreditCardVisa(), proto().residentPortalCreditCardVisa(), proto().prospectCreditCardVisa(), proto()
                .cashEquivalentCreditCardVisa());
    }

    List<IPrimitive<Boolean>> membersCreditCardMasterCard() {
        return Arrays.asList(proto().acceptedCreditCardMasterCard(), proto().residentPortalCreditCardMasterCard(), proto().prospectCreditCardMasterCard(),
                proto().cashEquivalentCreditCardMasterCard());
    }

    List<IPrimitive<Boolean>> membersVisaDebit() {
        return Arrays.asList(proto().acceptedVisaDebit(), proto().residentPortalVisaDebit(), proto().prospectVisaDebit(), proto().cashEquivalentVisaDebit());
    }

    private void setConvenienceMarker(IPrimitive<Boolean> member, boolean convenienceEnabled) {
        if (convenienceEnabled) {
            //  TODO "<div class=\"" + VistaTheme.StyleName.InfoMessage.name() + "\"" + convenienceMarker + "</div>"
            ((FieldDecorator) get(member).getDecorator()).getLabel().setText(member.getMeta().getCaption() + convenienceMarker);
        } else {
            ((FieldDecorator) get(member).getDecorator()).getLabel().setText(member.getMeta().getCaption());
        }
    }

    private void setEnabled(List<IPrimitive<Boolean>> members, boolean enabled) {
        for (IPrimitive<Boolean> member : members) {
            get(member).setEnabled(enabled);
        }
    }

}
