/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 17, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.operations.client.activity.crud.auditrecords;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.ListerActivityBase;

import com.propertyvista.operations.client.ui.crud.auditrecords.AuditRecordListerView;
import com.propertyvista.operations.client.viewfactories.crud.SecurityViewFactory;
import com.propertyvista.operations.rpc.services.AuditRecordListerService;
import com.propertyvista.dto.AuditRecordDTO;

public class AuditRecordsActivity extends ListerActivityBase<AuditRecordDTO> {

    public AuditRecordsActivity(Place place) {
        super(place, SecurityViewFactory.instance(AuditRecordListerView.class), GWT.<AuditRecordListerService> create(AuditRecordListerService.class),
                AuditRecordDTO.class);
    }

}
