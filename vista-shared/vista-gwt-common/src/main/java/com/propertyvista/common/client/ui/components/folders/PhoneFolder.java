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

import java.util.ArrayList;
import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;

import com.propertyvista.common.client.ui.VistaTableFolder;
import com.propertyvista.domain.contact.Phone;

public class PhoneFolder extends VistaTableFolder<Phone> {

    private final boolean showType, showDescription;

    public PhoneFolder(boolean editable) {
        this(editable, true, false);
    }

    public PhoneFolder(boolean editable, boolean showType, boolean showDescription) {
        super(Phone.class, editable);
        this.showType = showType;
        this.showDescription = showDescription;
    }

    @Override
    protected List<EntityFolderColumnDescriptor> columns() {
        List<EntityFolderColumnDescriptor> columns;
        columns = new ArrayList<EntityFolderColumnDescriptor>();
        if (showType) {
            columns.add(new EntityFolderColumnDescriptor(proto().type(), "8em"));
        }
        columns.add(new EntityFolderColumnDescriptor(proto().number(), "11em"));
        columns.add(new EntityFolderColumnDescriptor(proto().extension(), "5em"));
        if (showDescription) {
            columns.add(new EntityFolderColumnDescriptor(proto().description(), "20em"));
        }
        return columns;
    }
}
