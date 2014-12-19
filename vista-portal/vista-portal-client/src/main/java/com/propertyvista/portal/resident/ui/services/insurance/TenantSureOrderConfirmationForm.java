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
package com.propertyvista.portal.resident.ui.services.insurance;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.rpc.AppPlace;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.Label;

import com.propertyvista.portal.resident.themes.TenantSureTheme;
import com.propertyvista.portal.rpc.portal.resident.dto.insurance.TenantSureInsurancePolicyDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class TenantSureOrderConfirmationForm extends CPortalEntityForm<TenantSureInsurancePolicyDTO> {

    private static final I18n i18n = I18n.get(TenantSureOrderConfirmationForm.class);

    public TenantSureOrderConfirmationForm(TenantSureOrderConfirmationViewImpl view) {
        super(TenantSureInsurancePolicyDTO.class, view, i18n.tr("Congratulations. Your TenantSure Insurance Policy has been processed!"), new Button(
                i18n.tr("Continue"), new Command() {

                    @Override
                    public void execute() {
                        AppSite.getPlaceController().goTo(AppPlace.NOWHERE);
                    }
                }), ThemeColor.contrast3);

        inheritViewable(false);
    }

    @Override
    protected IsWidget createContent() {
        Label label = new Label();
        label.addStyleName(TenantSureTheme.StyleName.TenantSurePucrhaseViewMessageText.name());
        label.setText(i18n.tr("An email with your insurance policy has been sent to your email."));
        return label;
    }

}
