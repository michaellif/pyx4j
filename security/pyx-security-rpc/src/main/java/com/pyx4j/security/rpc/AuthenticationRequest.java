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
 * Created on Jan 18, 2010
 * @author vlads
 * @version $Id$
 */
package com.pyx4j.security.rpc;

import com.pyx4j.commons.Pair;
import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.annotations.LogTransient;
import com.pyx4j.entity.annotations.Transient;
import com.pyx4j.entity.annotations.validator.NotNull;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.i18n.annotations.I18n;

@Transient
@I18n(strategy = I18n.I18nStrategy.IgnoreThis)
public interface AuthenticationRequest extends IEntity {

    @I18n(strategy = I18n.I18nStrategy.IgnoreMember)
    IPrimitive<String> logoutApplicationUrl();

    @Editor(type = EditorType.email)
    @NotNull
    IPrimitive<String> email();

    @Editor(type = EditorType.password)
    @NotNull
    @LogTransient
    IPrimitive<String> password();

    @Caption(name = "Remember Me")
    IPrimitive<Boolean> rememberID();

    /**
     * Text from image for human verification.
     */
    @Caption(name = "Enter the code")
    @Editor(type = Editor.EditorType.captcha)
    IPrimitive<Pair<String, String>> captcha();
}
