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

import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.forms.client.ui.panels.TwoColumnFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.ui.prime.form.IForm;

import com.propertyvista.common.client.ui.decorations.TableDecoratorBuilder;
import com.propertyvista.common.client.ui.decorations.TableWidgetDecorator;
import com.propertyvista.crm.client.ui.crud.policies.common.PolicyDTOTabPanelBasedForm;
import com.propertyvista.domain.policy.dto.PaymentTypeSelectionPolicyDTO;

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

        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedCash()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedCheck()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedEcheck()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedDirectBanking()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedCreditCardMasterCard()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedCreditCardVisa()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedVisaDebit()));
        accepted.setWidget(0, ++col, alignedToCenter(proto().acceptedInterac()));

        BasicFlexFormPanel residentPortal = new BasicFlexFormPanel();
        col = -1;
        residentPortal.setH4(0, ++col, 1, i18n.tr("Resident Portal:"));
        residentPortal.getWidget(0, col).setWidth(hdrW);

        residentPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, new HTML()); // fill empty cell
        residentPortal.getWidget(0, col).setWidth(lblW);
        residentPortal.setWidget(0, ++col, alignedToCenter(proto().residentPortalEcheck()));
        residentPortal.setWidget(0, ++col, alignedToCenter(proto().residentPortalDirectBanking()));
        residentPortal.setWidget(0, ++col, alignedToCenter(proto().residentPortalCreditCardMasterCard()));
        residentPortal.setWidget(0, ++col, alignedToCenter(proto().residentPortalCreditCardVisa()));
        residentPortal.setWidget(0, ++col, alignedToCenter(proto().residentPortalVisaDebit()));
        residentPortal.setWidget(0, ++col, alignedToCenter(proto().residentPortalInterac()));

        BasicFlexFormPanel cashEquivalent = new BasicFlexFormPanel();
        col = -1;
        cashEquivalent.setH4(0, ++col, 1, i18n.tr("Cash Equivalent:"));
        cashEquivalent.getWidget(0, col).setWidth(hdrW);

        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentCash()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentCheck()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentEcheck()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentDirectBanking()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentCreditCardMasterCard()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentCreditCardVisa()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentVisaDebit()));
        cashEquivalent.setWidget(0, ++col, alignedToCenter(proto().cashEquivalentInterac()));

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

    private TableWidgetDecorator alignedToCenter(IPrimitive<Boolean> check) {
        return new TableDecoratorBuilder(inject(check), lblW, contW, contW).labelPosition(TableDecoratorBuilder.LabelPosition.top)
                .labelAlignment(TableDecoratorBuilder.Alignment.center).componentAlignment(TableDecoratorBuilder.Alignment.center).build();
    }
}
