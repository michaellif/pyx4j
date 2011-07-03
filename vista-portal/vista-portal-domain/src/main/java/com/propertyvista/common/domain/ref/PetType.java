/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-07-02
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.domain.ref;

import com.pyx4j.i18n.shared.I18nEnum;
import com.pyx4j.i18n.shared.Translation;

public enum PetType {

    @Translation("Dog")
    dog,

    @Translation("Cat")
    cat;

    @Override
    public String toString() {
        return I18nEnum.tr(this);
    }
}