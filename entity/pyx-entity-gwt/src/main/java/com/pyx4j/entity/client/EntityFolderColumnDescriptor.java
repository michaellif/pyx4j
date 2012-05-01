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
 * Created on Feb 12, 2011
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.entity.client;

import com.pyx4j.entity.shared.IObject;

public class EntityFolderColumnDescriptor {

    private final IObject<?> object;

    private final String width;

    private final boolean readOnly;

    public EntityFolderColumnDescriptor(IObject<?> object, String width) {
        this(object, width, false);
    }

    public EntityFolderColumnDescriptor(IObject<?> object, String width, boolean readOnly) {
        super();
        this.object = object;
        this.width = width;
        this.readOnly = readOnly;
    }

    public IObject<?> getObject() {
        return object;
    }

    public String getWidth() {
        return width;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

}
