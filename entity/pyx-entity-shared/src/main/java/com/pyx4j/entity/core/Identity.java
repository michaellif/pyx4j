/*
 * Pyx4j framework
 * Copyright (C) 2008-2013 pyx4j.com.
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
 * Created on May 19, 2015
 * @author michaellif
 * @version $Id: code-templates.xml 12647 2013-05-01 18:01:19Z vlads $
 */
package com.pyx4j.entity.core;

import java.io.Serializable;

import com.pyx4j.commons.GWTSerializable;

public final class Identity<E extends IEntity> implements Serializable {

    private static final long serialVersionUID = 1L;

    private E identityStub;

    @GWTSerializable
    protected Identity() {

    }

    public Identity(E entity) {
        identityStub = entity.createIdentityStub();
    }

    public E getIdentityStub() {
        return identityStub;
    }

    public static <T extends IEntity> Identity<T> key(T entity) {
        return new Identity<>(entity);
    }
}
