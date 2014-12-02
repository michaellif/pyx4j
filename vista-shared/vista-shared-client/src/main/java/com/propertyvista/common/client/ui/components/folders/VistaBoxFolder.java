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
package com.propertyvista.common.client.ui.components.folders;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.folder.CFolder;

import com.propertyvista.common.client.ui.decorations.VistaBoxFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaBoxFolderItemDecorator;

public abstract class VistaBoxFolder<E extends IEntity> extends CFolder<E> {

    private final String itemName;

    public VistaBoxFolder(Class<E> clazz) {
        this(clazz, true);
    }

    public VistaBoxFolder(Class<E> clazz, String itemName) {
        this(clazz, itemName, true);
    }

    public VistaBoxFolder(Class<E> clazz, boolean modifiable) {
        this(clazz, null, modifiable);
    }

    public VistaBoxFolder(Class<E> clazz, String itemName, boolean modifyable) {
        super(clazz);
        this.itemName = itemName;
        setAddable(modifyable);
        setRemovable(modifyable);
        setOrderable(modifyable);
    }

    @Override
    protected VistaBoxFolderDecorator<E> createFolderDecorator() {
        return new VistaBoxFolderDecorator<E>(this);
    }

    @Override
    public VistaBoxFolderItemDecorator<E> createItemDecorator() {
        return new VistaBoxFolderItemDecorator<E>(this);
    }

    public String getItemName() {
        return (itemName != null ? itemName : "");
    }
}