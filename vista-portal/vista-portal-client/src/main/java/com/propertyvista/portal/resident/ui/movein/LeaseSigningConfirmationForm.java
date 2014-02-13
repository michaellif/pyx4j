/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-11
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementConfirmationDTO;
import com.propertyvista.portal.shared.ui.AbstractFormView;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class LeaseSigningConfirmationForm extends CPortalEntityForm<LeaseAgreementConfirmationDTO> {

    private static final I18n i18n = I18n.get(LeaseSigningConfirmationForm.class);

    public LeaseSigningConfirmationForm(AbstractFormView<LeaseAgreementConfirmationDTO> view) {
        super(LeaseAgreementConfirmationDTO.class, view, i18n.tr("Lease Agreement Submitted"), ThemeColor.contrast4);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel content = new BasicFlexFormPanel();
        int row = -1;

        content.setH4(
                ++row,
                0,
                1,
                i18n.tr("Thank you. We have received your signed Lease Agreement. You can obtain a draft copy of the agreement by clicking 'Download Agreement' button here."));

        content.setWidget(++row, 0, new Button(i18n.tr("Download Agreement"), new Command() {
            @Override
            public void execute() {
                onDownloadAgreement();
            }
        }));

        content.setBR(++row, 0, 1);

        if (SecurityController.checkBehavior(PortalResidentBehavior.Resident)) {
            HTML helpText = new HTML(
                    i18n.tr("You now have access to the mycommunity portal. From the dashboard you can manage your payments, submit maintenance requests, view special offers and more."));
            //" Please use the links below to continue with the setup of your tenant services, then continue on to the mycommunity dashboard."
            helpText.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            content.setWidget(++row, 0, helpText);
        } else if (SecurityController.checkBehavior(PortalResidentBehavior.Guarantor)) {
            HTML helpText = new HTML(
                    i18n.tr("You now have access to the mycommunity portal. From the dashboard you can manage your payments, view special offers and more."));
            helpText.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            content.setWidget(++row, 0, helpText);
        }

        return content;
    }

    public void onDownloadAgreement() {

    }

}
