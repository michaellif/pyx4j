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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import com.pyx4j.commons.CommonsStringUtils;
import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.client.DomainManager;
import com.pyx4j.entity.client.EntityCSSClass;
import com.pyx4j.entity.client.ui.CEntityForm;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.rpc.EntityServices;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CCaption;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CLabel;
import com.pyx4j.forms.client.ui.CForm.LabelAlignment;
import com.pyx4j.forms.client.ui.ValidationResults;
import com.pyx4j.gwt.commons.UnrecoverableClientError;
import com.pyx4j.rpc.client.BlockingAsyncCallback;
import com.pyx4j.rpc.client.RPCManager;
import com.pyx4j.widgets.client.dialog.MessageDialog;
import com.pyx4j.widgets.client.event.shared.PageLeavingEvent;
import com.pyx4j.widgets.client.event.shared.PageLeavingHandler;

public abstract class AbstractEntityEditorPanel<E extends IEntity> extends SimplePanel implements PageLeavingHandler {

    private static final Logger log = LoggerFactory.getLogger(AbstractEntityEditorPanel.class);

    private static I18n i18n = I18nFactory.getI18n(AbstractEntityEditorPanel.class);

    private final DelegatingEntityFormFactory<E> formFactory;

    private final CEntityForm<E> form;

    private final Class<E> entityClass;

    private boolean requresFocus;

    private class DelegatingEntityFormFactory<T extends IEntity> extends EntityFormFactory<T> {

        public DelegatingEntityFormFactory(Class<T> entityClass) {
            super(entityClass);
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
        super();
        this.entityClass = entityClass;
        formFactory = new DelegatingEntityFormFactory<E>(entityClass);
        form = formFactory.createForm();
        setStyleName(EntityCSSClass.pyx4j_Entity_EntityEditor.name());
        requresFocus = true;

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

    public E meta() {
        return formFactory.meta();
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
     * @return true when any filed in Entity has been changes.
     */
    public boolean isChanged() {
        return !equalRecursive(form.getOrigValue(), getEntityForSave(), new HashSet<IEntity>());
    }

    public static boolean equalRecursive(IEntity entity1, IEntity entity2) {
        return equalRecursive(entity1, entity2, new HashSet<IEntity>());
    }

    private static boolean equalRecursive(IEntity entity1, IEntity entity2, Set<IEntity> processed) {
        if (((entity2 == null) || entity2.isNull())) {
            return isEmptyEntity(entity1);
        } else if ((entity1 == null) || entity1.isNull()) {
            return isEmptyEntity(entity2);
        }
        if (processed != null) {
            if (processed.contains(entity1)) {
                return true;
            }
            processed.add(entity1);
        }
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
                //TODO OwnedRelationships
                if (!EqualsHelper.equals((ISet<?>) entity1.getMember(memberName), (ISet<?>) entity2.getMember(memberName))) {
                    log.debug("changed {}", memberName);
                    return false;
                }
            } else if (IList.class.equals(memberMeta.getObjectClass())) {
                if (memberMeta.isOwnedRelationships()) {
                    if (!listValuesEquals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName), processed)) {
                        log.debug("changed {}", memberName);
                        return false;
                    }
                } else if (!EqualsHelper.equals((IList<?>) entity1.getMember(memberName), (IList<?>) entity2.getMember(memberName))) {
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

    private static boolean listValuesEquals(IList<?> value1, IList<?> value2, Set<IEntity> processed) {
        if (value1.size() != value2.size()) {
            return false;
        }
        Iterator<?> iter1 = value1.iterator();
        Iterator<?> iter2 = value2.iterator();
        for (; iter1.hasNext() && iter2.hasNext();) {
            if (!equalRecursive((IEntity) iter1.next(), (IEntity) iter2.next(), processed)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyEntity(IEntity entity) {
        if ((entity == null) || entity.isNull()) {
            return true;
        }
        EntityMeta em = entity.getEntityMeta();
        for (String memberName : em.getMemberNames()) {
            MemberMeta memberMeta = em.getMemberMeta(memberName);
            if (memberMeta.isDetached() || memberMeta.isTransient() || memberMeta.isRpcTransient()) {
                continue;
            }
            IObject<?> member = entity.getMember(memberName);
            if (member.isNull()) {
                continue;
            } else if (memberMeta.isEntity()) {
                if (!isEmptyEntity((IEntity) member)) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else if ((ISet.class.equals(memberMeta.getObjectClass())) || (IList.class.equals(memberMeta.getObjectClass()))) {
                if (!((ICollection<?, ?>) member).isEmpty()) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else if (Boolean.class.equals(memberMeta.getValueClass())) {
                // Special case for values presented by CheckBox
                if (member.getValue() == Boolean.TRUE) {
                    log.debug("member {} not empty; {}", memberName, member);
                    return false;
                }
            } else {
                log.debug("member {} not empty; {}", memberName, member);
                return false;
            }
        }
        return true;
    }

    //TODO move to EntityEditorWidget
    @Override
    public void onPageLeaving(PageLeavingEvent event) {
        if (isChanged()) {
            String entityName = getEntity().getStringView();
            if (CommonsStringUtils.isEmpty(entityName)) {
                event.addMessage(i18n.tr("Changes to {0} were not saved", meta().getEntityMeta().getCaption()));
            } else {
                event.addMessage(i18n.tr("Changes to {0} ''{1}'' were not saved", meta().getEntityMeta().getCaption(), entityName));
            }
        }
    }

    protected Class<? extends EntityServices.Save> getSaveService() {
        return EntityServices.Save.class;
    }

    protected void onBeforeSave() {
    }

    protected void onAfterSave() {
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
            MessageDialog.warn(i18n.tr("Validation failed."), validationResults.getMessagesText(false));
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

}
