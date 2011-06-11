/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jun 11, 2011
 * @author Dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui;

import com.google.gwt.event.shared.GwtEvent;
import com.propertyvista.portal.client.ui.MainNavigViewImpl.NavigTab;

public class TabSelectedEvent extends GwtEvent<TabSelectedHandler> {

    public final static Type<TabSelectedHandler> TYPE = new Type<TabSelectedHandler>();

    private final NavigTab tab;

    TabSelectedEvent(NavigTab tab) {
        this.tab = tab;
    }

    public NavigTab getTab() {
        return tab;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<TabSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(TabSelectedHandler handler) {
        handler.onTabSelect(this);
    }

}
