/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-16
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.portal.rpc.portal.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.commons.Key;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.domain.ref.City;
import com.propertyvista.domain.site.ContentDescriptor;
import com.propertyvista.domain.site.ContentDescriptor.Lang;
import com.propertyvista.domain.site.PageContent;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;

public interface PortalSiteServices extends IService {

    public void retrieveContentDescriptor(AsyncCallback<ContentDescriptor> callback, Lang lang);

    public void retrieveStaticContent(AsyncCallback<PageContent> callback, Key pageContentId);

    /**
     * get List<City> with available units.
     */
    public void retrieveCityList(AsyncCallback<Vector<City>> callback);

    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback);

    public void retrievePropertyDetails(AsyncCallback<PropertyDetailsDTO> callback, Key propertyId);

    public void retrieveFloorplanDetails(AsyncCallback<FloorplanDetailsDTO> callback, Key floorplanId);

}
