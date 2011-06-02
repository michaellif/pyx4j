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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;

import com.pyx4j.entity.client.ui.flex.editor.BaseFolderItemDecorator;
import com.pyx4j.entity.client.ui.flex.editor.CEntityFolderItem;

import com.propertyvista.crm.client.resources.CrmImages;

public class CrmFolderItemDecorator extends BaseFolderItemDecorator {

    protected static I18n i18n = I18nFactory.getI18n(CrmFolderItemDecorator.class);

    protected final Button actionButton;

    public CrmFolderItemDecorator(String title, boolean editable) {
        super(CrmImages.INSTANCE.del(), CrmImages.INSTANCE.delHover(), title, editable);
        actionButton = new Button(i18n.tr(editable ? "Edit" : "View"));
        actionButton.getElement().getStyle().setMarginLeft(2, Unit.EM);
        getRowHolder().add(actionButton);
        setWidget(getRowHolder());
    }

    @Override
    public void setFolderItem(CEntityFolderItem<?> folderItem) {
        folderItem.setEditable(false);
        super.setFolderItem(folderItem);
    }

    @Override
    public HandlerRegistration addItemClickHandler(ClickHandler handler) {
        return actionButton.addClickHandler(handler);
    }

    @Override
    public HandlerRegistration addItemRemoveClickHandler(ClickHandler handler) {
        if (isRemovable() && getRemoveImage() != null) {
            return getRemoveImage().addClickHandler(handler);
        }
        return null;
    }

    @Override
    public HandlerRegistration addRowUpClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowDownClickHandler(ClickHandler handler) {
        return null;
    }

    @Override
    public HandlerRegistration addRowCollapseClickHandler(ClickHandler handler) {
        return null;
    }
}
