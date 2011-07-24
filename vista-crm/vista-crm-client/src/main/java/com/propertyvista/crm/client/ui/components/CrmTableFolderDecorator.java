/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-05-24
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components;

import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.client.resources.CrmImages;

public class CrmTableFolderDecorator<E extends IEntity> extends TableFolderEditorDecorator<E> {

    public CrmTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, String title, boolean editable) {
        super(columns, CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), title, editable);
    }
}
