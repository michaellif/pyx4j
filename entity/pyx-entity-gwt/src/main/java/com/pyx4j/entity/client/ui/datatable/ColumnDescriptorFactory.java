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
import com.pyx4j.entity.shared.IObject;

public class ColumnDescriptorFactory {

    public static <E extends IEntity> ColumnDescriptor<E> createColumnDescriptor(E meta, IObject<?> member, boolean visible) {
        return new MemberColumnDescriptor.Builder(member, visible).build();
    }

    public static <E extends IEntity> ColumnDescriptor<E> createColumnDescriptor(E meta, IObject<?> member, String width, boolean visible) {
        return createColumnDescriptor(meta, member, width, false, visible);
    }

    public static <E extends IEntity> ColumnDescriptor<E> createColumnDescriptor(E meta, IObject<?> member, String width, boolean wordWrap, boolean visible) {
        ColumnDescriptor<E> cd = createColumnDescriptor(meta, member, visible);
        cd.setWidth(width);
        cd.setWordWrap(wordWrap);
        return cd;
    }

// custom titled column creation:

    public static <E extends IEntity> ColumnDescriptor<E> createTitledColumnDescriptor(E meta, IObject<?> member, String title, boolean visible) {
        return createTitledColumnDescriptor(meta, member, title, ColumnDescriptor.DEFAULT_WIDTH, false, visible);
    }

    public static <E extends IEntity> ColumnDescriptor<E> createTitledColumnDescriptor(E meta, IObject<?> member, String title, String width, boolean visible) {
        return createTitledColumnDescriptor(meta, member, title, width, false, visible);
    }

    public static <E extends IEntity> ColumnDescriptor<E> createTitledColumnDescriptor(E meta, IObject<?> member, String title, String width, boolean wordWrap,
            boolean visible) {
        ColumnDescriptor<E> cd = ColumnDescriptorFactory.createColumnDescriptor(meta, member, visible);
        cd.setColumnTitle(title);
        cd.setWidth(width);
        cd.setWordWrap(wordWrap);
        return cd;
    }
}
