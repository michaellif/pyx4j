/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Dec 13, 2013
 * @author VladL
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui.util;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.folder.BoxFolderDecorator;
import com.pyx4j.forms.client.ui.folder.BoxFolderItemDecorator;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;

public class PortalBoxFolder<E extends IEntity> extends CEntityFolder<E> {

    private static final I18n i18n = I18n.get(PortalBoxFolder.class);

    private final String itemName;

    public PortalBoxFolder(Class<E> clazz) {
        this(clazz, true);
    }

    public PortalBoxFolder(Class<E> clazz, String itemName) {
        this(clazz, itemName, true);
    }

    public PortalBoxFolder(Class<E> clazz, boolean modifiable) {
        this(clazz, null, modifiable);
    }

    public PortalBoxFolder(Class<E> clazz, String itemName, boolean modifyable) {
        super(clazz);
        this.itemName = itemName;

        setAddable(modifyable);
        setRemovable(modifyable);
        setOrderable(modifyable);
    }

    @Override
    public IFolderItemDecorator<E> createItemDecorator() {
        BoxFolderItemDecorator<E> decor = new BoxFolderItemDecorator<E>(VistaImages.INSTANCE);
        return decor;
    }

    @Override
    protected IFolderDecorator<E> createFolderDecorator() {
        return new BoxFolderDecorator<E>(VistaImages.INSTANCE, "Add" + getItemName());
    }

    public String getItemName() {
        return (itemName != null ? itemName : "");
    }
}
