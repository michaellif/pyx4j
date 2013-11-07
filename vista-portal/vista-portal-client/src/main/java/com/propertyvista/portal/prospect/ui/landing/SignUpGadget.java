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
package com.propertyvista.portal.prospect.ui.landing;

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

import com.propertyvista.portal.prospect.ui.landing.LandingView.LandingPresenter;
import com.propertyvista.portal.shared.ui.AbstractGadget;

public class SignUpGadget extends AbstractGadget<LandingViewImpl> {

    static final I18n i18n = I18n.get(SignUpGadget.class);

    private LandingPresenter presenter;

    SignUpGadget(LandingViewImpl view) {
        super(view, null, i18n.tr("Apply Online"), ThemeColor.contrast3, 1);
        setActionsToolbar(new SignUpToolbar());

        FlowPanel contentPanel = new FlowPanel();

        contentPanel
                .add(new HTML(
                        "TODO!!!!!!! Introduction ‘welcome’ text goes here explaining the process. Highlight the fact that after creating an account, the user can log back in to continue later if they choose to. It’s also recommended to highlight the secure factor of this online (or in-office) application (vs. filling out paperwork)."));

        HorizontalPanel imagesPanel = new HorizontalPanel();
        imagesPanel.setWidth("100%");

        contentPanel.add(imagesPanel);

        setContent(contentPanel);
    }

    public void setPresenter(LandingPresenter presenter) {
        this.presenter = presenter;
    }

    class SignUpToolbar extends Toolbar {

        private final Button signUpButton;

        public SignUpToolbar() {

            signUpButton = new Button(i18n.tr("CREATE ACCOUNT"), new Command() {
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
