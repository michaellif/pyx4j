/*
 * Pyx4j framework
 * Copyright (C) 2008-2012 pyx4j.com.
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
 * Created on 2013-02-12
 * @author vlads
 */
package com.pyx4j.security.shared;

import java.io.Serializable;

@SuppressWarnings("serial")
public class PasswordSerializable implements Serializable {

    private char[] value;

    public PasswordSerializable() {

    }

    public PasswordSerializable(char[] value) {
        super();
        this.value = value;
    }

    public void destroy() {
        if (this.value != null) {
            for (int i = 0; i < this.value.length; i++) {
                this.value[i] = 0;
            }
            this.value = null;
        }
    }

    public char[] getValue() {
        return value;
    }

    public void setValue(char[] value) {
        this.value = value;
    }
}
