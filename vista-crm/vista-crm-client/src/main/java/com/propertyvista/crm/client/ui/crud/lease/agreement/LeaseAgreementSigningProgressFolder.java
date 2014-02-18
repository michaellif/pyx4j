/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-02-13
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.agreement;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.dto.LeaseAgreementStakeholderSigningProgressDTO;

public class LeaseAgreementSigningProgressFolder extends VistaTableFolder<LeaseAgreementStakeholderSigningProgressDTO> {

    public LeaseAgreementSigningProgressFolder() {
        super(LeaseAgreementStakeholderSigningProgressDTO.class);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(//@formatter:off
                new EntityFolderColumnDescriptor(proto().name(), "200px"),
                new EntityFolderColumnDescriptor(proto().role(), "150px"),
                new EntityFolderColumnDescriptor(proto().hasSigned(), "150px"),
                new EntityFolderColumnDescriptor(proto().singatureType(), "150px")
        );//@formatter:on
    }
}
