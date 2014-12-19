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
 */
package com.propertyvista.common.client.ui.components.folders;

import java.util.List;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.folder.CFolder;
import com.pyx4j.forms.client.ui.folder.CFolderRowEditor;
import com.pyx4j.forms.client.ui.folder.FolderColumnDescriptor;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

import com.propertyvista.common.client.ui.components.VistaViewersComponentFactory;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderItemDecorator;

public abstract class VistaTableFolder<E extends IEntity> extends CFolder<E> {

    private final String itemName;

    private boolean modifyable;

    private Class<E> clazz;

    public VistaTableFolder(Class<E> clazz) {
        this(clazz, true);
    }

    public VistaTableFolder(Class<E> clazz, String itemName) {
        this(clazz, itemName, true);
    }

    public VistaTableFolder(Class<E> clazz, boolean modifiable) {
        this(clazz, null, modifiable);
    }

    public VistaTableFolder(Class<E> clazz, String itemName, boolean modifyable) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        this.modifyable = modifyable;
        setAddable(modifyable);
        setRemovable(modifyable);
        setOrderable(modifyable);
    }

    public abstract List<FolderColumnDescriptor> columns();

    @Override
    protected IFolderDecorator<E> createFolderDecorator() {
        return new VistaTableFolderDecorator<E>(this, modifyable);
    }

    @Override
    protected IFolderItemDecorator<E> createItemDecorator() {
        return new VistaTableFolderItemDecorator<E>(this);
    }

    public String getItemName() {
        return (itemName != null ? itemName : "");
    }

    @Override
    protected CForm<? extends E> createItemForm(IObject<?> member) {
        return new CFolderRowEditor<E>(clazz, columns(), new VistaViewersComponentFactory());
    }
}