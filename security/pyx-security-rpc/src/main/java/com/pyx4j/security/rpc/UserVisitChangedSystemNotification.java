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
 * Created on Oct 30, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.rpc;

import java.io.Serializable;

import com.pyx4j.security.shared.UserVisit;

public class UserVisitChangedSystemNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    private UserVisit userVisit;

    protected UserVisitChangedSystemNotification() {
    }

    public UserVisitChangedSystemNotification(UserVisit userVisit) {
        this.userVisit = userVisit;
    }

    public UserVisit getUserVisit() {
        return userVisit;
    }

    public void setUserVisit(UserVisit userVisit) {
        this.userVisit = userVisit;
    }

}
