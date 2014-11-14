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
package com.propertyvista.crm.client.activity.crud.administration.auditrecords;

import com.pyx4j.site.client.backoffice.activity.prime.AbstractPrimeListerActivity;
import com.pyx4j.site.rpc.AppPlace;

import com.propertyvista.crm.client.CrmSite;
import com.propertyvista.crm.client.ui.crud.administration.auditrecord.CrmAuditRecordListerView;
import com.propertyvista.dto.AuditRecordDTO;

public class CrmAuditRecordsListerActivity extends AbstractPrimeListerActivity<AuditRecordDTO> {

    public CrmAuditRecordsListerActivity(AppPlace place) {
        super(AuditRecordDTO.class, place, CrmSite.getViewFactory().getView(CrmAuditRecordListerView.class));
    }

}
