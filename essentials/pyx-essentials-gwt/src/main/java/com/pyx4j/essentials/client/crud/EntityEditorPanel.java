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

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.crud.AbstractEntityEditorPanel;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.widgets.client.dialog.MessageDialog;

public abstract class EntityEditorPanel<E extends IEntity> extends AbstractEntityEditorPanel<E> {

    private static I18n i18n = I18nFactory.getI18n(EntityEditorPanel.class);

    private EntityEditorWidget<E> parentWidget;

    protected final Button saveButton;

    public EntityEditorPanel(Class<E> clazz) {
        super(clazz);

        populateForm(null);

        VerticalPanel contentPanel = new VerticalPanel();
        setWidget(contentPanel);

        contentPanel.add(createFormWidget(LabelAlignment.LEFT));

        if (getSaveService() != null) {
            saveButton = new Button(i18n.tr("Save"));
            saveButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    parentWidget.setMessage(null);
                    ValidationResults validationResults = getForm().getValidationResults();
                    if (validationResults.isValid()) {
                        doSave();
                    } else {
                        MessageDialog.warn(i18n.tr("Validation failed."), validationResults.getMessagesText(false));
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
        this.parentWidget = widget;
    }

    public String toStringForPrint() {
        return getForm().getNativeComponent().toStringForPrint();
    }

    @Override
    protected void populateSaved(E entity) {
        parentWidget.populateForm(entity);
    }

    @Override
    protected void onAfterSave() {
        parentWidget.setMessage(i18n.tr("{0} is saved.", meta().getEntityMeta().getCaption()));
    }
}