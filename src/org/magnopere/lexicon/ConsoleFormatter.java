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

package org.magnopere.lexicon;

import java.util.List;

import org.magnopere.lexicon.latin.LexiconEntry;
import org.magnopere.lexicon.latin.MorphologyAnalysis;
import org.magnopere.lexicon.sql.ResultFormatter;

/**
 * @author Roger Grantham
 * @since May 30, 2011
 */
public class ConsoleFormatter implements ResultFormatter {

	private static final String ENDL = System.getProperty("line.separator");
	
    @Override
    public String format(List<MorphologyAnalysis> analyses, List<LexiconEntry> entries) {
        final StringBuilder sb = new StringBuilder();
        sb.append(formatAnalyses(analyses));
        sb.append(formatEntries(entries));
        sb.append(ENDL);
        return sb.toString();
    }

    private String formatAnalyses(List<MorphologyAnalysis> analyses){
        final StringBuilder sb = new StringBuilder();
        if (analyses.size() > 0){
            final String form = analyses.get(0).getForm();
            final String lemma = analyses.get(0).getLemma();
            sb.append(String.format("%s (%s):%n", form, lemma));
            for (MorphologyAnalysis analysis: analyses){

                final String pos        = analysis.getPos();
                final String  person    = analysis.getPerson();
                final String gramCase   = analysis.getGrammaticalCase();
                final String gender     = analysis.getGender();
                final String degree     = analysis.getDegree();
                final String number     = analysis.getNumber();
                final String tense      = analysis.getTense();
                final String mood       = analysis.getMood();
                final String voice      = analysis.getVoice();

                sb.append(String.format(
                        "\t- %s%s%s%s%s%s%s%s%s%n",
                        pos.isEmpty() ? pos : pos + " ",
                        person.isEmpty() ? person : person + " person ",
                        gramCase.isEmpty() ? gramCase : gramCase + " ",
                        gender.isEmpty() ? gender : gender + " ",
                        degree.isEmpty() ? degree : degree + " ",
                        number.isEmpty() ? number : number + " ",
                        tense.isEmpty() ? tense : tense + " ",
                        mood.isEmpty() ? mood : mood + " ",
                        voice.isEmpty() ? voice : voice + " "));
            }
        }
        return sb.toString();
    }

    private String formatEntries(List<LexiconEntry> entries){
        final StringBuilder sb = new StringBuilder();
        if (entries.size() > 0){
            for (int i = 0; i < entries.size(); i++){
                final LexiconEntry entry = entries.get(i);
                sb.append(String.format("%d. %s, %s; %s %s%n\t%s%n",
                        i + 1,
                        entry.getKey(),
                        entry.getiType(),
                        entry.getPos(),
                        entry.getGender(),
                        formatDefinition(entry.getDefinition())));
            }
        }
        return sb.toString();
    }
    
    private String formatDefinition(String def){
        // TODO: use ANSI escapes to set a color or style in the terminal?
        return def.replaceAll("<em>", "").replaceAll("</em>", "");
    }

}
