/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 28, 2013
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server;

import org.apache.wicket.request.cycle.RequestCycle;

public class PMSiteInternalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public PMSiteInternalException(Throwable t) {
        super("PMSite error on request: " + RequestCycle.get().getRequest().getUrl().toString(), t);
    }

    public static boolean hasCaused(Throwable e) {
        while (e != null) {
            if (e instanceof PMSiteInternalException) {
                return true;
            }
            e = e.getCause();
        }
        return false;
    }

}
