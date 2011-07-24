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

import com.pyx4j.entity.client.ui.flex.CEntityForm;
import com.pyx4j.entity.client.ui.flex.EntityFolderColumnDescriptor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderRowEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.IFolderItemEditorDecorator;
import com.pyx4j.entity.shared.IEntity;

public abstract class CrmEntityFolder<E extends IEntity> extends CEntityFolderEditor<E> {
    protected static I18n i18n = I18nFactory.getI18n(CrmEntityFolder.class);

    private final Class<E> clazz;

    private final String itemName;

    private final boolean editable;

    private final CEntityForm<?> parent;

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable) {
        this(clazz, itemName, editable, null);
    }

    public CrmEntityFolder(Class<E> clazz, String itemName, boolean editable, CEntityForm<?> parent) {
        super(clazz);
        this.clazz = clazz;
        this.itemName = itemName;
        this.editable = editable;
        this.parent = parent;
    }

    protected abstract List<EntityFolderColumnDescriptor> columns();

    @Override
    protected CEntityFolderItemEditor<E> createItem() {
        return new CEntityFolderRowEditor<E>(clazz, columns()) {
            @Override
            public IFolderItemEditorDecorator<E> createFolderItemDecorator() {
                return new CrmFolderItemDecorator<E>(i18n.tr("Remove ") + itemName, editable);
            }
        };
    }

    @Override
    protected IFolderEditorDecorator<E> createFolderDecorator() {
        return new CrmTableFolderDecorator<E>(columns(), i18n.tr("Add new ") + itemName, editable);
    }
}