/*
 * Pyx4j framework
 * Copyright (C) 2008-2011 pyx4j.com.
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
 * Created on Mar 5, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.folder;

import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.shared.IEntity;

public abstract class BaseFolderItemDecorator<E extends IEntity> extends SimplePanel implements IFolderItemDecorator<E> {

    private final EntityFolderImages images;

    private CEntityFolderItem<E> folderItem;

    public BaseFolderItemDecorator(EntityFolderImages images) {
        this.images = images;
    }

    @Override
    public void setComponent(final CEntityFolderItem<E> folderItem) {
        this.folderItem = folderItem;
    }

    public CEntityFolderItem<E> getFolderItem() {
        return folderItem;
    }

    public EntityFolderImages getImages() {
        return images;
    }

}
