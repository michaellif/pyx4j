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
package com.propertyvista.crm.client.ui.crud.auditrecord;

import com.propertyvista.crm.client.ui.crud.CrmListerViewImplBase;
import com.propertyvista.crm.rpc.CrmSiteMap;
import com.propertyvista.dto.AuditRecordDTO;

public class CrmAuditRecordListerViewImpl extends CrmListerViewImplBase<AuditRecordDTO> {

    public CrmAuditRecordListerViewImpl() {
        super(CrmSiteMap.Settings.Security.AuditRecords.class);
        setLister(new CrmAuditRecordsLister());
    }
}
