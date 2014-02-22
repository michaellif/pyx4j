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
package com.propertyvista.portal.prospect.ui.application;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.prospect.ProspectPortalSiteMap;
import com.propertyvista.portal.rpc.portal.prospect.dto.OnlineApplicationConfirmationDTO;
import com.propertyvista.portal.shared.ui.CPortalEntityForm;

public class ApplicationConfirmationForm extends CPortalEntityForm<OnlineApplicationConfirmationDTO> {

    private static final I18n i18n = I18n.get(ApplicationConfirmationForm.class);

    public ApplicationConfirmationForm(ApplicationConfirmationView view) {
        super(OnlineApplicationConfirmationDTO.class, view, i18n.tr("Application Submitted Successfully!"), ThemeColor.contrast2);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel contentPanel = new BasicFlexFormPanel();
        int row = -1;

        contentPanel
                .setH4(++row,
                        0,
                        1,
                        i18n.tr("Your application has been successfully submitted. An email has been sent to you as well with all pertinent information. Once all applicants/guarantors (if applicable) have successfully submitted the application we will process your application and will advise you of our decision. The process can take between 1-3 business days. If you have any questions about the status of your application, please do not hesitate to contact us directly or check back here to see the latest status of your application."));

        contentPanel.setBR(++row, 0, 1);

        Button okButton = new Button(i18n.tr("OK"), new Command() {
            @Override
            public void execute() {
                AppSite.getPlaceController().goTo(new ProspectPortalSiteMap.Status());
            }
        });
        okButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
        contentPanel.setWidget(++row, 0, okButton);

        contentPanel.setBR(++row, 0, 1);

        return contentPanel;

    }
}
