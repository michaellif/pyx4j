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
package com.propertyvista.crm.client.ui.crud.settings.content.site;

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.site.AvailableLocale;

class AvailableLocaleFolder extends VistaTableFolder<AvailableLocale> {

    public AvailableLocaleFolder(boolean modifyable) {
        super(AvailableLocale.class, modifyable);
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        ArrayList<EntityFolderColumnDescriptor> columns = new ArrayList<EntityFolderColumnDescriptor>();
        columns.add(new EntityFolderColumnDescriptor(proto().lang(), "10em"));
        return columns;
    }

}