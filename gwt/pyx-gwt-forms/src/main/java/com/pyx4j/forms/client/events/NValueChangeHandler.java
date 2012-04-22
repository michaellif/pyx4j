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
 * Created on 2011-01-19
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.forms.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Native component value change event handler, (Real Time) as opposite to CComponent
 */
public interface NValueChangeHandler<T> extends EventHandler {

    void onNValueChange(NValueChangeEvent<T> event);

}
