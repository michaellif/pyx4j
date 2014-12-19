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
 * Created on Sep 6, 2012
 * @author ArtyomB
 */
package com.pyx4j.site.rpc.customization;

import java.io.Serializable;

import com.pyx4j.commons.UserRuntimeException;
import com.pyx4j.i18n.shared.I18n;

public class CustomizationOverwriteAttemptException extends UserRuntimeException implements Serializable {

    private static final I18n i18n = I18n.get(CustomizationOverwriteAttemptException.class);

    private static final long serialVersionUID = -5195090453948805463L;

    public CustomizationOverwriteAttemptException() {
        super(i18n.tr("Failed to save customization info because a customization info with the same name already exists"));
    }

}
