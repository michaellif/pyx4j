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
package com.propertyvista.common.client.ui.decorations;

import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.VistaTableFolder;

public class VistaTableFolderDecorator<E extends IEntity> extends TableFolderDecorator<E> {
    protected static I18n i18n = I18n.get(VistaTableFolderDecorator.class);

    public VistaTableFolderDecorator(List<EntityFolderColumnDescriptor> columns) {
        super(columns, VistaImages.INSTANCE);
    }

    public VistaTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, String addLabel) {
        super(columns, VistaImages.INSTANCE, addLabel);
    }

    public VistaTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, String addLabel, boolean addable) {
        super(columns, VistaImages.INSTANCE, addLabel, addable);
    }

    public VistaTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, VistaTableFolder<E> parent) {
        super(columns, VistaImages.INSTANCE, i18n.tr("Add {0}", parent.getItemName()), parent.isEditable());
    }
}
