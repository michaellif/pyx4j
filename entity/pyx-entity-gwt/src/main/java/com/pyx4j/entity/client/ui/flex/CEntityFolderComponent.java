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
 * Created on Feb 11, 2011
 * @author Misha
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.forms.client.ui.CEditableComponent;

public abstract class CEntityFolderComponent<E extends IEntity> extends CEditableComponent<IList<E>, NativeEntityFolder<IList<E>>> {

    private FolderDecorator folderDecorator;

    private FlowPanel content;

    public CEntityFolderComponent() {
    }

    public abstract void createContent();

    protected abstract CEntityEditableComponent<E> createItem();

    public void setFolderDecorator(FolderDecorator folderDecorator) {
        this.folderDecorator = folderDecorator;
        asWidget().setWidget(folderDecorator);

        content = new FlowPanel();
        folderDecorator.setWidget(content);
    }

    @Override
    public void populate(IList<E> value) {
        super.populate(value);

        //TODO reuse existing  CEntityEditableComponent.  See  CEntityFormFolder

        content.clear();
        for (E item : value) {
            CEntityEditableComponent<E> comp = createItem();
            comp.createContent();
            content.add(comp);
            comp.populate(item);

        }
    }

    @Override
    protected NativeEntityFolder initWidget() {
        return new NativeEntityFolder();
    }

    public void setWidget(Widget widget) {
        folderDecorator.setWidget(widget);
    }

}
