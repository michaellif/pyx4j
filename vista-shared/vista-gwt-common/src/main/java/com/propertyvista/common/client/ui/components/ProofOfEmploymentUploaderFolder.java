/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 27, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.shared.EntityFactory;

import com.propertyvista.domain.media.ApplicationDocument;

public class ProofOfEmploymentUploaderFolder extends ApplicationDocumentUploaderFolder {

    private static final List<EntityFolderColumnDescriptor> PERSONAL_INCOME_COLUMNS;

    static {
        ApplicationDocument proto = EntityFactory.getEntityPrototype(ApplicationDocument.class);
        PERSONAL_INCOME_COLUMNS = new ArrayList<EntityFolderColumnDescriptor>();
        PERSONAL_INCOME_COLUMNS.add(new EntityFolderColumnDescriptor(proto.details(), "15em"));
        PERSONAL_INCOME_COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileName(), "25em"));
        PERSONAL_INCOME_COLUMNS.add(new EntityFolderColumnDescriptor(proto.fileSize(), "5em"));
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return PERSONAL_INCOME_COLUMNS;
    }

}
