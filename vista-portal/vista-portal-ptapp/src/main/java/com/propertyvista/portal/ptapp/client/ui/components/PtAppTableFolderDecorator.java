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
package com.propertyvista.portal.ptapp.client.ui.components;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.TableFolderEditorDecorator;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.portal.ptapp.client.resources.PortalImages;

public class PtAppTableFolderDecorator<E extends IEntity> extends TableFolderEditorDecorator<E> {
    protected static I18n i18n = I18nFactory.getI18n(PtAppTableFolderDecorator.class);

    public PtAppTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, String title, boolean editable) {
        super(columns, PortalImages.INSTANCE.add(), PortalImages.INSTANCE.addHover(), title, editable);
    }

    public PtAppTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, PtAppEntityFolder<E> parent, boolean editable) {
        this(columns, i18n.tr("Add new ") + parent.getItemName(), editable);
    }

    public PtAppTableFolderDecorator(List<EntityFolderColumnDescriptor> columns, PtAppEntityFolder<E> parent) {
        this(columns, parent, parent.isEditable());
    }
}
