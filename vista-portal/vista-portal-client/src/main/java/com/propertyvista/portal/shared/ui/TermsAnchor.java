/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 7, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.WhiteSpace;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;

import com.pyx4j.site.client.NavigationUri;
import com.pyx4j.site.rpc.AppPlaceInfo;
import com.pyx4j.widgets.client.Anchor;

public class TermsAnchor extends Anchor {

    public TermsAnchor(String text, final Class<? extends Place> placeClass) {
        super(text);
        getElement().getStyle().setDisplay(Display.INLINE);
        getElement().getStyle().setPadding(0, Unit.PX);
        getElement().getStyle().setWhiteSpace(WhiteSpace.NORMAL);
        setHref(AppPlaceInfo.absoluteUrl(GWT.getModuleBaseURL(), true, placeClass));
        addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(AppPlaceInfo.absoluteUrl(NavigationUri.getHostPageURL(), false, placeClass), "_blank", null);
                DOM.eventPreventDefault((com.google.gwt.user.client.Event) event.getNativeEvent());
            }
        });

    }

}
