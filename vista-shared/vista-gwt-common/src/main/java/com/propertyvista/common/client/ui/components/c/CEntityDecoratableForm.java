/*
 * (C) Copyright Property Vista Software Inc. 2011- All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Oct 24, 2011
 * @author Vlad
 * @version $Id$
 */
package com.propertyvista.common.client.ui.components.c;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.forms.client.ui.CEntityForm;
import com.pyx4j.forms.client.ui.IEditableComponentFactory;

@Deprecated
abstract public class CEntityDecoratableForm<E extends IEntity> extends CEntityForm<E> {

    public CEntityDecoratableForm(Class<E> clazz) {
        super(clazz);
    }

    public CEntityDecoratableForm(Class<E> clazz, IEditableComponentFactory factory) {
        super(clazz, factory);
    }

}
