/*
 * (C) Copyright Property Vista Software Inc. 2011-2015 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 20, 2014
 * @author michaellif
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

public class MoveinWizardStepPreviewGadget extends AbstractGadget<MoveinWizardStepPreviewView> {

    private static final I18n i18n = I18n.get(NewTenantWelcomeGadget.class);

    private final Image buildingImage;

    private final Image helpImage;

    public MoveinWizardStepPreviewGadget(MoveinWizardStepPreviewView view) {
        super(view, null, i18n.tr("Move-In Wizard"), ThemeColor.contrast2, 1);
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
                                i18n.tr("<b>Congratulations You’ve been Approved!</b><p/><div style=text-align:left>Use this Move-In Wizard to ease your move-in experience . After a few simple steps you’ll be ready for your new home.</div>")));

        helpImage = new Image(PortalImages.INSTANCE.signUpPersonal());
        helpImage.getElement().getStyle().setPaddingRight(20, Unit.PX);
        welcomePanel.setWidget(1, 0, helpImage);
        welcomePanel
                .setWidget(
                        1,
                        1,
                        new HTML(
                                i18n.tr("<b>We'll help you:</b><p/><ul style='margin: auto; text-align: left; display: inline-block;'><li>Sign your lease agreement</li><li>Purchase Tenant Insurance</li><li>Book your Move-In Day & Elevators</li><li>Set up Pre-Authorised Payments</li><li>Sign up for exclusive offers</li></ul>")));

        setContent(welcomePanel);
    }

    class NewResidentWelcomeToolbar extends GadgetToolbar {

        private final Button startButton;

        public NewResidentWelcomeToolbar() {

            startButton = new Button(i18n.tr("Let's Get Started!"), new Command() {
                @Override
                public void execute() {
                    AppSite.getPlaceController().goTo(new ResidentPortalSiteMap.LeaseSigning.LeaseSigningWizard());
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
            helpImage.setVisible(false);
            break;

        default:
            buildingImage.setVisible(true);
            helpImage.setVisible(true);
            break;
        }
    }
}
