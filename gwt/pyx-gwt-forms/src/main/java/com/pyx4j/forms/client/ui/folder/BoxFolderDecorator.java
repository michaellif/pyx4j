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
 */
package com.pyx4j.forms.client.ui.folder;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.pyx4j.gwt.commons.ui.SimplePanel;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.forms.client.images.FolderImages;

public class BoxFolderDecorator<E extends IEntity> extends BaseFolderDecorator<E> {

    public BoxFolderDecorator(FolderImages images) {
        this(images, null, false);
    }

    public BoxFolderDecorator(FolderImages images, String title) {
        this(images, title, true);
    }

    public BoxFolderDecorator(FolderImages images, String title, boolean addable) {
        super(images, title, addable);

        asWidget().setStyleName(FolderTheme.StyleName.CFolderBoxDecorator.name());

        add(getMessagePannel());

        add(getContentPanel());

        SimplePanel addButtonHolder = new SimplePanel(getAddButton());
        addButtonHolder.setStyleName(FolderTheme.StyleName.CFolderBoxDecoratorAddButtonHolder.name());

        add(addButtonHolder);

        setAddButtonVisible(addable);

    }

    @Override
    public void onValueChange(ValueChangeEvent<IList<E>> event) {
        // TODO Auto-generated method stub
    }

}
