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

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.event.shared.PageLeavingHandler;

public abstract class AbstractEntityEditorPanel<E extends IEntity> extends SimplePanel implements PageLeavingHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractEntityEditorPanel.class);

    private final DelegatingEntityFormFactory<E> formFactory;

    private final CEntityForm<E> form;

    private final Class<E> entityClass;

    private class DelegatingEntityFormFactory<T extends IEntity> extends EntityFormFactory<T> {

        public DelegatingEntityFormFactory(Class<T> entityClass) {
            super(entityClass);
        }

        @Override
        protected IObject<?>[][] getFormMembers() {
            return AbstractEntityEditorPanel.this.getFormMembers();
        }

        @Override
        protected CEditableComponent<?> createComponent(IObject<?> member) {
            return AbstractEntityEditorPanel.this.createComponent(member);
        }

        protected CEditableComponent<?> defaultCreateComponent(IObject<?> member) {
            return super.createComponent(member);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void enhanceComponents(CEntityForm<T> form) {
            AbstractEntityEditorPanel.this.enhanceComponents((CEntityForm<E>) form);
        }
    }

    public AbstractEntityEditorPanel(Class<E> entityClass) {
        super();
        this.entityClass = entityClass;
        formFactory = new DelegatingEntityFormFactory<E>(entityClass);
        form = formFactory.createForm();
        setStyleName(EntityCSSClass.pyx4j_Entity_EntityEditor.name());
    }

    protected abstract IObject<?>[][] getFormMembers();

    protected CEditableComponent<?> createComponent(IObject<?> member) {
        return formFactory.defaultCreateComponent(member);
    }

    protected void enhanceComponents(CEntityForm<E> form) {

    }

    public E meta() {
        return formFactory.meta();
    }

    public CEntityForm<E> getForm() {
        return form;
    }

    public void populateForm(E entity) {
        form.populate(entity);
    }

    public void clearForm() {
        form.populate(null);
    }

    public E getEntity() {
        return form.getValue();
    }

    public Class<E> getEntityClass() {
        return entityClass;
    }

    public <T extends IEntity> CEditableComponent<T> get(T member) {
        return form.get(member);
    }

    public <T> CEditableComponent<T> get(IObject<T> member) {
        return form.get(member);
    }

    public Widget createFormWidget(LabelAlignment allignment) {
        form.setAllignment(allignment);
        return (Widget) form.initNativeComponent();
    }

    /**
     * @return true when any filed in Entity has been changes.
     */
    public boolean isChanged() {
        return !equalRecursive(form.getOrigValue(), getEntity(), new HashSet<IEntity>());
    }

    private static boolean equalRecursive(IEntity entity1, IEntity entity2, Set<IEntity> processed) {
        if (((entity2 == null) || entity2.isNull())) {
            return (entity1 == null) || entity1.isNull();
        } else if ((entity1 == null) || entity1.isNull()) {
            return false;
        }
        if (processed.contains(entity1)) {
            return true;
        }
        processed.add(entity1);
        EntityMeta em = entity1.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isDetached() || memberMeta.isTransient() || memberMeta.isRpcTransient()) {
                continue;
            }
            if (memberMeta.isEntity()) {
                if (memberMeta.isEmbedded()) {
                    if (!equalRecursive((IEntity) entity1.getMember(memberName), (IEntity) entity2.getMember(memberName), processed)) {
                        log.debug("changed {}", memberName);
                        return false;
                    }
                } else if (((IEntity) entity1.getMember(memberName)).isNull()) {
                    if (!((IEntity) entity2.getMember(memberName)).isNull()) {
                        log.debug("changed [null] -> [{}]", entity2.getMember(memberName));
                        return false;
                    }
                } else if (!EqualsHelper.equals(entity1.getMember(memberName), entity2.getMember(memberName))) {
                    log.debug("changed [{}] -> [{}]", entity1.getMember(memberName), entity2.getMember(memberName));
                    return false;
                }
            } else if (ISet.class.equals(memberMeta.getObjectClass())) {
                if (!EqualsHelper.equals((ISet<?>) entity1.getMember(memberName), (ISet<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                if (!EqualsHelper.equals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (!EqualsHelper.equals(entity1.getMember(memberName), entity2.getMember(memberName))) {
                log.debug("changed {}", memberName);
                log.debug("[{}] -> [{}]", entity1.getMember(memberName), entity2.getMember(memberName));
                return false;
            }
        }
        return true;
    }

    //TODO move to EntityEditorWidget
    public void onPageLeaving(PageLeavingEvent event) {
        if (isChanged()) {
            event.addMessage(meta().getEntityMeta().getCaption() + " " + getEntity().getStringView() + " wasn't saved");
        }
    }

    protected Class<? extends EntityServices.Save> getSaveService() {
        return EntityServices.Save.class;
    }

    protected void onBeforeSave() {
        // TODO validations goes here.
    }

    protected void onAfterSave() {
        // TODO validations goes here.
    }

    @SuppressWarnings("unchecked")
    protected void doSave() {
        onBeforeSave();
        final AsyncCallback handlingCallback = new BlockingAsyncCallback<E>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException(caught);
            }

            @Override
            public void onSuccess(E result) {
                populateForm(result);
                onAfterSave();
            }

        };
        RPCManager.execute(getSaveService(), getEntity(), handlingCallback);
    }
}
