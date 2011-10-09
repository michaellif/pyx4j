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


import com.pyx4j.entity.client.ui.flex.folder.TableFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.VistaEntityFolder;

public class VistaTableFolderItemDecorator<E extends IEntity> extends TableFolderItemDecorator<E> {
    protected static I18n i18n = I18n.get(VistaTableFolderItemDecorator.class);

    public VistaTableFolderItemDecorator(String title, boolean editable) {
        super(VistaImages.INSTANCE, title, editable);
    }

    public VistaTableFolderItemDecorator(VistaEntityFolder<E> parent, boolean editable) {
        this(i18n.tr("Remove ") + parent.getItemName(), editable);
    }

    public VistaTableFolderItemDecorator(VistaEntityFolder<E> parent) {
        this(parent, parent.isEditable());
    }

}
