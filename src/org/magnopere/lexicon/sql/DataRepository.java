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
import org.magnopere.lexicon.latin.Orthography;

import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

import static org.magnopere.lexicon.latin.Slurper.*;

/**
 * A simple facade to the database
 * @author Roger Grantham
 * @since May 29, 2011
 */
public class DataRepository implements PersistenceStrategy {
    private static final Logger LOG = Logger.getLogger(DataRepository.class.getName());


    private static final String DB_FILE_NAME = "verba.db";
    private static final String DB_DRIVER_NAME = "org.sqlite.JDBC";
    private final String createMorphologyTableScript;
    private final String createLexiconTableScript;
    private PreparedStatement writeAnalysis;
    private PreparedStatement findAnalyses;
    private PreparedStatement writeLexiconEntry;
    private PreparedStatement findLexiconEntries;

    private final Connection conn;
    private final String dbDir;
    private final String dbPath;


    public DataRepository(String dbDir) {
        if (dbDir == null) throw new IllegalArgumentException("null: dbDir");
        this.dbDir = dbDir.trim().replaceFirst("[\\/]$", "");
        this.dbPath = dbDir + System.getProperty("file.separator") + DB_FILE_NAME;
        try {
            Class.forName(DB_DRIVER_NAME);
            conn = DriverManager.getConnection(String.format("jdbc:sqlite:%s", dbPath));
            createMorphologyTableScript = slurp(getClass().getClassLoader(), MORPH_TABLE_SQL);
            createLexiconTableScript = slurp(getClass().getClassLoader(), LEXICON_TABLE_SQL);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void buildLexiconTable()  {
        try {
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists lexicon;");
            stat.executeUpdate(createLexiconTableScript);
            stat.close();
            writeLexiconEntry = conn.prepareStatement(
             "insert into lexicon (lemma, ordinality, orthography, endings, gender, pos, definition) values (?, ?, ?, ?, ?, ?, ?);");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void buildMorphologyTable()  {
        try {
            Statement stat = conn.createStatement();
            stat.executeUpdate("drop table if exists morphology;");
            stat.executeUpdate(createMorphologyTableScript);
            stat.close();
            writeAnalysis = conn.prepareStatement(
             "insert into morphology (form, lemma, grammaticalCase, degree, gender, mood, number, person, pos, tense, voice) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void close() throws Exception {
        writeAnalysis.execute();
        writeLexiconEntry.execute();
        try {
            conn.close();
        } finally {
            conn.close();
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        conn.close();
    }

    /**
     * Accepts a word form, finds all matching morphological forms and their lemmas, and
     * all lexicon entries for those unique lemmas collecting this all in a QueryResult
     * @param word a word form, e.g. monebantur will find the lexicon entry or "moneo, monere, monui, monitus"
     * @return QueryResult
     */
    public QueryResult lookup(String word){
        final String normalized = Orthography.rectify(word.trim());
        final QueryResult result = new QueryResult();
        // first find all matching morphological forms.
        final List<MorphologyAnalysis> analyses = findAnalysis(normalized);
        result.addMorphologicalAnalysis(analyses);
        // then look up each unique lemma.
        for (String lemma: result.getUniqueLemmas()){
            final List<LexiconEntry> entries = findLexiconEntry(lemma);
            result.addLexicographicalEntry(entries);
        }
        // finally return the QueryResult
        return result;
    }

    public List<MorphologyAnalysis> findAnalysis(String form) {
        final String normalized = Orthography.rectify(form.trim());
        final List<MorphologyAnalysis> analyses = new ArrayList<MorphologyAnalysis>();
        try {

            if (findAnalyses == null){
                findAnalyses = conn.prepareStatement("SELECT form, lemma, grammaticalCase, degree, gender, mood, number, person, pos, tense, voice FROM morphology WHERE form = ?");
            }
            findAnalyses.setString(1, normalized);
            final ResultSet result = findAnalyses.executeQuery();
            while (result.next()){
                final MorphologyAnalysis analysis = new MorphologyAnalysis();
                analysis.setForm(result.getString(1));
                analysis.setLemma(result.getString(2));
                analysis.setGrammaticalCase(result.getString(3));
                analysis.setDegree(result.getString(4));
                analysis.setGender(result.getString(5));
                analysis.setMood(result.getString(6));
                analysis.setNumber(result.getString(7));
                analysis.setPerson(result.getString(8));
                analysis.setPos(result.getString(9));
                analysis.setTense(result.getString(10));
                analysis.setVoice(result.getString(11));
                analyses.add(analysis);
            }
            result.close();
        } catch (SQLException se){
            throw new RuntimeException(se);
        }
        return analyses;
    }

    /**
     * Accepts the lemmatized form of a word and returns it's entry
     * @param lemma the lemmatized form of a word lemma or any form of the word
     * @return (possibly empty) list of entries found
     */
    public List<LexiconEntry> findLexiconEntry(String lemma){
        final String normalized = Orthography.rectify(lemma.trim());
        final List<LexiconEntry> entries = new ArrayList<LexiconEntry>();
        try {
            if (findLexiconEntries == null){
                findLexiconEntries = conn.prepareStatement("SELECT lemma, ordinality, orthography, endings, gender, pos, definition FROM lexicon WHERE lemma = ?");
            }
            findLexiconEntries.setString(1, normalized);
            final ResultSet result = findLexiconEntries.executeQuery();
            while (result.next()){
                final LexiconEntry entry = new LexiconEntry();
                entry.setKey(result.getString("lemma"));
                entry.setOrdinality(result.getInt("ordinality"));
                entry.setOrthography(result.getString("orthography"));
                entry.setiType(result.getString("endings"));
                entry.setGender(result.getString("gender"));
                entry.setPos(result.getString("pos"));
                entry.setDefinition(result.getString("definition"));
                entries.add(entry);
            }
            result.close();
        } catch (SQLException se){
            throw new RuntimeException(se);
        }
        return entries;
    }


    @Override
    public void writeAnalysis(MorphologyAnalysis analysis) {

        try {
            writeAnalysis.setString(1, analysis.getForm());
            writeAnalysis.setString(2, analysis.getLemma());
            writeAnalysis.setString(3, analysis.getGrammaticalCase());
            writeAnalysis.setString(4, analysis.getDegree());
            writeAnalysis.setString(5, analysis.getGender());
            writeAnalysis.setString(6, analysis.getMood());
            writeAnalysis.setString(7, analysis.getNumber());
            writeAnalysis.setString(8, analysis.getPerson());
            writeAnalysis.setString(9, analysis.getPos());
            writeAnalysis.setString(10, analysis.getTense());
            writeAnalysis.setString(11, analysis.getVoice());
            writeAnalysis.execute();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public void writeLexiconEntry(LexiconEntry entry){

        try {
            writeLexiconEntry.setString(1, entry.getKey());
            writeLexiconEntry.setInt(2, entry.getOrdinality());
            writeLexiconEntry.setString(3, entry.getOrthography());
            writeLexiconEntry.setString(4, entry.getiType());
            writeLexiconEntry.setString(5, entry.getGender());
            writeLexiconEntry.setString(6, entry.getPos());
            writeLexiconEntry.setString(7, entry.getDefinition());
            writeLexiconEntry.execute();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
