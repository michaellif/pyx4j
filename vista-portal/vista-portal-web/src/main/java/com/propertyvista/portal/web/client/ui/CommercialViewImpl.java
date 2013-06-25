/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 1, 2013
 * @author Mykola
 * @version $Id$
 */
package com.propertyvista.portal.web.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class CommercialViewImpl extends FlowPanel implements CommercialView {

    public CommercialViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.Commercial.name());
        setWidth("200px");

        HTML add = new HTML(
                "<b>COMMERCIAL</b><br/>Lorem ipsum dolor sit amet, consectetuer adipiscing elit, sed diam nonummy nibh euismod tincidunt ut laoreet dolore magna aliquam erat volutpat. ");
        add.getElement().getStyle().setProperty("padding", "10px");
        add.getElement().getStyle().setProperty("border", "solid 1px #ddd");
        add.getElement().getStyle().setProperty("backgroundColor", "white");

        HTML news = new HTML(
                "<b>NEWS</b><br/>Ut wisi enim ad minim veniam, quis nostrud exerci tation ullamcorper suscipit lobortis nisl ut aliquip ex ea commodo consequat.");
        news.getElement().getStyle().setProperty("padding", "10px");
        news.getElement().getStyle().setProperty("marginTop", "10px");
        news.getElement().getStyle().setProperty("border", "solid 1px #ddd");
        news.getElement().getStyle().setProperty("backgroundColor", "white");

        add(add);
        add(news);

    }

}
