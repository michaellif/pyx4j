/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 15, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class LoginViewImpl extends SimplePanel implements LoginView {
    private Presenter presenter;

    public LoginViewImpl() {
        FlowPanel panel = new FlowPanel();
        HTML label = new HTML("This form should allow submitting authentification request via https<br>");
        panel.add(label);

        Button btn = new Button("Sign In");
        btn.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.gotoResidentsNavig();
            }

        });
        panel.add(btn);

        setWidget(panel);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;

    }

}
