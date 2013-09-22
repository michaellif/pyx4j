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
 * Created on Dec 3, 2011
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.rpc.client;

import com.pyx4j.i18n.annotations.I18nComment;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.rpc.shared.ServiceExecution.OperationType;

/**
 * This class instance is generated for each server base on data in annotation @ServiceExecution
 * 
 */
public class ServiceExecutionInfo {

    private static final I18n i18n = I18n.get(ServiceExecutionInfo.class);

    private final OperationType operationType;

    private final String waitCaption;

    @I18nComment("Default message at the top of the screen, when server operation is in progress")
    public static ServiceExecutionInfo DEFAULT = new ServiceExecutionInfo(OperationType.Transparent, i18n.tr("Loading..."));

    static ServiceExecutionInfo BACKGROUND = new ServiceExecutionInfo(OperationType.NonBlocking, null);

    public ServiceExecutionInfo(OperationType operationType, String waitCaption) {
        super();
        this.operationType = operationType;
        this.waitCaption = waitCaption;
    }

    public OperationType operationType() {
        return operationType;
    }

    public String waitCaption() {
        return waitCaption;
    }

}
