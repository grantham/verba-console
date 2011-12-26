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

package org.magnopere.lexicon.sql;

import org.magnopere.lexicon.latin.LexiconEntry;
import org.magnopere.lexicon.latin.MorphologyAnalysis;

/**
 * @author Roger Grantham
 * @since 6/4/11
 */
public interface PersistenceStrategy {

    final String MORPH_TABLE_SQL    = "org/magnopere/lexicon/sql/createMorphTable.sql";
    final String LEXICON_TABLE_SQL  = "org/magnopere/lexicon/sql/createLexiconTable.sql";

    /**
     * Sets up the table
     */
    void buildLexiconTable();
                              
    /**
     * Sets up the table
     */
    void buildMorphologyTable();


    /**
     * Closes resources held open for writing to the persistence layer
     */
    void close() throws Exception;

    /**
     * Persists the given analysis
     * @param analysis MorphologyAnalysis
     */
    void writeAnalysis(MorphologyAnalysis analysis);

    /**
     * Persists teh given entry
     * @param entry  LexiconEntry
     */
    void writeLexiconEntry(LexiconEntry entry);

}
