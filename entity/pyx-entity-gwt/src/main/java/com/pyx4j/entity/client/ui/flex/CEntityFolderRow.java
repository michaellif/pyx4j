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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CCheckBox;
import com.pyx4j.forms.client.ui.CComponent;

public abstract class CEntityFolderRow<E extends IEntity> extends CEntityFolderItem<E> {

    protected final List<EntityFolderColumnDescriptor> columns;

    public CEntityFolderRow(Class<E> clazz, List<EntityFolderColumnDescriptor> columns) {
        super(clazz);
        this.columns = columns;
    }

    @Override
    public IsWidget createContent() {
        HorizontalPanel main = new HorizontalPanel();
        for (EntityFolderColumnDescriptor column : columns) {
            CComponent<?> component = createCell(column);
            component.setWidth("100%");
            main.add(createDecorator(component, column.getWidth()));
        }
        return main;
    }

    protected SimplePanel createDecorator(CComponent<?> component, String width) {
        SimplePanel wrapper = new SimplePanel();
        wrapper.getElement().getStyle().setPaddingLeft(3, Unit.PX);
        wrapper.getElement().getStyle().setPaddingRight(3, Unit.PX);
        wrapper.setWidth(width);
        wrapper.setWidget(component);
        return wrapper;
    }

    protected CComponent<?> createCell(EntityFolderColumnDescriptor column) {
        CComponent<?> comp = inject(column.getObject());

        //Special TableFolder customisation
        if (comp instanceof CCheckBox) {
            ((CCheckBox) comp).setAlignmet(CCheckBox.Alignment.center);
        }

        return comp;
    }

}
