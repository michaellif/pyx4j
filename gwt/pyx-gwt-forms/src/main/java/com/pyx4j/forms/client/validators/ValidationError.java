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
 * Created on Dec 10, 2011
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.forms.client.validators;

import com.pyx4j.i18n.shared.I18n;

public class ValidationError {

    private static final I18n i18n = I18n.get(ValidationError.class);

    private String title;

    private String message;

    private String locationHint;

    public ValidationError(String title, String message, String locationHint) {
        this.title = title;
        this.message = message;
        this.locationHint = locationHint;
    }

    public ValidationError(String message) {
        this(null, message, null);
    }

    public String getMessageString(boolean showLocation) {
        StringBuilder builder = new StringBuilder();
        if (title != null && !title.isEmpty()) {
            builder.append("'");
            if (showLocation && locationHint != null && !locationHint.isEmpty()) {
                builder.append(locationHint).append("/");
            }
            builder.append(title).append("' ").append(i18n.tr("is not valid")).append(", ").append(message);
        }
        return builder.toString();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setLocationHint(String locationHint) {
        this.locationHint = locationHint;
    }

    public String getLocationHint() {
        return locationHint;
    }

}
