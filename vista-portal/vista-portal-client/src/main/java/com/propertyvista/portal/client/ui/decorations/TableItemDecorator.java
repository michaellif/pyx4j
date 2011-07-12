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

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.ui.flex.viewer.CEntityFolderItemViewer;
import com.pyx4j.entity.client.ui.flex.viewer.IFolderItemViewerDecorator;
import com.pyx4j.entity.shared.IEntity;

public class TableItemDecorator<E extends IEntity> extends SimplePanel implements IFolderItemViewerDecorator<E> {

    public static String DEFAULT_STYLE_PREFIX = "TableItemDecorator";

    private CEntityFolderItemViewer<E> viewer;

    public TableItemDecorator() {
        setStyleName(DEFAULT_STYLE_PREFIX);
    }

    @Override
    public void setFolderItem(CEntityFolderItemViewer<E> viewer) {
        this.viewer = viewer;
        setWidget(viewer.getContainer());
    }
}
