/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.media;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.pyx4j.entity.client.ui.flex.editor.BoxFolderEditorDecorator;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderEditor;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItemEditor;
import com.pyx4j.entity.client.ui.flex.editor.IFolderEditorDecorator;

import com.propertyvista.crm.client.resources.CrmImages;
import com.propertyvista.domain.media.Media;

public class CrmMediaListFolderEditor extends CEntityFolderEditor<Media> {

    protected static I18n i18n = I18nFactory.getI18n(CrmMediaListFolderEditor.class);

    private final boolean editable;

    public CrmMediaListFolderEditor(boolean editable) {
        super(Media.class);
        this.editable = editable;
    }

    @Override
    protected IFolderEditorDecorator<Media> createFolderDecorator() {
        return new BoxFolderEditorDecorator<Media>(CrmImages.INSTANCE.add(), CrmImages.INSTANCE.addHover(), i18n.tr("Add one more Media"), editable);
    }

    @Override
    protected CEntityFolderItemEditor<Media> createItem() {
        return new CrmMediaFolderItemEditor(editable);
    }

}
