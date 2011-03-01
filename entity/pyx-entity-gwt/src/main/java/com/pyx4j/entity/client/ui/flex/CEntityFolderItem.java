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
package com.pyx4j.entity.client.ui.flex;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.commons.CompositeDebugId;
import com.pyx4j.commons.IDebugId;
import com.pyx4j.commons.StringDebugId;
import com.pyx4j.entity.shared.IEntity;

public abstract class CEntityFolderItem<E extends IEntity> extends CEntityEditableComponent<E> {

    private static final Logger log = LoggerFactory.getLogger(CEntityFolderItem.class);

    private FolderItemDecorator folderItemDecorator;

    private final SimplePanel content;

    private boolean first;

    private IDebugId rowDebugId;

    public CEntityFolderItem(Class<E> clazz) {
        super(clazz);
        content = new SimplePanel();
    }

    public abstract FolderItemDecorator createFolderItemDecorator();

    public void setFolderItemDecorator(FolderItemDecorator folderItemDecorator) {
        this.folderItemDecorator = folderItemDecorator;
        folderItemDecorator.setFolderItem(this);

        folderItemDecorator.asWidget().ensureDebugId(getDebugId().getDebugIdString());

        //TODO
        //addValueChangeHandler(folderItemDecorator);

        asWidget().setWidget(folderItemDecorator);

        folderItemDecorator.setFolderItem(this);

        //TODO use components inheritance
        if (this.getDebugId() != null) {
            folderItemDecorator.asWidget().ensureDebugId(this.getDebugId().getDebugIdString() + "_fd_");
        }
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isFirst() {
        return first;
    }

    public IDebugId getRowDebugId() {
        return rowDebugId;
    }

    public void setRowDebugId(int currentRowDebug) {
        rowDebugId = new StringDebugId(currentRowDebug);
    }

    @Override
    public IDebugId getDebugId() {
        return new CompositeDebugId(super.getDebugId(), rowDebugId);
    }

    public SimplePanel getContent() {
        return content;
    }
}
