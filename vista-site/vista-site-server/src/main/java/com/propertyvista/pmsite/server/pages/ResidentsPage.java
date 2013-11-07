/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 22, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.pmsite.server.pages;

import org.apache.wicket.RestartResponseException;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.propertyvista.config.VistaDeployment;
import com.propertyvista.domain.security.common.VistaApplication;

public class ResidentsPage extends BasePage {

    private static final long serialVersionUID = 1L;

    private static final Logger log = LoggerFactory.getLogger(ResidentsPage.class);

    public ResidentsPage() {
        super();

        // redirect if not enabled
        boolean residentPortalEnabled = false;
        try {
            residentPortalEnabled = getCM().getSiteDescriptor().residentPortalEnabled().isBooleanTrue();
        } catch (Exception ignore) {
        }

        if (residentPortalEnabled) {
            // redirect to MyCommunity url
            String secureUrl = VistaDeployment.getBaseApplicationURL(VistaApplication.resident, true);
            log.debug("redirect to url: {}", secureUrl);
            throw new RedirectToUrlException(secureUrl);
        } else {
            log.debug("redirect to page: {}", LandingPage.class);
            throw new RestartResponseException(LandingPage.class);
        }

    }
}
