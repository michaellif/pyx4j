/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 3, 2013
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui.landing;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;
import com.pyx4j.widgets.client.actionbar.Toolbar;

import com.propertyvista.portal.web.client.ui.AbstractGadget;
import com.propertyvista.portal.web.client.ui.landing.LandingView.LandingPresenter;

public class SignUpGadget extends AbstractGadget<LandingViewImpl> {

    static final I18n i18n = I18n.get(LandingViewImpl.class);

    private LandingPresenter presenter;

    private final Image safeAndSecureImage;

    private final Image easyToUseImage;

    private final Image manageRequestsImage;

    SignUpGadget(LandingViewImpl view) {
        super(view, null, "New Users", ThemeColor.contrast3);
        setActionsToolbar(new SignUpToolbar());

        FlowPanel contentPanel = new FlowPanel();

        contentPanel.add(new HTML("Don't have an account yet?"));

        HorizontalPanel imagesPanel = new HorizontalPanel();
        imagesPanel.setWidth("100%");

        safeAndSecureImage = new Image();
        imagesPanel.add(safeAndSecureImage);
        imagesPanel.setCellHorizontalAlignment(safeAndSecureImage, HorizontalPanel.ALIGN_CENTER);

        easyToUseImage = new Image();
        imagesPanel.add(easyToUseImage);
        imagesPanel.setCellHorizontalAlignment(easyToUseImage, HorizontalPanel.ALIGN_CENTER);

        manageRequestsImage = new Image();
        imagesPanel.add(manageRequestsImage);
        imagesPanel.setCellHorizontalAlignment(manageRequestsImage, HorizontalPanel.ALIGN_CENTER);

        contentPanel.add(imagesPanel);

        setContent(contentPanel);
    }

    public void setPresenter(LandingPresenter presenter) {
        this.presenter = presenter;
    }

    void setImages(ImageResource safeAndSecure, ImageResource easyToUse, ImageResource manageRequests) {
        safeAndSecureImage.setResource(safeAndSecure);
        easyToUseImage.setResource(easyToUse);
        manageRequestsImage.setResource(manageRequests);
    }

    class SignUpToolbar extends Toolbar {

        private final Button signUpButton;

        public SignUpToolbar() {

            signUpButton = new Button(i18n.tr("SIGN UP"), new Command() {
                @Override
                public void execute() {
                    presenter.signUp();
                }
            });
            signUpButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            add(signUpButton);

        }
    }

}
