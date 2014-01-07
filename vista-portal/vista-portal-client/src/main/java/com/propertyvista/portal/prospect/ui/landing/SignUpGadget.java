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

import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.commons.css.StyleManager;
import com.pyx4j.commons.css.ThemeColor;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.widgets.client.Button;

import com.propertyvista.portal.prospect.ui.landing.LandingView.LandingPresenter;
import com.propertyvista.portal.shared.ui.AbstractGadget;
import com.propertyvista.portal.shared.ui.GadgetToolbar;

public class SignUpGadget extends AbstractGadget<LandingViewImpl> {

    static final I18n i18n = I18n.get(SignUpGadget.class);

    private LandingPresenter presenter;

    SignUpGadget(LandingViewImpl view) {
        super(view, null, i18n.tr("Apply Online"), ThemeColor.contrast3, 1);
        setActionsToolbar(new SignUpToolbar());

        FlowPanel contentPanel = new FlowPanel();
        contentPanel.getElement().getStyle().setTextAlign(TextAlign.LEFT);

        contentPanel
                .add(new HTML(
                        i18n.tr("<b>Are you ready?</b><div>We have moved the entire application process online."
                                + " You now have the ability to create an online application online in a safe and secure environment that just takes minutes to complete.</div>")));

        contentPanel.add(new HTML(i18n
                .tr("<br/><b>Security</b><div>This site is protected by above bank-grade security. Your information is kept safe and secure at all times."
                        + " Please ensure you are using a newer browser to ensure the highest security protocols are met.</div>")));

        setContent(contentPanel);
    }

    public void setPresenter(LandingPresenter presenter) {
        this.presenter = presenter;
    }

    class SignUpToolbar extends GadgetToolbar {

        private final Button signUpButton;

        public SignUpToolbar() {

            signUpButton = new Button(i18n.tr("START APPLICATION"), new Command() {
                @Override
                public void execute() {
                    presenter.signUp();
                }
            });
            signUpButton.getElement().getStyle().setProperty("background", StyleManager.getPalette().getThemeColor(ThemeColor.contrast3, 1));
            addItem(signUpButton);

        }
    }

}
