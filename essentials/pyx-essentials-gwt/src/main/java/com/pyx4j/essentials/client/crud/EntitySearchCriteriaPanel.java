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
 * Created on Feb 18, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.ui.crud.AbstractEntitySearchCriteriaPanel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;

public abstract class EntitySearchCriteriaPanel<E extends IEntity> extends AbstractEntitySearchCriteriaPanel<E> {

    public EntityListWithCriteriaWidget<E> listWidget;

    public EntitySearchCriteriaPanel(Class<E> clazz) {
        super(clazz);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        Widget searchForm = CForm.createDecoratedFormWidget(LabelAlignment.TOP, getComponents(), "Search");
        contentPanel.add(searchForm);

        enhanceComponents();

        form.populate(null);

        HorizontalPanel actionsPanel = new HorizontalPanel();
        contentPanel.add(actionsPanel);

        Button viewButton = new Button("View");
        viewButton.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                listWidget.submitSearchCriteria();
            }

        });
        viewButton.getElement().getStyle().setProperty("margin", "3px 20px 3px 8px");
        actionsPanel.add(viewButton);

        Anchor clearAction = new Anchor("<i>clear</i>", true);

        clearAction.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                form.populate(null);
            }

        });

        actionsPanel.add(clearAction);
        actionsPanel.setCellVerticalAlignment(clearAction, HasVerticalAlignment.ALIGN_MIDDLE);

        this.addKeyDownHandler(new KeyDownHandler() {

            @Override
            public void onKeyDown(KeyDownEvent event) {
                if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
                    listWidget.show(0);
                }
            }
        });

    }

    public void setListWidget(EntityListWithCriteriaWidget<E> listWidget) {
        this.listWidget = listWidget;
    }

    protected abstract CComponent<?>[][] getComponents();

    protected void enhanceComponents() {

    }

    public HandlerRegistration addKeyDownHandler(KeyDownHandler handler) {
        return addDomHandler(handler, KeyDownEvent.getType());
    }

}
