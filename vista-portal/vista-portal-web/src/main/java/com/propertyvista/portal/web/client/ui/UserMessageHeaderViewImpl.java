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

import java.util.List;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

import com.pyx4j.widgets.client.ImageFactory;
import com.pyx4j.widgets.client.ImageFactory.WidgetsImageBundle;

import com.propertyvista.common.client.site.UserMessage;
import com.propertyvista.portal.web.client.themes.PortalWebRootPaneTheme;

public class UserMessageHeaderViewImpl extends FlowPanel implements UserMessageHeaderView {

    private Presenter presenter;

    private final FlowPanel contentPanel;

    public UserMessageHeaderViewImpl() {

        setStyleName(PortalWebRootPaneTheme.StyleName.Messages.name());

        WidgetsImageBundle images = ImageFactory.getImages();

        HTML error = new HTML("<b>Error Notification</b><br>Error Message goes here");
        error.getElement().getStyle().setProperty("textAlign", "center");
        error.getElement().getStyle().setProperty("background", "url('" + images.error().getSafeUri().asString() + "') no-repeat scroll 10px center");
        error.getElement().getStyle().setPaddingTop(10, Unit.PX);
        error.setHeight("40px");
        error.getElement().getStyle().setProperty("border", "1px solid #E09293");
        error.getElement().getStyle().setProperty("borderRadius", "5px");
        error.getElement().getStyle().setProperty("margin", "0 10px 10px 10px");
        error.getElement().getStyle().setProperty("backgroundColor", "#FFD2D3");

        HTML info = new HTML("<b>Notification</b><br>Message goes here");
        info.getElement().getStyle().setProperty("textAlign", "center");
        info.getElement().getStyle().setProperty("background", "url('" + images.info().getSafeUri().asString() + "') no-repeat scroll 10px center");
        info.getElement().getStyle().setPaddingTop(10, Unit.PX);
        info.setHeight("40px");
        info.getElement().getStyle().setProperty("border", "1px solid #9ADF8F");
        info.getElement().getStyle().setProperty("borderRadius", "5px");
        info.getElement().getStyle().setProperty("margin", "0 10px 10px 10px");
        info.getElement().getStyle().setProperty("backgroundColor", "#D4FFCD");

        contentPanel = new FlowPanel();

        contentPanel.add(error);
        contentPanel.add(info);

        add(contentPanel);

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void populate(List<UserMessage> userMessages) {
        contentPanel.clear();
        WidgetsImageBundle images = ImageFactory.getImages();

        for (UserMessage userMessage : userMessages) {

            ImageResource image;

            switch (userMessage.getMessageType()) {
            case FAILURE:
                image = images.error();
                break;
            case ERROR:
                image = images.error();
                break;
            case WARN:
                image = images.warn();
                break;
            case INFO:
                image = images.info();
                break;

            default:
                image = images.info();
                break;
            }

            HTML message = new HTML("<b>" + userMessage.getTitle() + "</b><br/>" + userMessage.getMessage());
            message.getElement().getStyle().setProperty("textAlign", "center");
            message.getElement().getStyle().setProperty("background", "url('" + image.getSafeUri().asString() + "') no-repeat scroll 10px center");
            message.getElement().getStyle().setPaddingTop(10, Unit.PX);
            message.setHeight("40px");
            message.getElement().getStyle().setProperty("border", "1px solid #E09293");
            message.getElement().getStyle().setProperty("borderRadius", "5px");
            message.getElement().getStyle().setProperty("margin", "2px");
            message.getElement().getStyle().setProperty("backgroundColor", "#FFD2D3");
        }

    }
}
