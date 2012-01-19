/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jan 9, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.crud.policies.pet;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.components.folders.VistaTableFolder;
import com.propertyvista.domain.policy.policies.specials.PetConstraints;

public class PetConstraintsFolder extends VistaTableFolder<PetConstraints> {

    public PetConstraintsFolder() {
        super(PetConstraints.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(

        new EntityFolderColumnDescriptor(proto().pet().name(), "10em"),

        new EntityFolderColumnDescriptor(proto().maxNumber(), "10em"),

        new EntityFolderColumnDescriptor(proto().maxWeight(), "10em"));
    }

}
