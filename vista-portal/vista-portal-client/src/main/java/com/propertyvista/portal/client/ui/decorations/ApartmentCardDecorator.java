/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 3, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.decorations;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;

public class ApartmentCardDecorator extends BasicCardDecorator {

    private final Anchor mapIitem;

    public ApartmentCardDecorator() {
        super();
        mapIitem = new Anchor(i18n.tr("Map"));
        addMenuItem(mapIitem);
    }

    public HandlerRegistration addMapClickHandler(ClickHandler h) {
        return mapIitem.addClickHandler(h);

    }

}
