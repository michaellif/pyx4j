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

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IList;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.IPrimitiveSet;
import com.pyx4j.entity.shared.ISet;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class ColumnDescriptorFactory {

    public static <E extends IEntity> ColumnDescriptor<E> createColumnDescriptor(E meta, IObject<?> member) {
        MemberMeta mm = member.getMeta();
        if (mm.isEntity()) {
            return new MemberEntityColumnDescriptor<E>(member.getPath(), mm.getCaption(), mm.getFormat());
        } else if ((member instanceof ISet<?>) || (member instanceof IList<?>)) {
            return new MemberEntityCollectionColumnDescriptor<E>(member.getPath(), mm.getCaption(), mm.getFormat());
        } else if (member instanceof IPrimitiveSet<?>) {
            return new MemberCollectionColumnDescriptor<E>(member.getPath(), mm.getCaption(), mm.getFormat());
        } else if (member instanceof IPrimitive<?>) {
            return new MemberPrimitiveColumnDescriptor<E>(member.getPath(), mm.getCaption());
        } else {
            return new MemberColumnDescriptor<E>(member.getPath(), mm.getCaption(), mm.getFormat());
        }
    }

    public static <E extends IEntity> ColumnDescriptor<E> createColumnDescriptor(E meta, IObject<?> member, String width) {
        return createColumnDescriptor(meta, member, width, false);
    }

    public static <E extends IEntity> ColumnDescriptor<E> createColumnDescriptor(E meta, IObject<?> member, String width, boolean wordWrap) {
        ColumnDescriptor<E> cd = ColumnDescriptorFactory.createColumnDescriptor(meta, member);
        cd.setWidth(width);
        cd.setWordWrap(wordWrap);
        return cd;
    }
}
