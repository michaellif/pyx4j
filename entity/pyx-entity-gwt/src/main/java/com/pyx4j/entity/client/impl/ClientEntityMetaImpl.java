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
 * Created on Jan 11, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.client.impl;

import java.util.HashMap;
import java.util.Set;

import com.pyx4j.entity.shared.meta.EntityMeta;
import com.pyx4j.entity.shared.meta.MemberMeta;

public abstract class ClientEntityMetaImpl implements EntityMeta {

    private final String caption;

    private final String description;

    private final boolean persistenceTransient;

    protected final HashMap<String, MemberMeta> membersMeta = new HashMap<String, MemberMeta>();

    public ClientEntityMetaImpl(String caption, String description, boolean persistenceTransient, String[] membersNames) {
        super();
        this.caption = caption;
        this.description = description;
        this.persistenceTransient = persistenceTransient;
        for (String m : membersNames) {
            membersMeta.put(m, null);
        }
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isTransient() {
        return persistenceTransient;
    }

    @Override
    public Set<String> getMemberNames() {
        return membersMeta.keySet();
    }

    /**
     * Generated method
     */
    protected abstract MemberMeta createMemberMeta(String memberName);

    @Override
    public MemberMeta getMemberMeta(String memberName) {
        MemberMeta memberMeta = membersMeta.get(memberName);
        if (memberMeta == null) {
            memberMeta = createMemberMeta(memberName);
            membersMeta.put(memberName, memberMeta);
        }
        return memberMeta;
    }

}
