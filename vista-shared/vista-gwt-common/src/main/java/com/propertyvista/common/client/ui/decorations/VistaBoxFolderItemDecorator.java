/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 8, 2011
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.common.client.ui.decorations;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.flex.folder.BoxFolderItemDecorator;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.common.client.resources.VistaImages;
import com.propertyvista.common.client.ui.VistaEntityFolder;

public class VistaBoxFolderItemDecorator<E extends IEntity> extends BoxFolderItemDecorator<E> {
    protected static I18n i18n = I18nFactory.getI18n(VistaBoxFolderItemDecorator.class);

    public VistaBoxFolderItemDecorator(String title, boolean editable) {
        super(VistaImages.INSTANCE, title, editable);
    }

    public VistaBoxFolderItemDecorator(VistaEntityFolder<E> parent, boolean editable) {
        this(i18n.tr("Remove ") + parent.getItemName(), editable);
    }

    public VistaBoxFolderItemDecorator(VistaEntityFolder<E> parent) {
        this(parent, parent.isEditable());
    }

}