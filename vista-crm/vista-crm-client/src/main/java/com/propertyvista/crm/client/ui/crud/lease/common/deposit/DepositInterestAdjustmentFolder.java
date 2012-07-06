/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2012-07-06
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.lease.common.deposit;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.tenant.lease.DepositInterestAdjustment;

public class DepositInterestAdjustmentFolder extends VistaTableFolder<DepositInterestAdjustment> {

    public DepositInterestAdjustmentFolder(boolean modifiable) {
        super(DepositInterestAdjustment.class, modifiable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList( //@formatter:off
                new EntityFolderColumnDescriptor(proto().date(), "9em"),
                new EntityFolderColumnDescriptor(proto().interestRate(), "7em"),
                new EntityFolderColumnDescriptor(proto().amount(), "7em")
            ); //@formatter:on
    }
}
