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
 * Created on Jan 20, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.shared;

import java.io.Serializable;

/**
 * Extendable by application user Information
 */
public class UserVisit implements Serializable {

    private static final long serialVersionUID = 4747659543319319301L;

    private Long principalPrimaryKey;

    private String name;

    private String email;

    protected transient boolean changed;

    public UserVisit() {

    }

    public UserVisit(Long principalPrimaryKey, String name) {
        super();
        this.principalPrimaryKey = principalPrimaryKey;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.changed = true;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        this.changed = true;
    }

    public Long getPrincipalPrimaryKey() {
        return principalPrimaryKey;
    }

    public void setPrincipalPrimaryKey(Long principalPrimaryKey) {
        this.principalPrimaryKey = principalPrimaryKey;
    }

    public boolean isChanged() {
        return changed;
    }

    @Override
    public String toString() {
        return getPrincipalPrimaryKey() + " " + getName();
    }

}
