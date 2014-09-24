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

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.gwt.commons.layout.LayoutType;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.site.client.AppSite;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.rpc.portal.resident.ResidentPortalSiteMap;
import com.propertyvista.portal.shared.resources.PortalImages;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class GuarantorWelcomeGadget extends AbstractGadget<MoveInWizardView> {

    private static final I18n i18n = I18n.get(GuarantorWelcomeGadget.class);

    private final Image buildingImage;

    public GuarantorWelcomeGadget(MoveInWizardView view) {
        super(view, null, i18n.tr("Lease Agreement Signing Wizard for Guarantor"), ThemeColor.contrast2, 1);
        setActionsToolbar(new NewResidentWelcomeToolbar());

        FlexTable welcomePanel = new FlexTable();
        welcomePanel.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        welcomePanel.getElement().getStyle().setProperty("maxWidth", "500px");
        welcomePanel.getElement().getStyle().setProperty("textAlign", "left");

        buildingImage = new Image(PortalImages.INSTANCE.signUpBuilding());
        buildingImage.getElement().getStyle().setPaddingRight(20, Unit.PX);
        welcomePanel.setWidget(0, 0, buildingImage);

        welcomePanel
                .setWidget(
                        0,
                        1,
                        new HTML(
                                i18n.tr("<b>Congratulations. The application you are the Guarantor for has been Approved!</b><p/><div style=text-align:left>To finalize the Lease you have guaranteed, you will need to sign the Lease by following a few short steps.</div>")));

        setContent(welcomePanel);
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
