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
 */
package com.propertyvista.pmsite.server.pages;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.wicket.markup.html.basic.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.config.shared.ApplicationMode;

import com.propertyvista.pmsite.server.PMSiteApplication;

public class InternalErrorPage extends ErrorPage {
    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(InternalErrorPage.class);

    public InternalErrorPage() {
        StringWriter err = new StringWriter();
        // get internal exception if available
        Exception e = PMSiteApplication.get().getInternalError();

        if (ApplicationMode.isDevelopment()) {
            err.write(ApplicationMode.DEV);
            e.printStackTrace(new PrintWriter(err));
            log.error(ApplicationMode.DEV, e);
        } else {
            err.write(getOriginalCause(e).getMessage());
            log.warn("'{}' caused by: '{}'", e.getMessage(), getOriginalCause(e).getMessage());
        }
        add(new Label("errorContent", err.toString()));
    }

    public Throwable getOriginalCause(Throwable t) {
        Throwable cause = t;
        while (cause != null) {
            if ((cause = cause.getCause()) != null) {
                t = cause;
            }
        }
        return t;
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
