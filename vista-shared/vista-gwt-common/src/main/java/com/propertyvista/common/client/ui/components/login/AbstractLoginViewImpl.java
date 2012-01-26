/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 26, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.login;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractLoginViewImpl implements LoginView {

    private LoginView.Presenter presenter;

    private final LoginForm form;

    private final Widget viewAsWidget;

    public AbstractLoginViewImpl(String caption) {
        form = new LoginForm(caption, new Command() {
            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }
        },

        new Command() {

            @Override
            public void execute() {
                // TODO Auto-generated method stub

            }

        });

        viewAsWidget = createContent();
    }

    protected abstract Widget createContent();

    @Override
    public Widget asWidget() {
        return viewAsWidget;
    }

    @Override
    public void setPresenter(Presenter presenter) {

        this.presenter = presenter;

        form.populateNew();
        //
        form.disableCaptcha();
    }

    @Override
    public void enableHumanVerification() {
        // TODO Auto-generated method stub        
    }

    @Override
    public void discard() {
        // TODO Auto-generated method stub

    }

}
