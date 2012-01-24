/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 19, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;

import com.propertyvista.dto.ApplicationStatusDTO;

public class ApplicationStatusFolder extends VistaTableFolder<ApplicationStatusDTO> {

    public ApplicationStatusFolder(boolean modifyable) {
        super(ApplicationStatusDTO.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().person(), "25em"));
        columns.add(new EntityFolderColumnDescriptor(proto().role(), "10em"));
        columns.add(new EntityFolderColumnDescriptor(proto().progress(), "7em"));
        columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
        return columns;
    }
}