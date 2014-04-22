/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on 2014-04-22
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.tools.common.widgets.selectorfolder;

import com.pyx4j.entity.core.IEntity;
import com.pyx4j.forms.client.ui.folder.CEntityFolder;
import com.pyx4j.forms.client.ui.folder.IFolderDecorator;

public abstract class SelectorFolder<E extends IEntity> extends CEntityFolder<E> {

    public SelectorFolder(Class<E> rowClass) {
        super(rowClass);
    }

    @Override
    protected SelectorFolderItemDecorator<E> createItemDecorator() {
        return new SelectorFolderItemDecorator<E>();
    }

    @Override
    protected IFolderDecorator<E> createFolderDecorator() {
        return new SelectorFolderDecorator<>();

    }
}
