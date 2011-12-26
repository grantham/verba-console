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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * A single word may have zero or more morphological entries with zero or more lemmas. We wish to describe
 * each morphological form the word can map to and relate each lemma to zero or more lexicographical entries.
 *
 * @author Roger Grantham
 * @since May 30, 2011
 */
public class QueryResult {

    final List<MorphologyAnalysis> morphologyicalAnalyses = new ArrayList<MorphologyAnalysis>();
    final List<LexiconEntry>       lexiconEntries         = new ArrayList<LexiconEntry>();

    public QueryResult() {
    }


    public void addMorphologicalAnalysis(List<MorphologyAnalysis> analyses){
        morphologyicalAnalyses.addAll(analyses);
    }

    public void addLexicographicalEntry(List<LexiconEntry> entries){
        lexiconEntries.addAll(entries);
    }


    public Set<String> getUniqueLemmas(){
        final Set<String> uniques = new TreeSet<String>();
        for (MorphologyAnalysis analysis: morphologyicalAnalyses){
            uniques.add(analysis.getLemma());
        }
        return uniques;
    }


    public String formatResult(ResultFormatter formatter){
        final StringBuilder sb = new StringBuilder();
        for (String lemma: getUniqueLemmas()){
            sb.append(formatter.format(findAnalysisForLemma(lemma), findEntriesMatchingLemma(lemma)));
        }
        return sb.toString();
    }


    private List<MorphologyAnalysis> findAnalysisForLemma(String lemma){
        final List<MorphologyAnalysis> matches = new ArrayList<MorphologyAnalysis>();
        for (MorphologyAnalysis analysis: morphologyicalAnalyses){
            if (analysis.getLemma().equals(lemma)){
                matches.add(analysis);
            }
        }
        return matches;
    }

    private List<LexiconEntry> findEntriesMatchingLemma(String lemma){
        final List<LexiconEntry> matches = new ArrayList<LexiconEntry>();
        for (LexiconEntry entry: lexiconEntries){
            if (entry.getKey().equals(lemma)){
                matches.add(entry);
            }
        }
        return matches;
    }

}
