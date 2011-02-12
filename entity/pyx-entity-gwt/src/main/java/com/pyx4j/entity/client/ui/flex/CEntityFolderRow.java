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
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import java.util.List;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;

public class CEntityFolderRow<E extends IEntity> extends CEntityEditableComponent<E> {

    private final List<EntityFolderColumnDescriptor> columns;

    private final CEntityForm<?> form;

    private final ImageResource removeButton;

    public CEntityFolderRow(Class<E> clazz, List<EntityFolderColumnDescriptor> columns, CEntityForm<?> form, ImageResource removeButton) {
        super(clazz);
        this.columns = columns;
        this.form = form;
        this.removeButton = removeButton;
    }

    @Override
    public void createContent() {

        FlowPanel main = new FlowPanel();
        main.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
        main.setWidth("100%");
        Image image = new Image(removeButton);
        image.asWidget().getElement().getStyle().setFloat(Float.LEFT);
        main.add(image);
        for (EntityFolderColumnDescriptor column : columns) {
            CComponent<?> component = form.create(column.getObject(), this);
            component.setWidth(column.getWidth());
            component.asWidget().getElement().getStyle().setFloat(Float.LEFT);
            main.add(component);
        }
        setWidget(main);
    }

}
