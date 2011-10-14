/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Aug 26, 2011
 * @author vlads
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.components.media;

import com.pyx4j.forms.client.ui.CEditableComponent;
import com.pyx4j.forms.client.validators.EditableValueValidator;
import com.pyx4j.i18n.shared.I18n;

import com.propertyvista.domain.media.Media;

public class MediaItemValidator implements EditableValueValidator<Media> {

    private static I18n i18n = I18n.get(MediaItemValidator.class);

    @Override
    public boolean isValid(CEditableComponent<Media, ?> component, Media value) {
        if (value.type().isNull()) {
            return false;
        }
        boolean valid = true;
        switch (value.type().getValue()) {
        case file:
            valid = !value.file().isNull();
            break;
        case externalUrl:
            valid = !value.url().isNull();
            break;
        case youTube:
            valid = !value.youTubeVideoID().isNull();
            break;
        }
        return valid;
    }

    @Override
    public String getValidationMessage(CEditableComponent<Media, ?> component, Media value) {
        return i18n.tr("Media source cannot be empty!");
    }
}
