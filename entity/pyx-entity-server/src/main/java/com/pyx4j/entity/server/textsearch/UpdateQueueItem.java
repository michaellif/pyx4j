/*
 * Pyx4j framework
 * Copyright (C) 2008-2015 pyx4j.com.
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
 * Created on May 22, 2015
 * @author vlads
 */
package com.pyx4j.entity.server.textsearch;

import com.pyx4j.commons.EqualsHelper;
import com.pyx4j.entity.core.IEntity;

class UpdateQueueItem {

    private final String namespace;

    private final IEntity identityStub;

    private final boolean fireChains;

    UpdateQueueItem(String namespace, IEntity identityStub, boolean fireChains) {
        super();
        this.namespace = namespace;
        this.identityStub = identityStub;
        this.fireChains = fireChains;
    }

    public IEntity getIdentityStub() {
        return identityStub;
    }

    public String getNamespace() {
        return namespace;
    }

    public boolean isFireChains() {
        return fireChains;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (fireChains ? 1231 : 1237);
        result = prime * result + ((identityStub == null) ? 0 : identityStub.hashCode());
        result = prime * result + ((namespace == null) ? 0 : namespace.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        UpdateQueueItem other = (UpdateQueueItem) obj;
        return EqualsHelper.equals(identityStub, other.identityStub) && EqualsHelper.equals(namespace, other.namespace) && this.fireChains == other.fireChains;
    }

    @Override
    public String toString() {
        return "UpdateQueueItem [" + namespace + "@" + getIdentityStub().getDebugExceptionInfoString() + "]";
    }

}
