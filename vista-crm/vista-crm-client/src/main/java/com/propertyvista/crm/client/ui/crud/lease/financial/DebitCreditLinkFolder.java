/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2013-04-09
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.financial;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.crm.rpc.dto.lease.financial.DebitLinkDTO;

public class DebitCreditLinkFolder extends VistaTableFolder<DebitLinkDTO> {

    private static final List<EntityFolderColumnDescriptor> COLUMNS;
    static {
        DebitLinkDTO proto = EntityFactory.getEntityPrototype(DebitLinkDTO.class);
        COLUMNS = Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto.date(), "10em"),
                new EntityFolderColumnDescriptor(proto.arCodeType(), "10em"),
                new EntityFolderColumnDescriptor(proto.description(), "20em"),
                new EntityFolderColumnDescriptor(proto.outstandingAmount(), "10em"),
                new EntityFolderColumnDescriptor(proto.paidAmount(), "10em") 
        );//@formatter:on
    }

    public DebitCreditLinkFolder() {
        super(DebitLinkDTO.class);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return COLUMNS;
    }

}
