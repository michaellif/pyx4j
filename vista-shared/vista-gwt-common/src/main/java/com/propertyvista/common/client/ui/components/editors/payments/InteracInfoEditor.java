/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 2, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.editors.payments;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;

import com.pyx4j.forms.client.ui.panels.BasicFlexFormPanel;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;
import com.propertyvista.domain.payment.InteracInfo;

public class InteracInfoEditor extends CEntityDecoratableForm<InteracInfo> {

    private static final I18n i18n = I18n.get(InteracInfoEditor.class);

    public InteracInfoEditor() {
        super(InteracInfo.class);
    }

    @Override
    public IsWidget createContent() {
        BasicFlexFormPanel main = new BasicFlexFormPanel();

        main.setWidget(0, 0, InteracPanelCanada());
        main.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE);
        main.setHeight("12em");

        return main;
    }

    private HorizontalPanel InteracPanelCanada() {
        HorizontalPanel panel = new HorizontalPanel();

        panel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        panel.getElement().getStyle().setProperty("padding", "5px");

        Image image = new Image(VistaImages.INSTANCE.logoBMO().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("BMO");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");
        image = new Image(VistaImages.INSTANCE.logoRBC().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("RBC");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");
        image = new Image(VistaImages.INSTANCE.logoTD().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("TD");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");
        image = new Image(VistaImages.INSTANCE.logoScotia().getSafeUri());
        image.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                interacRedirect("Scotia");
            }
        });
        panel.add(image);
        panel.setCellWidth(image, "100");

        return panel;
    }

    private void interacRedirect(String site) { //TODO add a method for creating proper Interac links
        String url = null;
        if (site.equals("BMO")) {
            url = "https://www12.bmo.com/cgi-bin/netbnx/NBmain?product=1";
        } else if (site.equals("RBC")) {
            url = "https://www1.royalbank.com/cgi-bin/rbaccess/rbunxcgi?F6=1&F7=IB&F21=IB&F22=IB&REQUEST=ClientSignin&LANGUAGE=ENGLISH";
        } else if (site.equals("TD")) {
            url = "https://easywebcpo.td.com/waw/idp/login.htm?execution=e1s1";
        } else if (site.equals("Scotia")) {
            url = "https://www1.scotiaonline.scotiabank.com/online/authentication/authentication.bns";
        } else {
            Window.alert("Proper link is not set up yet");
            url = "www.google.com";
        }

        Window.open(url, "site", null);
    }
}
