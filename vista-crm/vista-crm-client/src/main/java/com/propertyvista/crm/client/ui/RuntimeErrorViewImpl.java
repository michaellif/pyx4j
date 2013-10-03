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
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.site.shared.domain.Notification;
import com.pyx4j.widgets.client.Button;

public class RuntimeErrorViewImpl extends VerticalPanel implements RuntimeErrorView {

    private Presenter presenter;

    private final HTML label = new HTML();

    public RuntimeErrorViewImpl() {
        setSize("100%", "100%");
        setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        FlexTable content = new FlexTable();
        int row = -1;

        HTML title = new HTML("Error");
        Style titleStyle = title.getElement().getStyle();
        titleStyle.setProperty("fontSize", "14px");
        titleStyle.setProperty("color", "red");

        content.setWidget(++row, 0, title);
        Style titleBar = content.getCellFormatter().getElement(row, 0).getStyle();
        titleBar.setProperty("padding", "10px 30px");
        titleBar.setProperty("borderBottom", "1px solid #ddd");

        Style style = content.getElement().getStyle();
        style.setProperty("border", "2px solid #ddd");
        style.setProperty("borderSpacing", "0");
        style.setProperty("margin", "10%");
        style.setProperty("marginBottom", "20%");

        Style msgStyle = label.getElement().getStyle();

        content.setWidget(++row, 0, label);
        Style msgBar = content.getCellFormatter().getElement(row, 0).getStyle();
        msgBar.setProperty("padding", "30px");

        Button back = new Button("OK", new Command() {
            @Override
            public void execute() {
                // TODO Auto-generated method stub
                presenter.backToOrigin();
            }
        });
        content.setWidget(++row, 0, back);
        content.getCellFormatter().setHorizontalAlignment(row, 0, HasHorizontalAlignment.ALIGN_RIGHT);
        Style bottomBar = content.getCellFormatter().getElement(row, 0).getStyle();
        bottomBar.setProperty("background", "#f0f0f0");
        bottomBar.setProperty("padding", "10px 30px");

        add(content);
    }

    @Override
    public void setError(Notification error) {
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
