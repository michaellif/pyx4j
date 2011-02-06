/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Feb 5, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.admin.client.site;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.propertyvista.portal.admin.client.SignInCommand;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.site.client.InlineWidget;

public class AcceptDeclineWidget extends HorizontalPanel implements InlineWidget {

    public AcceptDeclineWidget() {
        Button accept = new Button("Accept");
        Button decline = new Button("Decline");
        this.add(accept);
        this.add(new HTML(CommonsStringUtils.NO_BREAK_SPACE_HTML));
        this.add(decline);

        accept.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                new SignInCommand().execute();
            }
        });

        decline.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                AcceptDeclineWidget.this.setVisible(false);
            }
        });
    }

    @Override
    public void populate(Map<String, String> args) {
    }

}
