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
package com.propertyvista.crm.client.activity.crud.auditrecords;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;

import com.pyx4j.site.client.activity.ListerActivityBase;
import com.pyx4j.site.client.ui.crud.lister.ILister;

import com.propertyvista.crm.client.ui.crud.auditrecord.CrmAuditRecordListerView;
import com.propertyvista.crm.client.ui.viewfactories.SecurityViewFactory;
import com.propertyvista.crm.rpc.services.security.CrmAuditRecordsListerService;
import com.propertyvista.dto.AuditRecordDTO;

public class CrmAuditRecordsListerActivity extends ListerActivityBase<AuditRecordDTO> {

    public CrmAuditRecordsListerActivity(Place place) {
        super(place, (ILister<AuditRecordDTO>) SecurityViewFactory.instance(CrmAuditRecordListerView.class), GWT
                .<CrmAuditRecordsListerService> create(CrmAuditRecordsListerService.class), AuditRecordDTO.class);
    }

}
