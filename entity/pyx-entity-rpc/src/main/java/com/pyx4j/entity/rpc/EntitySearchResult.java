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
 * Created on 2010-05-04
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.entity.rpc;

import java.io.Serializable;
import java.util.Vector;

import com.pyx4j.entity.shared.IEntity;

@SuppressWarnings("serial")
public class EntitySearchResult<E extends IEntity> implements Serializable {

    private Vector<E> data;

    private boolean hasMoreData;

    private boolean quotaExceeded;

    private String encodedCursorReference;

    public EntitySearchResult() {
        data = new Vector<E>(); // TODO vlads - Why are we using Vector here and not a List?
    }

    public Vector<E> getData() {
        return data;
    }

    public void setData(Vector<E> data) {
        this.data = data;
    }

    public void add(E entity) {
        this.data.add(entity);
    }

    public boolean hasMoreData() {
        return hasMoreData;
    }

    public void hasMoreData(boolean hasMoreData) {
        this.hasMoreData = hasMoreData;
    }

    public boolean isQuotaExceeded() {
        return quotaExceeded;
    }

    public void setQuotaExceeded(boolean quotaExceeded) {
        this.quotaExceeded = quotaExceeded;
    }

    public String getEncodedCursorReference() {
        return encodedCursorReference;
    }

    public void setEncodedCursorReference(String encodedCursorReference) {
        this.encodedCursorReference = encodedCursorReference;
    }
}
