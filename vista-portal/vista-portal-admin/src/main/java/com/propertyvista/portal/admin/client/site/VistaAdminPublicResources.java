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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle.Source;
import com.google.gwt.resources.client.TextResource;
import com.propertyvista.portal.admin.client.VistaAdminResources;

public interface VistaAdminPublicResources extends VistaAdminResources {

    VistaAdminPublicResources INSTANCE = GWT.create(VistaAdminPublicResources.class);

    @Source("pageLanding.html")
    TextResource sitePageLanding();
}
