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
 * Created on Aug 10, 2011
 * @author vlads
 */
package com.pyx4j.gwt.rpc.upload;

import java.io.Serializable;

import com.pyx4j.commons.Key;

@SuppressWarnings("serial")
public class UploadId implements Serializable {

    private String deferredCorrelationId;

    private Key uploadKey;

    public UploadId() {

    }

    public String getDeferredCorrelationId() {
        return deferredCorrelationId;
    }

    public void setDeferredCorrelationId(String deferredCorrelationId) {
        this.deferredCorrelationId = deferredCorrelationId;
    }

    public Key getUploadKey() {
        return uploadKey;
    }

    public void setUploadKey(Key uploadKey) {
        this.uploadKey = uploadKey;
    }
}
