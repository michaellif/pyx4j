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
 * Created on May 26, 2014
 * @author vlads
 */
package com.pyx4j.security.shared;

/**
 * Permissions given by default without explicit grant in ACL.
 * 
 * Used to find the code that have unspecified security.
 * 
 * Each application should declare its own ImpliedPermission
 */
public abstract class ImpliedPermission implements Permission {

    private static final long serialVersionUID = -3624446631087758603L;

    protected ImpliedPermission() {
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public boolean implies(Permission p) {
        return ((p != null) && (p.getClass() == getClass()));
    }

}
