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
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.security.shared.SecurityController;

import com.propertyvista.domain.security.VistaBasicBehavior;
import com.propertyvista.misc.VistaTODO;

public class LogoViewImpl extends SimplePanel implements LogoView {

    private Presenter presenter;

    private static String brandedHeader;

    //TODO Misha How can I do this properly ?
    @Deprecated
    public static void temporaryWayToSetTitle(String title) {
        brandedHeader = title;
    }

    public LogoViewImpl() {
        if (VistaTODO.enableWelcomeWizardDemoMode) {
            HorizontalPanel logoPanel = new HorizontalPanel();
            logoPanel.setWidth("100%");
            if (SecurityController.checkAnyBehavior(VistaBasicBehavior.ProspectiveApp)) {
                HTML daysLeftCountdown = new HTML(
                        "<div style='color:#E6E6E6;float:left;'><div style='border-style:outset;border-width:1px;text-align:center;font-size:1.5em;font-weight:bold;width:2.5em;line-height:2.5em;margin:auto;vertical-align:center'>15</div><div style='margin:auto;text-align:center;'>Days Until Move-In Date</div></div>");
                daysLeftCountdown.getElement().getStyle().setPosition(Position.ABSOLUTE);
                daysLeftCountdown.getElement().getStyle().setTop(5, Unit.PCT);
                daysLeftCountdown.getElement().getStyle().setLeft(10, Unit.PX);
                logoPanel.add(daysLeftCountdown);
            }

            HTML logo = new HTML("Move-In Guide");
            logo.getElement().getStyle().setColor("#E6E6E6");
            logo.getElement().getStyle().setWidth(100, Unit.PCT);
            logo.getElement().getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
            logo.getElement().getStyle().setProperty("textAlign", "center");
//            logo.getElement().getStyle().setPosition(Position.ABSOLUTE);
//            logo.getElement().getStyle().setTop(8, Unit.PCT);
//            logo.getElement().getStyle().setLeft(0, Unit.PCT);
            logo.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            logo.getElement().getStyle().setFontSize(4, Unit.EM);
            logoPanel.add(logo);
            setWidget(logoPanel);
        } else {
            HTML logo = new HTML("<div style='font-size:40px; text-align:center; vertical-align:middle; color:#E6E6E6'><h1>" + brandedHeader + "</h1><div>");
            logo.getElement().getStyle().setCursor(Cursor.POINTER);
            logo.addClickHandler(new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    presenter.navigToLanding();
                }
            });

            setWidget(logo);
        }

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

}
