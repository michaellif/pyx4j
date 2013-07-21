/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on May 25, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.rpc.services.simulator;

import java.util.Vector;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.rpc.shared.IService;
import com.pyx4j.rpc.shared.VoidSerializable;

//TODO Remove , use data preloaders
@Deprecated
public interface SimulatedDataPreloadService extends IService {

    void generateArrearsHistory(AsyncCallback<VoidSerializable> callback);

    /**
     * @return if during history generation process: vector v containing two values: v.get(0) is current progress, v.get(1) is maximum progress, else return
     *         <code>null</code>
     */
    void getArrearsHistoryGenerationProgress(AsyncCallback<Vector<Integer>> callback);

    void generateMaintenanceRequests(AsyncCallback<VoidSerializable> callback);

}
