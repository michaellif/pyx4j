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
 * Created on Apr 24, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.essentials.client.crud;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.entity.client.DomainManager;
import com.pyx4j.entity.client.ui.crud.AbstractEntityEditorPanel;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CForm;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class EntityEditorPanel<E extends IEntity> extends AbstractEntityEditorPanel<E> {

    private final CForm form;

    private EntityEditorWidget<E> widget;

    protected final Button saveButton;

    public EntityEditorPanel(Class<E> clazz) {
        super(clazz);

        populateForm(null);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        form = createForm(LabelAlignment.LEFT, getComponents());

        contentPanel.add(((Widget) form.initNativeComponent()));

        enhanceComponents();

        if (getSaveService() != null) {
            saveButton = new Button("Save");
            saveButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    widget.setMessage(null);
                    ValidationResults validationResults = form.getValidationResults();
                    if (validationResults.isValid()) {
                        doSave();
                    } else {
                        MessageDialog.warn("Validation failed.", validationResults.getMessagesText());
                    }
                }
            });
            saveButton.getElement().getStyle().setMargin(20, Unit.PX);

            contentPanel.add(saveButton);
        } else {
            saveButton = null;
        }

    }

    public void setParentWidget(EntityEditorWidget<E> widget) {
        this.widget = widget;
    }

    protected abstract IObject<?>[][] getComponents();

    protected void enhanceComponents() {

    }

    public CForm getCForm() {
        return form;
    }

    public String toStringForPrint() {
        return form.getNativeComponent().toStringForPrint();
    }

    @Override
    protected Class<? extends EntityServices.Save> getSaveService() {
        return EntityServices.Save.class;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doSave() {
        onBeforeSave();
        final AsyncCallback handlingCallback = new BlockingAsyncCallback<E>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new UnrecoverableClientError(caught);
            }

            @Override
            public void onSuccess(E result) {
                DomainManager.entityUpdated(result);
                widget.populateForm(result);
                widget.setMessage("Entity is saved.");
            }

        };
        RPCManager.execute(getSaveService(), getEntity(), handlingCallback);
    }

}