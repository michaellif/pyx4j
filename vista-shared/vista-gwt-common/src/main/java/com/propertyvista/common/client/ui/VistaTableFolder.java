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

import com.pyx4j.entity.client.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.folder.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.folder.DefaultEntityFolderTheme;
import com.pyx4j.entity.client.ui.folder.IFolderDecorator;
import com.pyx4j.entity.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.ui.decorations.VistaTableFolderDecorator;
import com.propertyvista.common.client.ui.decorations.VistaTableFolderItemDecorator;

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

    public VistaTableFolder(Class<E> clazz, boolean modifiable) {
        this(clazz, null, modifiable);
    }

    public VistaTableFolder(Class<E> clazz, String itemName, boolean modifyable) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        setModifiable(modifyable);
        setOrderable(modifyable);
        if (!modifyable) {
            asWidget().addStyleDependentName(DefaultEntityFolderTheme.StyleDependent.readOnly.name());
        }
    }

    public abstract List<EntityFolderColumnDescriptor> columns();

    @Override
    protected IFolderDecorator<E> createDecorator() {
        return new VistaTableFolderDecorator<E>(this);
    }

    @Override
    protected IFolderItemDecorator<E> createItemDecorator() {
        return new VistaTableFolderItemDecorator<E>(this);
    }

    public String getItemName() {
        return (itemName != null ? itemName : "");
    }

    @Override
    public CComponent<?, ?> create(IObject<?> member) {
        if (clazz.equals(member.getObjectClass())) {
            return new CEntityFolderRowEditor<E>(clazz, columns());
        } else {
            return super.create(member);
        }
    }
}