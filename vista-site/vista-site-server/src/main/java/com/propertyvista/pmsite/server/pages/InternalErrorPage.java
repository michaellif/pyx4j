/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 12, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.pmsite.server.PMSiteApplication;
import com.propertyvista.pmsite.server.PMSitePageNotFoundException;

public class InternalErrorPage extends ErrorPage {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(InternalErrorPage.class);

    public InternalErrorPage() {
        StringWriter err = new StringWriter();
        // get internal exception if available
        Exception e = PMSiteApplication.get().getInternalError();
        if (e instanceof PMSitePageNotFoundException) {
            log.debug(e.getMessage());
        } else {
            log.error("site error", e);
        }
        if (e == null) {
            err.write("Unknown Error");
        } else if (ApplicationMode.isDevelopment()) {
            err.write(ApplicationMode.DEV);
            e.printStackTrace(new PrintWriter(err));
        } else {
            err.write(e.getMessage());
        }
        add(new Label("errorContent", err.toString()));
    }

    @Override
    public boolean isVersioned() {
        return false;
    }

    @Override
    public boolean isErrorPage() {
        return true;
    }
}
