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

import com.google.gwt.user.client.ui.VerticalPanel;

import com.pyx4j.entity.client.ui.BaseEditableComponentFactory;
import com.pyx4j.entity.client.ui.EntityFormFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CEditableComponent;

public class FlexEditableComponentFactory<E extends IEntity> extends BaseEditableComponentFactory {

    private final Class<E> rootClass;

    private final CEntityEditableComponent<E> entityEditor;

    private final EntityChangeManager<E> changeManager;

    public FlexEditableComponentFactory(Class<E> rootClass) {
        this.rootClass = rootClass;
        entityEditor = new CEntityEditableComponent<E>(rootClass, this) {

            @Override
            public void createLayout() {
                createEntityLayout();
            }
        };
        changeManager = new EntityChangeManager<E>(rootClass);
        entityEditor.createLayout();
    }

    @Override
    public CEditableComponent<?, ?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.getObjectClassType() == ObjectClassType.EntityList) {
            CEntityEditableComponent<?> comp = createMemberListEditor(member);
            comp.createLayout();
            return comp;
        } else if (mm.isOwnedRelationships() && mm.getObjectClassType() == ObjectClassType.Entity) {
            CEntityEditableComponent<?> comp = createMemberEditor(member);
            comp.createLayout();
            return comp;
        } else {
            return super.create(member);
        }

    }

    protected CEntityEditableComponent<?> createMemberListEditor(IObject<?> member) {
        throw new Error("No EntityListEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        throw new Error("No EntityEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

    public CEntityEditableComponent<E> getEntityEditor() {
        return entityEditor;
    }

    public void createEntityLayout() {
    }

    protected E proto() {
        return entityEditor.proto();
    }

    protected void setWidget(VerticalPanel main) {
        entityEditor.setWidget(main);
    }

    public void populate(E entity) {
        changeManager.populate(entity);
        entityEditor.populate(changeManager.getValue());
    }

    @Override
    @Deprecated
    protected EntityFormFactory<? extends IEntity> createEntityFormFactory(IObject<?> member) {
        throw new Error("EntityFormFactory should not be used");
    }
}
