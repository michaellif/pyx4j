/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 8, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.panels;

import org.apache.wicket.markup.html.panel.Panel;

import com.pyx4j.server.contexts.Context;
import com.pyx4j.server.contexts.Visit;

public class GwtInclude extends Panel {

    private static final long serialVersionUID = 1L;

    public GwtInclude(String id) {
        super(id);
    }

    private String getAuthenticationToken() {
        Visit visit = Context.getVisit();
        if (visit != null) {
            return visit.getSessionToken();
        } else {
            return "";
        }
    }

}
