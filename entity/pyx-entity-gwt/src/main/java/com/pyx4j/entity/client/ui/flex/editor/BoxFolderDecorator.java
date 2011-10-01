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
package com.pyx4j.entity.client.ui.flex.editor;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.ImageResource;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;

public class BoxFolderDecorator<E extends IEntity> extends BaseFolderDecorator<E> {

    public BoxFolderDecorator(ImageResource addButton) {
        this(addButton, (ImageResource) null);
    }

    public BoxFolderDecorator(ImageResource addButton, ImageResource addButtonHover) {
        this(addButton, addButtonHover, null, false);
    }

    public BoxFolderDecorator(ImageResource addButton, String title) {
        this(addButton, null, title);
    }

    public BoxFolderDecorator(ImageResource addButton, ImageResource addButtonHover, String title) {
        this(addButton, addButtonHover, title, true);
    }

    public BoxFolderDecorator(ImageResource addButton, String title, boolean addable) {
        this(addButton, null, title, addable);
    }

    public BoxFolderDecorator(ImageResource addButton, ImageResource addButtonHover, String title, boolean addable) {
        super(addButton, addButtonHover, title, addable);

        add(getContainer());

        if (isAddable()) {
            add(getImageHolder());
        }
    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        // TODO Auto-generated method stub
    }
}
