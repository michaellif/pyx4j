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
 * Created on Apr 13, 2016
 * @author vlads
 */
package com.pyx4j.forms.client.ui.concerns;

import com.pyx4j.gwt.commons.concerns.EnablingConcern;

/**
 * Internal framework assistant. Should not be used in application code
 */
public class EnablingConcernAccessAdapterConvertor extends AbstractAccessAdapter {

    public final EnablingConcern enablingConcern;

    public EnablingConcernAccessAdapterConvertor(EnablingConcern enablingConcern) {
        this.enablingConcern = enablingConcern;
    }
}
