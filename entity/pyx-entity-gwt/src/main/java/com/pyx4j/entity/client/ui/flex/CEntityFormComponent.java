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

import com.pyx4j.entity.client.ui.BaseEditableComponentFactory;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.ObjectClassType;
import com.pyx4j.entity.shared.meta.MemberMeta;
import com.pyx4j.forms.client.ui.CEditableComponent;

public abstract class CEntityFormComponent<E extends IEntity> extends CEntityEditableComponent<E> {

    private final BaseEditableComponentFactory factory;

    public CEntityFormComponent(Class<E> rootClass) {
        super(new EntityFormBinder<E>(rootClass));
        factory = new BaseEditableComponentFactory();
        createLayout();
    }

    public CEditableComponent<?, ?> create(IObject<?> member, CEntityEditableComponent<?> parent) {
        MemberMeta mm = member.getMeta();
        CEditableComponent<?, ?> comp = null;
        if (mm.getObjectClassType() == ObjectClassType.EntityList) {
            comp = null;
            //comp = createMemberFolderEditor(member);
            ((CEntityEditableComponent<?>) comp).createLayout();
        } else if (mm.isOwnedRelationships() && mm.getObjectClassType() == ObjectClassType.Entity) {
            comp = createMemberEditor(member);
            ((CEntityEditableComponent<?>) comp).createLayout();
        } else {
            comp = factory.create(member);
        }
        parent.bind(comp, member);
        return comp;
    }

    protected CEntityFolderComponent<?> createMemberFolderEditor(IObject<?> member) {
        throw new Error("No MemberFolderEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

    protected CEntityEditableComponent<?> createMemberEditor(IObject<?> member) {
        throw new Error("No MemberEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

}
