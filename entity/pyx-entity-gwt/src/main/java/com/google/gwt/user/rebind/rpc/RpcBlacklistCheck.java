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
 * Created on Jan 27, 2010
 * @author vlads
 * @version $Id$
 */
package com.google.gwt.user.rebind.rpc;

import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;

/**
 * Access GWT internals implementation
 */
public class RpcBlacklistCheck {

    private final BlacklistTypeFilter rpcFilter;

    public RpcBlacklistCheck(TreeLogger logger, PropertyOracle propertyOracle) throws UnableToCompleteException {
        rpcFilter = new BlacklistTypeFilter(logger, propertyOracle);
    }

    public boolean isAllowed(JClassType type) {
        return rpcFilter.isAllowed(type);
    }
}
