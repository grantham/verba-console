/*
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * The Initial Developer of the Original Code is Roger Grantham.
 * Portions created by Roger Grantham are
 * Copyright (C) 2011. All Rights Reserved.
 *
 * Contributor(s): Roger Grantham
 *
 * Alternatively, the contents of this file may be used under the terms
 * of the GNU Public License license (the  "[GPL] License"), in which case the
 * provisions of [GPL] License are applicable instead of those
 * above.  If you wish to allow use of your version of this file only
 * under the terms of the [GPL] License and not to allow others to use
 * your version of this file under the MPL, indicate your decision by
 * deleting  the provisions above and replace  them with the notice and
 * other provisions required by the [GPL] License.  If you do not delete
 * the provisions above, a recipient may use your version of this file
 * under either the MPL or the [GPL] License.
 */

package org.magnopere.lexicon.latin;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * @author Roger Grantham
 * @since May 29, 2011
 */

public class RecordStack<T> {

    private final Stack<T> records = new Stack<T>();

    private final List<RepositoryListener<T>> listeners = new ArrayList<RepositoryListener<T>>();

    public void addListener(RepositoryListener<T> listener){
        listeners.add(listener);
    }

    public void push(T rec){
        records.push(rec);
        for (RepositoryListener<T> listener: listeners){
            listener.pushCalled(rec);
        }
    }

    public T peek(){
        final T rec = records.peek();
        for (RepositoryListener<T> listener: listeners){
            listener.peekCalled(rec);
        }
        return rec;
    }

    public T pop(){
        final T rec = records.pop();
        for (RepositoryListener<T> listener: listeners){
            listener.popCalled(rec);
        }
        return rec;
    }


    public interface RepositoryListener<T> {

        void pushCalled(T rec);

        void peekCalled(T rec);

        void popCalled(T rec);

    }


}
