/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2011-02-10
 * @author antonk
 * @version $Id: code-templates.xml 7812 2011-01-10 20:13:00Z vlads $
 */
package com.propertyvista.portal.domain.pt;

import com.pyx4j.entity.annotations.Caption;
import com.pyx4j.entity.annotations.Editor;
import com.pyx4j.entity.annotations.Editor.EditorType;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface LegalQuestions extends IEntity {
    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever been sued for rent?")
    IPrimitive<Boolean> suedForRent();

    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever been sued for damages?")
    IPrimitive<Boolean> suedForDamages();

    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever been evicted?")
    IPrimitive<Boolean> everEvicted();

    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever defaulted on a lease?")
    IPrimitive<Boolean> defaultedOnLease();

    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever been convicted of a felony that involved an offence against property, persons, government officials, or that involved firearms, illegal drugs, or sex or sex crimes?")
    IPrimitive<Boolean> convictedOfFelony();

    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever had any public record suits, liens, judgements or reposessions?")
    IPrimitive<Boolean> legalTroubles();

    @Editor(type = EditorType.radiogroup)
    @Caption(name = "Have you ever filed for bankruptcy protection?")
    IPrimitive<Boolean> filedBankruptcy();

}
