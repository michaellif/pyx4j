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
 * Created on May 3, 2012
 * @author vlads
 * @version $Id$
 */
package org.apache.log4j;

/**
 * https://issues.apache.org/bugzilla/show_bug.cgi?id=50486
 */
public class MDC50486 {

    @SuppressWarnings("rawtypes")
    public static void remove() {
        ((ThreadLocal) MDC.mdc.tlm).remove();
    }
}
