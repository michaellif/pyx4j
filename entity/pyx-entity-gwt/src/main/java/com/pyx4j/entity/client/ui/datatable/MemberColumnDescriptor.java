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
import com.pyx4j.entity.shared.Path;
import com.pyx4j.entity.shared.meta.MemberMeta;

public class MemberColumnDescriptor<E extends IEntity> extends ColumnDescriptor<E> {

    protected MemberColumnDescriptor(Builder builder) {
        super(builder);
    }

    public Path getColumnPath() {
        return ((Builder) getBuilder()).member.getPath();
    }

    public String getFormatPattern() {
        return ((Builder) getBuilder()).member.getMeta().getFormat();
    }

    @Override
    public String convert(E entity) {
        Object value = entity.getMember(getColumnPath()).getValue();
        if (value == null) {
            return "";
        } else {
            return value.toString();
        }
    }

    public static class Builder extends ColumnDescriptor.Builder {

        private final IObject<?> member;

        public Builder(IObject<?> member) {
            super(member.getPath().toString(), member.getMeta().getCaption());
            this.member = member;
        }

        public Builder(IObject<?> member, boolean visible) {
            this(member);
            visible(visible);
        }

        public Builder title(IObject<?> member) {
            title(member.getMeta().getCaption());
            return this;
        }

        @Override
        public <E extends IEntity> ColumnDescriptor<E> build() {
            ColumnDescriptor<E> cd = null;
            MemberMeta mm = member.getMeta();
            if (mm.isEntity()) {
                cd = new MemberEntityColumnDescriptor<E>(this);
            } else if ((member instanceof ISet<?>) || (member instanceof IList<?>)) {
                cd = new MemberEntityCollectionColumnDescriptor<E>(this);
            } else if (member instanceof IPrimitiveSet<?>) {
                cd = new MemberCollectionColumnDescriptor<E>(this);
            } else if (member instanceof IPrimitive<?>) {
                cd = new MemberPrimitiveColumnDescriptor<E>(this);
            } else {
                cd = new MemberColumnDescriptor<E>(this);
            }
            return cd;
        }

    }
}
