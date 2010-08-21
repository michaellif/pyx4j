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
 * Created on Aug 21, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.essentials.rpc.admin;

import java.io.Serializable;

import com.pyx4j.entity.shared.criterion.EntitySearchCriteria;

public class DBMaintenanceRequest implements Serializable {

    private static final long serialVersionUID = -496432664030048422L;

    private int batchSize;

    private EntitySearchCriteria<?> criteria;

    private Class<? extends IDBMaintenanceProcessor> processor;

    public DBMaintenanceRequest() {

    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public EntitySearchCriteria<?> getCriteria() {
        return criteria;
    }

    public void setCriteria(EntitySearchCriteria<?> criteria) {
        this.criteria = criteria;
    }

    public Class<? extends IDBMaintenanceProcessor> getProcessor() {
        return processor;
    }

    public void setProcessor(Class<? extends IDBMaintenanceProcessor> processor) {
        this.processor = processor;
    }

}
