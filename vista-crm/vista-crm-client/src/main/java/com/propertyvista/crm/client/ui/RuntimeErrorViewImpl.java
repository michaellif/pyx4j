/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 13, 2012
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.widgets.client.Button;

import com.propertyvista.common.client.UserMessage;

public class RuntimeErrorViewImpl extends VerticalPanel implements RuntimeErrorView {

    private Presenter presenter;

    private final HTML label = new HTML();

    public RuntimeErrorViewImpl() {
        setSize("100%", "100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        VerticalPanel content = new VerticalPanel();
        content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        Style style = content.getElement().getStyle();
        style.setProperty("border", "1px solid grey");
        style.setProperty("padding", "20px");
        style.setProperty("margin", "10%");
        style.setProperty("marginBottom", "20%");
        content.add(label);

        Button back = new Button("Back");
        back.getElement().getStyle().setProperty("marginTop", "20px");
        back.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.backToOrigin();
            }
        });
        content.add(back);

        add(content);
    }

    @Override
    public void setError(UserMessage error) {
        if (error != null) {
            label.setHTML(error.getMessage());
        } else
            label.setHTML("All errors will now be rendered in the RuntimeErrorView...");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }
}
