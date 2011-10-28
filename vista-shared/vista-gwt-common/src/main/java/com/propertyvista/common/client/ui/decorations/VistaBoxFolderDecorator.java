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

import com.pyx4j.entity.client.ui.flex.folder.BoxFolderDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.VistaBoxFolder;

public class VistaBoxFolderDecorator<E extends IEntity> extends BoxFolderDecorator<E> {
    protected static I18n i18n = I18n.get(VistaBoxFolderDecorator.class);

    public VistaBoxFolderDecorator(String removeLabel, boolean editable) {
        super(VistaImages.INSTANCE, removeLabel, editable);
    }

    public VistaBoxFolderDecorator(VistaBoxFolder<E> parent, boolean addable) {
        this(i18n.tr("Add {0}", parent.getItemName()), addable);
    }

    public VistaBoxFolderDecorator(VistaBoxFolder<E> parent) {
        this(parent, parent.isEditable());
    }
}
