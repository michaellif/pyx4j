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
 * Created on Feb 12, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex.folder;

import com.google.gwt.event.logical.shared.ValueChangeEvent;

import com.pyx4j.entity.client.images.EntityFolderImages;
import com.pyx4j.entity.client.ui.flex.folder.CEntityFolder.StyleName;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public class BoxFolderDecorator<E extends IEntity> extends BaseFolderDecorator<E> {

    public BoxFolderDecorator(EntityFolderImages images) {
        this(images, null, false);
    }

    public BoxFolderDecorator(EntityFolderImages images, String title) {
        this(images, title, true);
    }

    public BoxFolderDecorator(EntityFolderImages images, String title, boolean addable) {
        super(images, title, addable);

        asWidget().setStyleName(StyleName.EntityFolderBoxDecorator.name());

        add(getContainer());

        if (isAddable()) {
            add(getActionsPanel());
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        // TODO Auto-generated method stub
    }

}
