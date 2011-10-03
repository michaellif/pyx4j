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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;

public abstract class CrmEntityFolder<E extends IEntity> extends CEntityFolder<E> {
    protected static I18n i18n = I18nFactory.getI18n(CrmEntityFolder.class);

    private final Class<E> clazz;

    private final String itemName;

    private final boolean editable;

    public CrmEntityFolder(Class<E> clazz) {
        this(clazz, true);
    }

    public CrmEntityFolder(Class<E> clazz, String itemName) {
        this(clazz, itemName, true);
    }

    public CrmEntityFolder(Class<E> clazz, boolean editable) {
        this(clazz, null, editable);
    }

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        this.editable = editable;
    }

    protected abstract List<EntityFolderColumnDescriptor> columns();

    @Override
    protected CEntityFolderItemEditor<E> createItem() {
        return new CEntityFolderRowEditor<E>(clazz, columns()) {
            @Override
            public IFolderItemDecorator<E> createDecorator() {
                return new CrmTableFolderItemDecorator<E>(i18n.tr("Remove ") + getItemName(), editable);
            }
        };
    }

    @Override
    protected IFolderDecorator<E> createDecorator() {
        return new CrmTableFolderDecorator<E>(columns(), i18n.tr("Add ") + getItemName(), editable);
    }

    public String getItemName() {
        return (itemName != null ? itemName : "");
    }

    @Override
    public boolean isEditable() {
        return editable;
    }
}