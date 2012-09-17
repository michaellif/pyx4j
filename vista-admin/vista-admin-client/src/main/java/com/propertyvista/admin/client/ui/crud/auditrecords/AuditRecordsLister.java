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
package com.propertyvista.admin.client.ui.crud.auditrecords;

import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.AuditRecordDTO;

public class AuditRecordsLister extends ListerBase<AuditRecordDTO> {

    public AuditRecordsLister() {
        super(AuditRecordDTO.class);
        setColumnDescriptors(//@formatter:off
                new MemberColumnDescriptor.Builder(proto().when()).build(),
                new MemberColumnDescriptor.Builder(proto().pmc()).build(),
                new MemberColumnDescriptor.Builder(proto().userName()).build(),
                new MemberColumnDescriptor.Builder(proto().remoteAddr()).build(),
                new MemberColumnDescriptor.Builder(proto().app()).build(),
                new MemberColumnDescriptor.Builder(proto().event()).build(),
                new MemberColumnDescriptor.Builder(proto().details()).build()                
        );//@formatter:on
    }
}
