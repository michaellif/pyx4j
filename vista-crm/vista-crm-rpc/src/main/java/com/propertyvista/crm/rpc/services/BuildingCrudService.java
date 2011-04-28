/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 17, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.rpc.services;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.propertyvista.portal.domain.Building;

public interface BuildingCrudService extends AbstractCrudService<Building> {

    public void getTestBuildingNomberOne(AsyncCallback<Building> callback);

    public void getTestBuildingsList(AsyncCallback<Vector<Building>> callback);
}
