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
 * Created on Jul 19, 2012
 * @author ArtyomB
 * @version $Id$
 */
package com.pyx4j.forms.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.TreeViewModel;

import com.pyx4j.entity.core.AttachLevel;
import com.pyx4j.entity.core.ICollection;
import com.pyx4j.entity.core.IEntity;
import com.pyx4j.entity.core.IObject;
import com.pyx4j.entity.core.IPrimitive;

public class EntityViewerCellTree extends CellTree {

    public static class EntityChildrenDataProvider extends ListDataProvider<IObject<?>> {

        public EntityChildrenDataProvider(IEntity entity) {
            super(getMembers(entity));
        }

        private static List<IObject<?>> getMembers(IEntity entity) {
            List<IObject<?>> members = new ArrayList<IObject<?>>();
            for (String memberName : entity.getEntityMeta().getMemberNames()) {
                members.add(entity.getMember(memberName));
            }
            return members;
        }
    }

    public static class IObjectCell extends AbstractCell<IObject<?>> {

        @Override
        public void render(com.google.gwt.cell.client.Cell.Context context, IObject<?> value, SafeHtmlBuilder sb) {
            if (value == null) {
                sb.appendEscaped("NULL");
            } else {
                IObject<?> obj = value;
                sb.appendHtmlConstant("<div style=\"border-width: 1px; border-style: solid; margin-bottom: 0.3em; padding-left: 1em;\">");

                sb.appendHtmlConstant("<div style=\"font-weight: bold;\">");
                if (obj.getFieldName() == null) {
                    sb.appendEscaped(((IEntity) obj).getEntityMeta().getCaption());
                } else {
                    sb.appendEscaped("" + obj.getFieldName());
                }
                sb.appendHtmlConstant("</div>");

                if (obj.getFieldName() != null) {
                    sb.appendHtmlConstant("<div>");
                    sb.appendEscaped("Title: " + obj.getMeta().getCaption());
                    sb.appendHtmlConstant("</div>");
                }

                if (value instanceof IEntity) {
                    IEntity entity = (IEntity) value;
                    String pk = entity.getPrimaryKey() == null ? "new" : entity.getPrimaryKey().toString();
                    String hashCode = "" + System.identityHashCode(entity);

                    sb.appendHtmlConstant("<div>");
                    sb.appendEscaped("Class: " + entity.getEntityMeta().getEntityClass().getName());
                    sb.appendHtmlConstant("</div>");
                    sb.appendHtmlConstant("<div>");
                    sb.appendEscaped("PK: " + pk + "; VM Hash Code: " + hashCode);
                    sb.appendHtmlConstant("</div>");

                    if (entity.isValueDetached()) {
                        sb.appendHtmlConstant("<div>");
                        sb.appendEscaped("DETACHED");
                        sb.appendHtmlConstant("</div>");
                    } else if (entity.isNull()) {
                        sb.appendHtmlConstant("<div>");
                        sb.appendEscaped("NULL");
                        sb.appendHtmlConstant("</div>");
                    }

                } else if (value instanceof IPrimitive) {
                    IPrimitive<?> primitive = (IPrimitive<?>) value;
                    sb.appendHtmlConstant("<div>");
                    sb.appendEscaped(!primitive.isNull() ? primitive.getValue().toString() : "NULL");
                    sb.appendHtmlConstant("</div>");

                } else if (value instanceof ICollection) {

                    ICollection<?, ?> col = (ICollection<?, ?>) value;

                    if (col.getAttachLevel() == AttachLevel.Detached) {

                        sb.appendEscaped(" DETACHED");
                        sb.appendHtmlConstant("</div>");
                    } else if (col.isNull()) {
                        sb.appendHtmlConstant("<div>");
                        sb.appendEscaped(" NULL");
                        sb.appendHtmlConstant("</div>");
                    } else if (col.isEmpty()) {
                        sb.appendHtmlConstant("<div>");
                        sb.appendEscaped(" EMPTY");
                        sb.appendHtmlConstant("</div>");
                    }

                } else {
                    sb.appendEscaped("NOT YET SUPPORTED: " + value.getClass().getName());
                }
                sb.appendHtmlConstant("</div>");
            }
        }
    }

    public static class EntityTreeViewModel implements TreeViewModel {

        private final IObject<?> root;

        public EntityTreeViewModel(IObject<?> root) {
            this.root = root;
        }

        @Override
        public <T> NodeInfo<?> getNodeInfo(T value) {
            if (value == null) {
                List<IObject<?>> nodeList = new ArrayList<IObject<?>>(1);
                nodeList.add(root);
                ListDataProvider<IObject<?>> firstNodeProvider = new ListDataProvider<IObject<?>>(nodeList);
                return new DefaultNodeInfo<IObject<?>>(firstNodeProvider, new IObjectCell());
            } else if (value instanceof IEntity) {
                EntityChildrenDataProvider entityChildrenProvider = new EntityChildrenDataProvider((IEntity) value);
                return new DefaultNodeInfo<IObject<?>>(entityChildrenProvider, new IObjectCell());
            } else if (value instanceof ICollection) {
                ArrayList<IObject<?>> listNodes = new ArrayList<IObject<?>>();
                for (IObject<?> obj : ((ICollection<?, ?>) value)) {
                    listNodes.add(obj);
                }
                ListDataProvider<IObject<?>> listNodesProvider = new ListDataProvider<IObject<?>>(listNodes);
                return new DefaultNodeInfo<IObject<?>>(listNodesProvider, new IObjectCell());
            }

            return null;
        }

        @Override
        public boolean isLeaf(Object value) {
            if (value instanceof IPrimitive) {
                return true;
            } else if (value instanceof IEntity) {
                IEntity entity = (IEntity) value;
                return entity.isValueDetached() | entity.isNull();
            } else if (value instanceof ICollection) {
                return ((ICollection<?, ?>) value).getAttachLevel() == AttachLevel.Detached;
            } else {
                return false;
            }
        }
    }

    public EntityViewerCellTree(IObject<?> root) {

        super(new EntityTreeViewModel(root), null, getResources());
    }

    private static Resources getResources() {
        // TODO remove the workaround when it's not required
        // WORKAROUND START (see: http://code.google.com/p/google-web-toolkit/issues/detail?id=6359 for more details)
        Resources resources = GWT.create(CellTree.BasicResources.class);
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeTopItem() + " {margin-top: 0px;}");
        // WORKAROUND END
        // remove padding (i don't really know why it must be done here and not in the theme, but it that the way it works)
        StyleInjector.injectAtEnd("." + resources.cellTreeStyle().cellTreeItem() + " {padding: 0px;}");
        return resources;
    }

}
