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
package com.propertyvista.crm.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.components.MediaUtils;

public class LogoViewImpl extends SimplePanel implements LogoView {

    private static final I18n i18n = I18n.get(LogoViewImpl.class);

    private Presenter presenter;

    private static String brandedHeader;

    private static boolean useLogoImage;

    //TODO Misha How can I do this properly ?
    @Deprecated
    public static void temporaryWayToSetTitle(String title, boolean logoImageAvalable) {
        brandedHeader = title;
        useLogoImage = logoImageAvalable;
    }

    public LogoViewImpl() {
        HasClickHandlers logoElement;
        if (useLogoImage) {
            Image logoImage = new Image(MediaUtils.createSiteLogoUrl());
            logoImage.getElement().getStyle().setProperty("maxHeight", "50px");
            logoImage.getElement().getStyle().setMarginLeft(20, Unit.PX);
            logoImage.getElement().getStyle().setMarginTop(4, Unit.PX);
            logoImage.getElement().getStyle().setFloat(Style.Float.LEFT);
            logoImage.getElement().getStyle().setCursor(Cursor.POINTER);
            logoElement = logoImage;
            setWidget(logoImage);
            logoImage.setTitle(i18n.tr("Home"));
        } else {
            HTML logo = new HTML("<h1>" + (brandedHeader != null ? new SafeHtmlBuilder().appendEscaped(brandedHeader).toSafeHtml().asString() : "") + "</h1>");
            logo.getElement().getStyle().setCursor(Cursor.POINTER);
            logoElement = logo;
            setWidget(logo);
        }
        logoElement.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.navigToLanding();
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
