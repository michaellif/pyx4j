/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Apr 14, 2014
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.server.services.importer;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.essentials.server.upload.UploadedData;
import com.pyx4j.gwt.server.deferred.AbstractDeferredProcess;

@SuppressWarnings("serial")
public class ImportBuildingDataDeferredProcess extends AbstractDeferredProcess {

    private final IEntity uploadInitiationData;

    private final UploadedData uploadedData;

    public ImportBuildingDataDeferredProcess(IEntity uploadInitiationData, UploadedData uploadedData) {
        super();
        this.uploadInitiationData = uploadInitiationData;
        this.uploadedData = uploadedData;
    }

    @Override
    public void execute() {
        // TODO Auto-generated method stub
    }

}
