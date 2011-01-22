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
 * Created on May 26, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.entity.client.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.client.ui.crud.Decorator;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.ui.CTextComponent;
import com.pyx4j.forms.client.ui.FormFactory;

public abstract class EntityFormFactory<E extends IEntity> implements FormFactory {

    private static final Logger log = LoggerFactory.getLogger(EntityFormFactory.class);

    protected final E metaEntity;

    private final EditableComponentFactory editableComponentFactory;

    public EntityFormFactory(Class<E> entityClass) {
        this(entityClass, new BaseEditableComponentFactory());
    }

    public EntityFormFactory(Class<E> entityClass, EditableComponentFactory editableComponentFactory) {
        metaEntity = EntityFactory.create(entityClass);
        this.editableComponentFactory = editableComponentFactory;
    }

    public E meta() {
        return metaEntity;
    }

    protected abstract IObject<?>[][] getFormMembers();

    protected CEditableComponent<?, ?> createComponent(IObject<?> member) {
        return editableComponentFactory.create(member);
    }

    protected CComponent<?> createDecoration(Decorator decorator) {
        return null;
    }

    protected void enhanceComponents(CEntityForm<E> form) {

    }

    @Override
    public CEntityForm<E> createForm() {
        log.debug("createFormInstance of {}", metaEntity.getObjectClass());
        CEntityForm<E> form = createFormInstance();
        IObject<?>[][] members = getFormMembers();
        CComponent<?>[][] components = new CComponent<?>[members.length][members[0].length];

        for (int i = 0; i < components.length; i++) {
            for (int j = 0; j < components[0].length; j++) {
                IObject<?> member = members[i][j];
                if (member == null) {
                    components[i][j] = null;
                } else if (form.contains(member)) {
                    components[i][j] = form.get(member);
                } else if (member instanceof Decorator) {
                    components[i][j] = createDecoration((Decorator) member);
                } else {
                    components[i][j] = createComponent(member);
                    MemberMeta mm = member.getMeta();
                    if (components[i][j] instanceof CEditableComponent && mm.isValidatorAnnotationPresent(NotNull.class)) {
                        ((CEditableComponent) components[i][j]).setMandatory(true);
                    }
                    if (components[i][j] instanceof CTextComponent && String.class == mm.getValueClass()) {
                        ((CTextComponent) components[i][j]).setMaxLength(mm.getStringLength());
                        if (mm.getDescription() != null) {
                            ((CTextComponent) components[i][j]).setWatermark(mm.getWatermark());
                        }
                    }

                    if (mm.getDescription() != null) {
                        components[i][j].setToolTip(mm.getDescription());
                    }
                    components[i][j].setTitle(mm.getCaption());
                    form.bind((CEditableComponent<?, ?>) components[i][j], member.getPath());
                }
            }
        }
        form.setComponents(components);
        log.debug("enhanceComponents of {}", metaEntity.getObjectClass());
        enhanceComponents(form);
        return form;
    }

    @SuppressWarnings("unchecked")
    protected CEntityForm<E> createFormInstance() {
        return new CEntityForm(metaEntity.getObjectClass());
    }

}
