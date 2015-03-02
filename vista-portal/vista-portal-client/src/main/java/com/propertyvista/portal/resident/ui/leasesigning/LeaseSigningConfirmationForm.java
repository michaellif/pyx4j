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
 */
package com.propertyvista.portal.resident.ui.leasesigning;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.DualColumnFluidPanel.Location;
import com.pyx4j.forms.client.ui.panels.FormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.shared.SecurityController;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.domain.security.PortalResidentBehavior;
import com.propertyvista.portal.rpc.portal.resident.dto.movein.LeaseAgreementConfirmationDTO;
import com.propertyvista.portal.shared.ui.AbstractFormView;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class LeaseSigningConfirmationForm extends CPortalEntityForm<LeaseAgreementConfirmationDTO> {

    private static final I18n i18n = I18n.get(LeaseSigningConfirmationForm.class);

    public LeaseSigningConfirmationForm(AbstractFormView<LeaseAgreementConfirmationDTO> view) {
        super(LeaseAgreementConfirmationDTO.class, view, i18n.tr("Lease Agreement Submitted"), new Button(i18n.tr("Continue"), new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
            }
        }), ThemeColor.contrast2);
    }

    @Override
    protected IsWidget createContent() {
        FormPanel formPanel = new FormPanel(this);

        formPanel
                .h4(i18n.tr("Thank you. We have received your signed Lease Agreement. You can obtain a draft copy of the agreement by clicking 'Download Agreement' button here."));

        formPanel.append(Location.Left, new Button(i18n.tr("Download Agreement"), new Command() {
            @Override
            public void execute() {
                onDownloadAgreement();
            }
        }));

        formPanel.br();

        if (SecurityController.check(PortalResidentBehavior.Resident)) {
            HTML helpText = new HTML(
                    i18n.tr("You now have access to the mycommunity portal. From the dashboard you can manage your payments, submit maintenance requests, view special offers and more."));
            //" Please use the links below to continue with the setup of your tenant services, then continue on to the mycommunity dashboard."
            helpText.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            formPanel.append(Location.Left, helpText);
        } else if (SecurityController.check(PortalResidentBehavior.Guarantor)) {
            HTML helpText = new HTML(
                    i18n.tr("You now have access to the mycommunity portal. From the dashboard you can manage your payments, view special offers and more."));
            helpText.getElement().getStyle().setTextAlign(TextAlign.LEFT);
            formPanel.append(Location.Left, helpText);
        }

        return formPanel;
    }

    public void onDownloadAgreement() {

    }

}
