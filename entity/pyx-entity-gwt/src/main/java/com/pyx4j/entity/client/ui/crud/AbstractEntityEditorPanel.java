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
package com.pyx4j.entity.client.ui.crud;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.entity.client.DomainManager;
import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.client.ui.IEditableComponentFactory;
import com.pyx4j.entity.client.ui.flex.EntityBinder;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.forms.client.ui.CCaption;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.rpc.shared.VoidSerializable;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.event.shared.PageLeavingHandler;

public abstract class AbstractEntityEditorPanel<E extends IEntity> extends SimplePanel implements PageLeavingHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractEntityEditorPanel.class);

    private static I18n i18n = I18n.get(AbstractEntityEditorPanel.class);

    private final DelegatingEntityFormFactory<E> formFactory;

    private final CEntityForm<E> form;

    private final Class<E> entityClass;

    private boolean requresFocus;

    private class DelegatingEntityFormFactory<T extends IEntity> extends EntityFormFactory<T> {

        public DelegatingEntityFormFactory(Class<T> entityClass, IEditableComponentFactory editableComponentFactory) {
            super(entityClass, editableComponentFactory);
        }

        @Override
        protected IObject<?>[][] getFormMembers() {
            return AbstractEntityEditorPanel.this.getFormMembers();
        }

        @Override
        protected CEditableComponent<?, ?> createComponent(IObject<?> member) {
            return AbstractEntityEditorPanel.this.createComponent(member);
        }

        @Override
        protected CComponent<?> createDecoration(Decorator decorator) {
            return AbstractEntityEditorPanel.this.createDecoration(decorator);
        }

        protected CEditableComponent<?, ?> defaultCreateComponent(IObject<?> member) {
            return super.createComponent(member);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void enhanceComponents(CEntityForm<T> form) {
            AbstractEntityEditorPanel.this.enhanceComponents((CEntityForm<E>) form);
        }
    }

    public AbstractEntityEditorPanel(Class<E> entityClass) {
        this(entityClass, null);
    }

    public AbstractEntityEditorPanel(Class<E> entityClass, IEditableComponentFactory editableComponentFactory) {
        super();
        this.entityClass = entityClass;
        formFactory = new DelegatingEntityFormFactory<E>(entityClass, editableComponentFactory);
        form = formFactory.createForm();
        setStyleName(EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        requresFocus = true;
        this.setWidth("100%");
    }

    protected abstract IObject<?>[][] getFormMembers();

    protected CEditableComponent<?, ?> createComponent(IObject<?> member) {
        return formFactory.defaultCreateComponent(member);
    }

    protected void enhanceComponents(CEntityForm<E> form) {

    }

    protected CComponent<?> createDecoration(Decorator decorator) {
        if (decorator instanceof DecoratorLabel) {
            return new CCaption(((DecoratorLabel) decorator).getLabel());
        } else {
            return null;
        }
    }

    public E proto() {
        return formFactory.proto();
    }

    public CEntityForm<E> getForm() {
        return form;
    }

    public void populateForm(E entity) {
        form.populate(entity);
        setFocusOnFirstComponent();
    }

    @Override
    protected void onUnload() {
        super.onUnload();
        requresFocus = true;
    }

    private void setFocusOnFirstComponent() {
        if (!requresFocus) {
            return;
        }
        requresFocus = false;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                Collection<CComponent<?>> components = form.getComponents();
                for (CComponent<?> cComponent : components) {
                    if (cComponent instanceof CEditableComponent && cComponent.isVisible() && cComponent.isEnabled()) {
                        ((CEditableComponent) cComponent).setFocus(true);
                        break;
                    }
                }
            }
        });
    }

    public void clearForm() {
        form.populate(null);
    }

    public E getEntity() {
        return form.getValue();
    }

    protected E getEntityForSave() {
        return getEntity();
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public <T extends IEntity> CEditableComponent<T, ?> get(T member) {
        return form.get(member);
    }

    public <T> CEditableComponent<T, ?> get(IObject<T> member) {
        return form.get(member);
    }

    public Widget createFormWidget(LabelAlignment allignment) {
        form.setAllignment(allignment);
        return form.asWidget();
    }

    /**
     * @return true when any field in Entity has been changes.
     */
    public boolean isChanged() {
        return !EntityBinder.equalRecursive(form.getOrigValue(), getEntityForSave());
    }

    public static boolean equalRecursive(IEntity entity1, IEntity entity2) {
        return EntityBinder.equalRecursive(entity1, entity2);
    }

    //TODO move to EntityEditorWidget
    @Override
    public void onPageLeaving(PageLeavingEvent event) {
        if (isChanged()) {
            String entityName = getEntity().getStringView();
            if (CommonsStringUtils.isEmpty(entityName)) {
                event.addMessage(i18n.tr("Changes to {0} were not saved", proto().getEntityMeta().getCaption()));
            } else {
                event.addMessage(i18n.tr("Changes to {0} ''{1}'' were not saved", proto().getEntityMeta().getCaption(), entityName));
            }
        }
    }

    protected Class<? extends EntityServices.Save> getSaveService() {
        return EntityServices.Save.class;
    }

    protected Class<? extends EntityServices.Delete> getDeleteService() {
        return null;
    }

    protected void onBeforeSave() {
    }

    protected void onAfterSave() {
    }

    protected void onAfterDelete() {
    }

    protected void populateSaved(E entity) {
        populateForm(entity);
    }

    protected void onSaveFailure(Throwable caught) {
        throw new UnrecoverableClientError(caught);
    }

    @SuppressWarnings("unchecked")
    public void doSave() {
        ValidationResults validationResults = form.getValidationResults();
        if (!validationResults.isValid()) {
            MessageDialog.warn(i18n.tr("Validation failed"), validationResults.getMessagesText(false));
            return;
        }
        onBeforeSave();
        E entityForSave = getEntityForSave();
        log.debug("saving {}", entityForSave);
        final AsyncCallback handlingCallback = new BlockingAsyncCallback<E>() {

            @Override
            public void onFailure(Throwable caught) {
                onSaveFailure(caught);
            }

            @Override
            public void onSuccess(E result) {
                DomainManager.entityUpdated(result);
                populateSaved(result);
                onAfterSave();
            }

        };
        RPCManager.execute(getSaveService(), entityForSave, handlingCallback);
    }

    @SuppressWarnings("unchecked")
    public void doDelete() {
        final E entity = getEntity();
        log.debug("removing {}", entity);
        final AsyncCallback handlingCallback = new BlockingAsyncCallback<VoidSerializable>() {

            @Override
            public void onFailure(Throwable caught) {
                onSaveFailure(caught);
            }

            @Override
            public void onSuccess(VoidSerializable result) {
                DomainManager.entityDeleted(entity);
                populateForm(null);
                onAfterDelete();
            }

        };
        RPCManager.execute(getDeleteService(), entity, handlingCallback);
    }

}
