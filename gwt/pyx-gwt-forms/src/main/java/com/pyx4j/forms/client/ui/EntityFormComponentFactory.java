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
 * Created on Feb 25, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.meta.MemberMeta;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;

public class EntityFormComponentFactory extends BaseEditableComponentFactory {

    public EntityFormComponentFactory() {
    }

    @Override
    public CComponent<?> create(IObject<?> member) {
        MemberMeta mm = member.getMeta();
        CComponent<?> comp = null;
        if (mm.isOwnedRelationships() && mm.getObjectClassType() == ObjectClassType.EntityList) {
            comp = createMemberFolderEditor(member);
        } else if (mm.getObjectClassType() == ObjectClassType.EntityList && EditorType.entityselector.equals(mm.getEditorType())) {
            comp = createMemberFolderEditor(member);
        } else {
            comp = super.create(member);
        }
        return comp;
    }

    protected CEntityFolder<?> createMemberFolderEditor(IObject<?> member) {
        throw new Error("No MemberFolderEditor for member " + member.getMeta().getCaption() + " of class " + member.getValueClass());
    }

}