/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-07-17
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.operations.client.ui.crud.outgoingmail;

import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.backoffice.ui.prime.lister.AbstractLister;

import com.propertyvista.operations.rpc.dto.OutgoingMailQueueDTO;

public class OutgoingMailLister extends AbstractLister<OutgoingMailQueueDTO> {

    public OutgoingMailLister() {
        super(OutgoingMailQueueDTO.class, false, false);

        setDataTableModel(new DataTableModel<OutgoingMailQueueDTO>(//@formatter:off
                    new MemberColumnDescriptor.Builder(proto().status()).build(),
                    new MemberColumnDescriptor.Builder(proto().namespace()).build(),
                    new MemberColumnDescriptor.Builder(proto().configurationId()).build(),
                    new MemberColumnDescriptor.Builder(proto().statusCallbackClass()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().created()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().updated()).build(),
                    new MemberColumnDescriptor.Builder(proto().attempts()).build(),
                    new MemberColumnDescriptor.Builder(proto().priority()).build(),
                    new MemberColumnDescriptor.Builder(proto().lastAttemptErrorMessage()).visible(false).build(),
                    new MemberColumnDescriptor.Builder(proto().sendTo()).build(),
                    new MemberColumnDescriptor.Builder(proto().sentDate()).build(),
                    new MemberColumnDescriptor.Builder(proto().messageId()).build(),
                    new MemberColumnDescriptor.Builder(proto().keywords()).visible(false).build()
            ));//@formatter:on
    }
}
