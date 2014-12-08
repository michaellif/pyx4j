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

import com.google.gwt.core.client.GWT;

import com.pyx4j.entity.rpc.AbstractCrudService;
import com.pyx4j.forms.client.ui.datatable.DataTableModel;
import com.pyx4j.forms.client.ui.datatable.MemberColumnDescriptor;
import com.pyx4j.site.client.ui.SiteDataTablePanel;

import com.propertyvista.operations.rpc.dto.OutgoingMailQueueDTO;
import com.propertyvista.operations.rpc.services.OutgoingMailCrudService;

public class OutgoingMailLister extends SiteDataTablePanel<OutgoingMailQueueDTO> {

    public OutgoingMailLister() {
        super(OutgoingMailQueueDTO.class, GWT.<AbstractCrudService<OutgoingMailQueueDTO>> create(OutgoingMailCrudService.class), false, false);

        setColumnDescriptors( //
                new MemberColumnDescriptor.Builder(proto().status()).build(), //
                new MemberColumnDescriptor.Builder(proto().namespace()).build(), //
                new MemberColumnDescriptor.Builder(proto().configurationId()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().statusCallbackClass()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().created()).build(), //
                new MemberColumnDescriptor.Builder(proto().updated()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().attempts()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().priority()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().lastAttemptErrorMessage()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().sendTo()).build(), //
                new MemberColumnDescriptor.Builder(proto().sentDate()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().messageId()).visible(false).build(), //
                new MemberColumnDescriptor.Builder(proto().keywords()).build() //
        );

        setDataTableModel(new DataTableModel<OutgoingMailQueueDTO>());
    }
}
