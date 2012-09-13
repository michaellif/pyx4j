/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.folders;

import java.util.Arrays;
import java.util.List;

import com.pyx4j.forms.client.ui.folder.EntityFolderColumnDescriptor;

import com.propertyvista.domain.company.CompanyPhone;

public class CompanyPhoneFolder extends VistaTableFolder<CompanyPhone> {

    public CompanyPhoneFolder(boolean modifyable) {
        super(CompanyPhone.class, modifyable);
    }

    @Override
    public List<EntityFolderColumnDescriptor> columns() {
        return Arrays.asList(new EntityFolderColumnDescriptor(proto().phone(), "15em"));
    }
}
