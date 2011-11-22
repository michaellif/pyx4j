/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Nov 1, 2011
 * @author vladlouk
 * @version $Id$
 */
package com.propertyvista.portal.ptapp.client.ui.steps.apartment;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.financial.offering.Concession;

class ConcessionsFolder extends VistaTableFolder<Concession> {

    public ConcessionsFolder() {
        super(Concession.class, false);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().type(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().value(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().term(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "40em"));
        return columns;
    }
}