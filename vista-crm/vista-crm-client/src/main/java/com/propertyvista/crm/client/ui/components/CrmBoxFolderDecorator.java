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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.flex.editor.BoxFolderEditorDecorator;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.crm.client.resources.CrmImages;

public class CrmBoxFolderDecorator<E extends IEntity> extends BoxFolderEditorDecorator<E> {
    protected static I18n i18n = I18nFactory.getI18n(CrmBoxFolderDecorator.class);

    public CrmBoxFolderDecorator(String title, boolean editable) {
        super(CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), title, editable);
    }

    public CrmBoxFolderDecorator(CrmEntityFolder<E> parent, boolean editable) {
        this(i18n.tr("Add new ") + parent.getItemName(), editable);
    }

    public CrmBoxFolderDecorator(CrmEntityFolder<E> parent) {
        this(parent, parent.isEditable());
    }
}
