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
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.ui.datatable;

import java.util.Date;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class ColumnDescriptorFactory {

    public static <T extends IEntity<IEntity<?>>> ColumnDescriptor<T> createColumnDescriptor(IObject<?, T> member) {
        MemberMeta mm = member.getMeta();
        if (mm.isEntity()) {
            return new MemberEntityColumnDescriptor<T>(mm.getFieldName(), mm.getCaption());
        } else if (mm.getValueClass().equals(Date.class)) {
            return new MemberDateColumnDescriptor<T>(mm.getFieldName(), mm.getCaption(), mm.getFormat());
        } else if ((member instanceof ISet<?>) || (member instanceof IList<?>)) {
            return new MemberEntityCollectionColumnDescriptor<T>(mm.getFieldName(), mm.getCaption());
        } else if (member instanceof IPrimitiveSet<?>) {
            return new MemberCollectionColumnDescriptor<T>(mm.getFieldName(), mm.getCaption(), mm.getFormat());
        } else {
            return new MemberColumnDescriptor<T>(mm.getFieldName(), mm.getCaption(), mm.getFormat());
        }
    }
}
