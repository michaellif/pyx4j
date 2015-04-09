/*
 * Pyx4j framework
 * Copyright (C) 2006-2010 pyx4j.com.
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
 * @author vlads
 */
package com.pyx4j.widgets.client.dialog;

import com.pyx4j.commons.IDebugId;

/**
 * Created on 26-Sep-06
 * 
 * Create Dialog with Custom button.
 */
public interface Custom2Option extends DialogOptions {

    public String custom2Text();

    public boolean onClickCustom2();

    public IDebugId getCustom2DebugID();
}
