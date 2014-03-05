/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Mar 5, 2014
 * @author michaellif
 * @version $Id$
 */
package com.propertyvista.portal.shared.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.pyx4j.entity.shared.ISignature;
import com.pyx4j.entity.shared.ISignature.SignatureFormat;
import com.pyx4j.forms.client.validators.AbstractComponentValidator;
import com.pyx4j.forms.client.validators.FieldValidationError;
import com.pyx4j.i18n.shared.I18n;
import com.pyx4j.security.client.ClientContext;

import com.propertyvista.domain.security.CustomerSignature;

public class OriginalSignatureValidator extends AbstractComponentValidator<CustomerSignature> {

    private static final I18n i18n = I18n.get(OriginalSignatureValidator.class);

    @Override
    public FieldValidationError isValid() {
        ISignature value = getComponent().getValue();
        if (value != null) {
            SignatureFormat signatureFormat = value.signatureFormat().isNull() ? SignatureFormat.None : value.signatureFormat().getValue();
            switch (signatureFormat) {
            case AgreeBoxAndFullName:
            case FullName:
                if (value.fullName().getValue() != null && !isSignatureOriginal(value.fullName().getValue())) {
                    return new FieldValidationError(getComponent(), i18n.tr("You have to enter first name followed by last name"));
                }
                break;
            case Initials:
                if (value.initials().getValue() != null && !isInitialsOriginal(value.initials().getValue())) {
                    return new FieldValidationError(getComponent(), i18n.tr("You have to enter your initials without space"));
                }
                break;
            default:
                break;
            }
        }
        return null;
    }

    private boolean isSignatureOriginal(String signature) {

        List<String> signatureTokens = Arrays.asList(signature.trim().toLowerCase().split("\\s+"));

        String name = ClientContext.getUserVisit().getName();
        List<String> nameTokens = new ArrayList<>(Arrays.asList(name.trim().toLowerCase().split("\\s+")));

        if (signatureTokens.size() < 2 && nameTokens.size() >= 2) {
            return false;
        }

        for (String string : signatureTokens) {
            if (!nameTokens.contains(string)) {
                return false;
            } else {
                nameTokens.remove(string);
            }
        }

        return true;
    }

    private boolean isInitialsOriginal(String signatureInitials) {
        List<Character> signatureInitialsTokens = new ArrayList<>();
        for (char ch : signatureInitials.trim().toLowerCase().replaceAll("\\s", "").toCharArray()) {
            signatureInitialsTokens.add(ch);
        }

        String name = ClientContext.getUserVisit().getName();
        List<Character> nameInitialsTokens = new ArrayList<>();
        for (char ch : name.trim().toLowerCase().replaceAll("(?<=\\w)\\w*(?=\\s)*", "").replaceAll("\\s", "").toCharArray()) {
            nameInitialsTokens.add(ch);
        }

        if (signatureInitialsTokens.size() < 2 && nameInitialsTokens.size() >= 2) {
            return false;
        }

        for (Character c : signatureInitialsTokens) {
            if (!nameInitialsTokens.contains(c)) {
                return false;
            } else {
                nameInitialsTokens.remove(c);
            }
        }

        return true;
    }
}
