/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 18, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.resident.ui.movein;

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.site.client.ui.layout.responsive.ResponsiveLayoutPanel.LayoutType;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class NewGuarantorWelcomeGadget extends AbstractGadget<NewGuarantorWelcomePageView> {

    private static final I18n i18n = I18n.get(NewGuarantorWelcomeGadget.class);

    private final Image buildingImage;

    public NewGuarantorWelcomeGadget(NewGuarantorWelcomePageViewImpl view) {
        super(view, null, i18n.tr("Lease Agreement Signing Wizard for Guarantor"), ThemeColor.contrast2, 1);
        setActionsToolbar(new NewResidentWelcomeToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.CENTER);

        BasicFlexFormPanel welcomePanel = new BasicFlexFormPanel();
        contentPanel.add(welcomePanel);

        welcomePanel.getColumnFormatter().setWidth(0, "50px");
        welcomePanel.getColumnFormatter().setWidth(1, "300px");
        int row = -1;

        buildingImage = new Image(PortalImages.INSTANCE.signUpBuilding());
        welcomePanel.setWidget(++row, 0, buildingImage);

        welcomePanel
                .setWidget(
                        row,
                        1,
                        new HTML(
                                i18n.tr("<b>Congratulations. The application you are the Guarantor for has been Approved!</b><p/><div style=text-align:left>To finalize the Lease you have guaranteed, you will need to sign the Lease by following a few short steps.</div>")));

        setContent(contentPanel);
    }

    class NewResidentWelcomeToolbar extends GadgetToolbar {

        private final Button startButton;

        public NewResidentWelcomeToolbar() {

            startButton = new Button(i18n.tr("Sign Lease Agreement"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.MoveIn.MoveInWizard());
                }
            });
            startButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast2, 1));
            addItem(startButton);

        }
    }

    public void doLayout(LayoutType layoutType) {
        switch (layoutType) {
        case phonePortrait:
        case phoneLandscape:
            buildingImage.setVisible(false);
            break;

        default:
            buildingImage.setVisible(true);
            break;
        }
    }
}
