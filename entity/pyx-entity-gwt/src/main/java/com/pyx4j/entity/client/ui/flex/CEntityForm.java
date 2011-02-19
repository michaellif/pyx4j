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
 * Created on 2011-02-09
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.flex;

import com.google.gwt.user.client.rpc.AsyncCallback;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.client.ui.BaseEditableComponentFactory;
import com.pyx4j.entity.client.ui.EditableComponentFactory;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.rpc.client.DefaultAsyncCallback;

/**
 * This component represents root IEntity of the shown object tree
 */
public abstract class CEntityForm<E extends IEntity> extends CEntityEditableComponent<E> {

    private final EditableComponentFactory factory;

    public CEntityForm(Class<E> rootClass) {
        super(new EntityFormBinder<E>(rootClass));
        factory = new EntityFormComponentFactory();
        createContent();
    }

    public CComponent<?> create(IObject<?> member, CEntityEditableComponent<?> parent) {
        CComponent<?> comp = null;
        comp = factory.create(member);
        if (comp instanceof CEditableComponent) {
            parent.bind((CEditableComponent<?, ?>) comp, member);
        }
        return comp;
    }

    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        throw new Error("No MemberFolderEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        throw new Error("No MemberEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

    @Override
    public void populate(E value) {
        if (value == null) {

            @SuppressWarnings("unchecked")
            E newEntity = (E) EntityFactory.create(proto().getValueClass());

            createNewEntity(newEntity, new DefaultAsyncCallback<E>() {

                @Override
                public void onSuccess(E result) {
                    CEntityForm.super.populate(result);
                }

            });
        } else {
            super.populate(value);
        }
    }

    /**
     * Implementation to override new Entity creation. No need to call
     * super.createNewEntity().
     * 
     * @param newEntity
     * @param callback
     */
    protected void createNewEntity(E newEntity, AsyncCallback<E> callback) {
        callback.onSuccess(newEntity);
    }

    class EntityFormComponentFactory extends BaseEditableComponentFactory {

        @Override
        public CEditableComponent<?, ?> create(IObject<?> member) {
            MemberMeta mm = member.getMeta();
            CEditableComponent<?, ?> comp = null;
            if (mm.isOwnedRelationships() && mm.getObjectClassType() == ObjectClassType.EntityList) {
                comp = createMemberFolderEditor(member);
                ((CEntityFolder<?>) comp).createContent();
            } else if (mm.isOwnedRelationships() && mm.isEntity()) {
                comp = createMemberEditor(member);
                ((CEntityEditableComponent<?>) comp).createContent();
            } else if (mm.getObjectClassType() == ObjectClassType.EntityList && EditorType.entityselector.equals(mm.getEditorType())) {
                comp = createMemberFolderEditor(member);
                ((CEntityFolder<?>) comp).createContent();
            } else {
                comp = super.create(member);
            }
            return comp;
        }

    }

}
