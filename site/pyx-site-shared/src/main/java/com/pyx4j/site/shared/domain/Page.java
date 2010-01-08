/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
 *
 * Created on Dec 28, 2009
 * @author Misha
 * @version $Id$
 */
package com.pyx4j.site.shared.domain;

import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IPrimitive;

public interface Page extends IEntity<Page> {

    PageDescriptor descriptor();

    IPrimitive<String> content();

}