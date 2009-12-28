/*
 * Pyx4j framework
 * Copyright (C) 2008-2009 pyx4j.com.
 *
 * Created on Dec 28, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IMember;
import com.pyx4j.entity.shared.IPrimitive;
import com.pyx4j.entity.shared.ISet;

public interface Page extends IEntity<Page> {

    interface Descriptor extends PageDescriptor, IMember<Page> {
    }

    Descriptor descriptor();

    IPrimitive<String, Page> content();

    ISet<PageDescriptor, Page> navigation();

}