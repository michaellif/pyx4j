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
 * Created on Feb 16, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.crud;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.shared.IEntity;

public class EntityCrudWidget<E extends IEntity> extends SimplePanel {

    private final TabPanel cards;

    private final EntityListWidget<E> listWidget;

    private final IEntityEditorPanel<E> editorWidget;

    public EntityCrudWidget(EntityListWidget<E> listWidget, IEntityEditorPanel<E> editorWidget) {
        this.listWidget = listWidget;
        //listWidget.setCrudContainer(this);
        this.editorWidget = editorWidget;
        //editorWidget.setCrudContainer(this);

        cards = new TabPanel();

        cards.add(listWidget, "list");

        cards.add((Widget) editorWidget, "editor");

        setWidget(cards);
        cards.selectTab(0);

    }

}
