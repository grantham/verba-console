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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static org.magnopere.lexicon.latin.Slurper.*;

/**
 * @author Roger Grantham
 * @since 6/4/11
 */
public class SQLWriter implements PersistenceStrategy {
    
    private static final String INSERT_ANALYSIS = "INSERT INTO morphology (form, lemma, grammaticalCase, degree, gender, mood, number, person, pos, tense, voice) VALUES ";
    private static final String INSERT_LEX_ENTRY = "INSERT INTO lexicon (lemma, ordinality, orthography, endings, gender, pos, definition) VALUES ";
    
    private final BufferedWriter    lexiconWriter;
    private final BufferedWriter    morphologyWriter;

    private final String createLexiconTableScript;
    private final String createMorphologyTableScript;

    public SQLWriter(File outputDir) {
        if (outputDir == null) throw new IllegalArgumentException("null: outputDir");
        createMorphologyTableScript = slurp(getClass().getClassLoader(), MORPH_TABLE_SQL);
        createLexiconTableScript = slurp(getClass().getClassLoader(), LEXICON_TABLE_SQL);
        try {
            lexiconWriter       = new BufferedWriter(new FileWriter(new File(outputDir, "lexicon.sql")));
            morphologyWriter    = new BufferedWriter(new FileWriter(new File(outputDir, "morphology.sql")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void buildLexiconTable() {
        try {
            lexiconWriter.write(createLexiconTableScript);
            lexiconWriter.write(String.format("BEGIN TRANSACTION;%n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void buildMorphologyTable() {
        try {

            morphologyWriter.write(createMorphologyTableScript);
            morphologyWriter.write(String.format("BEGIN TRANSACTION;%n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        lexiconWriter.write("END TRANSACTION;");
        lexiconWriter.flush();
        lexiconWriter.close();
        morphologyWriter.write("END TRANSACTION;");
        morphologyWriter.flush();
        morphologyWriter.close();
    }

    @Override
    public void writeAnalysis(MorphologyAnalysis analysis) {
        final String record = String.format("%s (\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");%n",
                INSERT_ANALYSIS,
                analysis.getForm(),
                analysis.getLemma(),
                analysis.getGrammaticalCase(),
                analysis.getDegree(),
                analysis.getGender(),
                analysis.getMood(),
                analysis.getNumber(),
                analysis.getPerson(),
                analysis.getPos(),
                analysis.getTense(),
                analysis.getVoice());
        try {
            morphologyWriter.write(record);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void writeLexiconEntry(LexiconEntry entry) {
        final String record = String.format("%s (\"%s\", \"%d\", \"%s\", \"%s\", \"%s\", \"%s\", \"%s\");%n",
                INSERT_LEX_ENTRY,
                entry.getKey(),
                entry.getOrdinality(),
                entry.getOrthography(),
                entry.getiType(),
                entry.getGender(),
                entry.getPos(),
                entry.getDefinition().replaceAll("[\"]", "[\']"));
        try {
            lexiconWriter.write(record);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
