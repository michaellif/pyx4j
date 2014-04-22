/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.selectorfolder;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.IDebugId;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.images.FolderImages;
import com.pyx4j.forms.client.ui.folder.CFolderItem;
import com.pyx4j.forms.client.ui.folder.IFolderItemDecorator;

public class SelectorFolderItemDecorator<E extends IEntity> extends Composite implements IFolderItemDecorator<E> {

    private final SimplePanel containerPanel;

    public SelectorFolderItemDecorator() {
        containerPanel = new SimplePanel();
        initWidget(containerPanel);
    }

    @Override
    public void init(CFolderItem<E> component) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setContent(IsWidget content) {
        containerPanel.setWidget(content);
    }

    @Override
    public void onSetDebugId(IDebugId parentDebugId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setActionsState(boolean remove, boolean up, boolean down) {
        // TODO Auto-generated method stub

    }

    @Override
    public void adoptItemActionsBar() {
        // TODO Auto-generated method stub

    }

    @Override
    public FolderImages getImages() {
        // TODO 
        return null;
    }
}
