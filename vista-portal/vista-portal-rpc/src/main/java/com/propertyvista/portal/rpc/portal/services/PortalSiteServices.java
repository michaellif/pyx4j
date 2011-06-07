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
import com.pyx4j.entity.rpc.GeoCriteria;
import com.pyx4j.rpc.shared.IService;

import com.propertyvista.common.domain.ref.City;
import com.propertyvista.portal.domain.dto.FloorplanDetailsDTO;
import com.propertyvista.portal.domain.dto.MainNavigDTO;
import com.propertyvista.portal.domain.dto.PropertyDetailsDTO;
import com.propertyvista.portal.domain.dto.PropertyListDTO;
import com.propertyvista.portal.domain.site.PageContent;
import com.propertyvista.portal.rpc.portal.PropertySearchCriteria;

public interface PortalSiteServices extends IService {

    public void retrieveMainNavig(AsyncCallback<MainNavigDTO> callback);

    public void retrieveStaticContent(AsyncCallback<PageContent> callback, String pageId);

    /**
     * get List<City> with available units.
     */
    public void retrieveCityList(AsyncCallback<Vector<City>> callback);

    public void retrievePropertyListByCity(AsyncCallback<PropertyListDTO> callback, City city);

    public void retrievePropertyList(AsyncCallback<PropertyListDTO> callback, PropertySearchCriteria criteria);

    public void retrievePropertyListByGeo(AsyncCallback<PropertyListDTO> callback, GeoCriteria geoCriteria);

    public void retrievePropertyDetails(AsyncCallback<PropertyDetailsDTO> callback, Key propertyId);

    public void retrieveFloorplanDetails(AsyncCallback<FloorplanDetailsDTO> callback, Key floorplanId);

}
