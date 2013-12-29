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
package com.pyx4j.forms.client.ui.datatable;

import java.util.EnumSet;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IList;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;
import com.pyx4j.entity.core.IPrimitiveSet;
import com.pyx4j.entity.core.ISet;
import com.pyx4j.entity.core.ObjectClassType;
import com.pyx4j.entity.core.Path;
import com.pyx4j.entity.core.meta.MemberMeta;

public class MemberColumnDescriptor extends ColumnDescriptor {

    protected MemberColumnDescriptor(ColumnDescriptor.Builder builder) {
        super(builder);
    }

    public Path getColumnPath() {
        return ((Builder) getBuilder()).member.getPath();
    }

    public String getFormatPattern() {
        return ((Builder) getBuilder()).member.getMeta().getFormat();
    }

    @Override
    public String convert(IEntity entity) {
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
            if (EnumSet.of(ObjectClassType.EntityList, ObjectClassType.EntitySet).contains(member.getMeta().getObjectClassType())) {
                this.sortable(false);
            }
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
        public ColumnDescriptor build() {
            ColumnDescriptor cd = null;
            MemberMeta mm = member.getMeta();
            if (mm.isEntity()) {
                cd = new MemberEntityColumnDescriptor(this);
            } else if ((member instanceof ISet<?>) || (member instanceof IList<?>)) {
                cd = new MemberEntityCollectionColumnDescriptor(this);
            } else if (member instanceof IPrimitiveSet<?>) {
                cd = new MemberCollectionColumnDescriptor(this);
            } else if (member instanceof IPrimitive<?>) {
                cd = new MemberPrimitiveColumnDescriptor(this);
            } else {
                cd = new MemberColumnDescriptor(this);
            }
            return cd;
        }

    }
}
