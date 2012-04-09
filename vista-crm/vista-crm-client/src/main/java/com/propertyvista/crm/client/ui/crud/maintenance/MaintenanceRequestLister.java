/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 20, 2011
 * @author stanp
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.maintenance;

import com.pyx4j.entity.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.crud.lister.ListerBase;

import com.propertyvista.dto.MaintenanceRequestDTO;

public class MaintenanceRequestLister extends ListerBase<MaintenanceRequestDTO> {

    public MaintenanceRequestLister() {
        super(MaintenanceRequestDTO.class, false, true);

        setColumnDescriptors( // @formatter:off
                new MemberColumnDescriptor.Builder(proto().issueClassification().issue()).build(),
                new MemberColumnDescriptor.Builder(proto().issueClassification().priority()).build(),
                new MemberColumnDescriptor.Builder(proto().description()).build(),
                new MemberColumnDescriptor.Builder(proto().tenant().person().name()).searchable(false).build(),
                new MemberColumnDescriptor.Builder(proto().tenant().person().name().firstName(), false).build(),
                new MemberColumnDescriptor.Builder(proto().tenant().person().name().lastName(), false).build(),
                new MemberColumnDescriptor.Builder(proto().submitted()).build(),
                new MemberColumnDescriptor.Builder(proto().status()).build(),
                new MemberColumnDescriptor.Builder(proto().updated()).build(),
                new MemberColumnDescriptor.Builder(proto().surveyResponse().rating()).build(),
                new MemberColumnDescriptor.Builder(proto().surveyResponse().description(), false).columnTitle(proto().surveyResponse().getMeta().getCaption()).build()
            ); // @formatter:on
    }
}
