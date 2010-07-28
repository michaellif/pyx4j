/*
 * Pyx4j framework
 * Copyright (C) 2008-2010 pyx4j.com.
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
 * Created on Jul 28, 2010
 * @author michaellif
 * @version $Id$
 */
package com.pyx4j.widgets.client.tree;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {

    private TreeNode parent;

    private final List<TreeNode> children;

    private Object userObject;

    private boolean selectable;

    public TreeNode() {
        children = new ArrayList<TreeNode>();
    }

    public TreeNode getChildAt(int childIndex) {
        return children.get(childIndex);

    }

    public int getChildCount() {
        return children.size();
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        return children.indexOf(node);
    }

    public boolean isLeaf() {
        return getChildCount() == 0;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void isSelectable(boolean flag) {
        selectable = flag;
    }

    public List<TreeNode> children() {
        return children;
    }

    public void insert(TreeNode child, int index) {
        children.add(index, child);
    }

    public void remove(int index) {
        children.remove(index);
    }

    public void remove(TreeNode node) {
        children.remove(node);
    }

    public void setUserObject(Object object) {
        userObject = object;
    }

    public Object getUserObject() {
        return userObject;
    }

    public void removeFromParent() {
        TreeNode parent = getParent();
        if (parent != null) {
            parent.remove(this);
        }
    }

    public void setParent(TreeNode newParent) {
        parent = newParent;
    }
}
