/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 13, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.components;

import com.pyx4j.site.client.ui.crud.lister.BasicLister;

import com.propertyvista.crm.client.ui.crud.maintenance.MaintenanceRequestLister;
import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestsDetailsLister extends BasicLister<MaintenanceRequestDTO> {

    public MaintenanceRequestsDetailsLister() {
        super(MaintenanceRequestDTO.class);
        setColumnDescriptors(MaintenanceRequestLister.createColumnDescriptors());
    }

}
