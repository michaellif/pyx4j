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
package com.propertyvista.portal.ptapp.client.ui.components;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.flex.editor.BoxFolderDecorator;
import com.pyx4j.entity.shared.IEntity;

import com.propertyvista.portal.ptapp.client.resources.PortalImages;

public class PtAppBoxFolderDecorator<E extends IEntity> extends BoxFolderDecorator<E> {
    protected static I18n i18n = I18nFactory.getI18n(PtAppBoxFolderDecorator.class);

    public PtAppBoxFolderDecorator(String title, boolean editable) {
        super(PortalImages.INSTANCE.add(), PortalImages.INSTANCE.addHover(), title, editable);
    }

    public PtAppBoxFolderDecorator(PtAppEntityFolder<E> parent, boolean editable) {
        this(i18n.tr("Add ") + parent.getItemName(), editable);
    }

    public PtAppBoxFolderDecorator(PtAppEntityFolder<E> parent) {
        this(parent, parent.isEditable());
    }
}
