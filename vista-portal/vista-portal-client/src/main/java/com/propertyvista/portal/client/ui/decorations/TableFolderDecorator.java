/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Jul 11, 2011
 * @author dad
 * @version $Id$
 */
package com.propertyvista.portal.client.ui.decorations;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder;
import com.pyx4j.entity.client.ui.flex.folder.IFolderDecorator;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.widgets.client.style.IStyleSuffix;

public class TableFolderDecorator<E extends IEntity> extends SimplePanel implements IFolderDecorator<E> {

    public static String DEFAULT_STYLE_PREFIX = "TableFolderDecorator";

    public static enum StyleSuffix implements IStyleSuffix {
        Header
    }

    public TableFolderDecorator() {
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        // TODO Auto-generated method stub

    }

    @Override
    public HandlerRegistration addItemAddClickHandler(ClickHandler handler) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setFolder(CEntityFolder<?> folder) {
        setWidget(folder.getContainer());
    }

}
