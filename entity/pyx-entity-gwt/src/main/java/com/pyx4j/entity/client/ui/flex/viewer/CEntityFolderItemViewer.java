/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 *
 * Created on Feb 13, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.viewer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;

public abstract class CEntityFolderItemViewer<E extends IEntity> extends CEntityViewer<E> {

    private final SimplePanel container;

    private boolean first;

    private boolean last;

    public CEntityFolderItemViewer() {
        container = new SimplePanel();
    }

    public abstract IFolderItemViewerDecorator createFolderItemDecorator();

    public void setFolderItemDecorator(IFolderItemViewerDecorator folderItemDecorator) {
        asWidget().setWidget(folderItemDecorator);
        folderItemDecorator.setFolderItemContainer(container);
    }

    @Override
    protected void setContent(IsWidget widget) {
        container.setWidget(widget);
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return first;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public boolean isLast() {
        return last;
    }

}
