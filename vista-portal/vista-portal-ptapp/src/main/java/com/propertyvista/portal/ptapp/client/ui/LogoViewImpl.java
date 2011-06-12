/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 1, 2011
 * @author Misha
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui;

import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class LogoViewImpl extends SimplePanel implements LogoView {

    private Presenter presenter;

    public LogoViewImpl() {
        HTML logo = new HTML(
                "<div style='font-size:15px; text-align:center; vertical-align:middle; padding-top:25px; width:900px; color:#E6E6E6'><h1>PO Branded Header</h1><div>");
        //        logo.setStyleName("logo");
        logo.getElement().getStyle().setCursor(Cursor.POINTER);
        logo.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });

        logo.setSize("300px", "115px");
        setWidget(logo);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
