/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 25, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import java.util.List;

import org.apache.wicket.Request;
import org.apache.wicket.protocol.http.WebSession;

import com.propertyvista.pmsite.server.panels.NavigationItem;

public class PMSiteSession extends WebSession {

    private static final long serialVersionUID = 1L;

    public PMSiteSession(Request request) {
        super(request);
    }

    public List<NavigationItem> getNavigationItems() {
        return PMSiteContentManager.getNavigationItems();
    }
}
