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
package com.propertyvista.common.client.ui;

import java.util.List;

import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.IFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderDecorator;
import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;

public abstract class VistaTableFolder<E extends IEntity> extends CEntityFolder<E> {
    protected static I18n i18n = I18n.get(VistaTableFolder.class);

    private final String itemName;

    private Class<E> clazz;

    public VistaTableFolder(Class<E> clazz) {
        this(clazz, true);
    }

    public VistaTableFolder(Class<E> clazz, String itemName) {
        this(clazz, itemName, true);
    }

    public VistaTableFolder(Class<E> clazz, boolean editable) {
        this(clazz, null, editable);
    }

    public VistaTableFolder(Class<E> clazz, String itemName, boolean editable) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        setModifiable(editable);
        setOrderable(editable);
    }

    protected abstract List<EntityFolderColumnDescriptor> columns();

    @Override
    protected IFolderDecorator<E> createDecorator() {
        return new TableFolderDecorator<E>(columns(), VistaImages.INSTANCE, i18n.tr("Add ") + getItemName());
    }

    @Override
    protected IFolderItemDecorator<E> createItemDecorator() {
        return new TableFolderItemDecorator<E>(VistaImages.INSTANCE, i18n.tr("Remove ") + getItemName());
    }

    public String getItemName() {
        return (itemName != null ? itemName : "");
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        if (clazz.equals(member.getObjectClass())) {
            return new CEntityFolderRowEditor<E>(clazz, columns());
        } else {
            return super.create(member);
        }
    }

}