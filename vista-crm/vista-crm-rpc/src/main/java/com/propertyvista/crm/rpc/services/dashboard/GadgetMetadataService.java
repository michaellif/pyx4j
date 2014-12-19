/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-10-02
 * @author ArtyomB
 */
package com.propertyvista.crm.rpc.services.dashboard;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;

import com.propertyvista.crm.rpc.dto.dashboard.GadgetDescriptorDTO;
import com.propertyvista.domain.dashboard.DashboardMetadata;
import com.propertyvista.domain.dashboard.gadgets.type.base.GadgetMetadata;

public interface GadgetMetadataService extends IService {

    public void listAvailableGadgets(AsyncCallback<Vector<GadgetDescriptorDTO>> callback, DashboardMetadata.DashboardType boardType);

    /**
     * @param proto
     *            used to request the type of a gadget metadata (the instance value class of the gadget)
     */
    public void createGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata proto);

    public void saveGadgetMetadata(AsyncCallback<GadgetMetadata> callback, GadgetMetadata gadgetMetadata);

}
