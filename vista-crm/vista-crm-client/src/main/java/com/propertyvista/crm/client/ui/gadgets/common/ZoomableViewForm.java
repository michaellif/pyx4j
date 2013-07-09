/*
 * (C) Copyright Property Vista Software Inc. 2011-2012 All Rights Reserved.
 *
 * This software is the confidential and proprietary information of Property Vista Software Inc. ("Confidential Information"). 
 * You shall not disclose such Confidential Information and shall use it only in accordance with the terms of the license agreement 
 * you entered into with Property Vista Software Inc.
 *
 * This notice and attribution to Property Vista Software Inc. may not be removed.
 *
 * Created on Sep 12, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.propertyvista.crm.client.ui.gadgets.common;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.Command;

import com.pyx4j.commons.GWTJava5Helper;
import com.pyx4j.entity.shared.EntityFactory;
import com.pyx4j.entity.shared.ICollection;
import com.pyx4j.entity.shared.IEntity;
import com.pyx4j.entity.shared.IObject;
import com.pyx4j.entity.shared.Path;
import com.pyx4j.forms.client.ui.CComponent;
import com.pyx4j.forms.client.ui.CField;

import com.propertyvista.common.client.ui.components.c.CEntityDecoratableForm;

public abstract class ZoomableViewForm<E extends IEntity> extends CEntityDecoratableForm<E> {

    public interface ZoominRequestHandler {
        void onZoomIn(IObject<?> zoomableObject);
    }

    private ZoominRequestHandler zoomInHandler;

    private IObject<?>[] zoomableMembers;

    public ZoomableViewForm(Class<E> clazz) {
        super(clazz);
        setEditable(false);
        setViewable(true);
    }

    /**
     * Sets up zoom in callback and defines which members should support it, must be called before {@link ZoomableViewForm#initContent()} to have any effect
     */
    public void initZoomIn(ZoominRequestHandler zoomInHandler, IObject<?>... zoomableMembers) {
        this.zoomableMembers = new IObject<?>[zoomableMembers.length];
        for (int i = 0; i < zoomableMembers.length; ++i) {
            this.zoomableMembers[i] = proto().getMember(zoomableMembers[i].getPath());
        }
//        ((ZoominLinkFactory) factory).initZoomIn(localZoomableMembers);
        this.zoomInHandler = zoomInHandler;

    }

    @SuppressWarnings("rawtypes")
    @Override
    public void bind(CComponent<?> component, final IObject<?> member) {
        super.bind(component, member);
        if (zoomInHandler != null) {
            if ((component instanceof ZoomableViewFolder) & isZoomable(member)) {
                List<IObject<?>> transformedZoomInMembers = new ArrayList<IObject<?>>();
                for (IObject<?> zoomableMember : zoomableMembers) {
                    String zoomableMemberPath = zoomableMember.getPath().toString();
                    if (zoomableMemberPath.startsWith(member.getPath().toString())) {
                        IEntity zoomedProto = EntityFactory.getEntityPrototype(((ICollection) member).getValueClass());
                        String transformedMemberPath = GWTJava5Helper.getSimpleName(zoomedProto.getValueClass()) + zoomableMemberPath.split("\\[\\]")[1];
                        IObject<?> transformedZoomInMember = zoomedProto.getMember(new Path(transformedMemberPath));
                        transformedZoomInMembers.add(transformedZoomInMember);
                    }
                }
                ((ZoomableViewFolder<?>) component)
                        .initZoomIn(zoomInHandler, transformedZoomInMembers.toArray(new IObject<?>[transformedZoomInMembers.size()]));
            } else if (isZoomable(member)) {
                ((CField) component).setNavigationCommand(new Command() {
                    @Override
                    public void execute() {
                        zoomInHandler.onZoomIn(ZoomableViewForm.this.getValue().getMember(member.getPath()));
                    }
                });

            }

        }
    }

    private boolean isZoomable(IObject<?> member) {
        for (IObject<?> zoomableMember : zoomableMembers) {
            if (member instanceof ICollection) {
                if (zoomableMember.getPath().toString().startsWith(member.getPath().toString())) {
                    return true;
                }
            } else if (zoomableMember == member) {
                return true;
            }
        }
        return false;
    }
}
