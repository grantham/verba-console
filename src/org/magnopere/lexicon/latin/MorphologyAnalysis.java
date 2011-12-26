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

import static org.magnopere.lexicon.latin.Orthography.*;

/**
 *
 * @author Roger Grantham
 * @since May 29, 2011
 */

public class MorphologyAnalysis {
    private String form = "";
    private String lemma = "";

    private Case grammaticalCase;
    private Degree degree;
    private Gender gender;
    private Mood mood;
    private Number number;
    private Person person;
    private POS pos;
    private Tense tense;
    private Voice voice;

    public String getDegree() {
        return get(degree);
    }

    private String get(Object o){
        return o == null ? "" : o.toString();
    }

    public String getForm() {
        return form;
    }

    public String getGender() {
        return get(gender);
    }

    public String getGrammaticalCase() {
        return get(grammaticalCase);
    }

    public String getLemma() {
        return lemma;
    }

    public String getMood() {
        return get(mood);
    }

    public String getNumber() {
        return get(number);
    }

    public String getPerson() {
        return get(person);
    }

    public String getPos() {
        return get(pos);
    }

    public String getTense() {
        return get(tense);
    }

    public String getVoice() {
        return get(voice);
    }

    public void setDegree(String degree) {
        if (degree != null && !degree.isEmpty()){
            this.degree = Degree.valueOf(degree);
        }
    }

    public void setForm(String form) {
        this.form = rectify(form);
    }

    public void setGender(String gender) {
        if (gender != null && !gender.isEmpty()){
            this.gender = Gender.valueOf(gender);
        }
    }

    public void setGrammaticalCase(String grammaticalCase) {
        if (grammaticalCase != null && !grammaticalCase.isEmpty()){
            this.grammaticalCase = Case.valueOf(grammaticalCase);
        }
    }

    public void setLemma(String lemma) {
        this.lemma = rectify(lemma.replaceAll("#.*?$", ""));
    }

    public void setMood(String mood) {
        if (mood != null && !mood.isEmpty()){
            this.mood = Mood.valueOf(mood);
        }
    }

    public void setNumber(String number) {
        if (number != null && !number.isEmpty()){
            this.number = Number.valueOf(number);
        }
    }

    public void setPerson(String person) {
         if (person != null && !person.isEmpty()){
             this.person = Person.fromString(person);
         }
    }

    public void setPos(String pos) {
        if (pos != null && !pos.isEmpty()){
            this.pos = POS.valueOf(pos);
        }
    }

    public void setTense(String tense) {
        if (tense != null && !tense.isEmpty()){
            this.tense = Tense.valueOf(tense);
        }
    }

    public void setVoice(String voice) {
        if (voice != null && !voice.isEmpty()){
            this.voice = Voice.valueOf(voice);
        }
    }

    @Override
    public String toString() {
        return "MorphologyAnalysis{" +
                "form='" + form + '\'' +
                ", lemma='" + lemma + '\'' +
                ", grammaticalCase=" + grammaticalCase +
                ", degree=" + degree +
                ", gender=" + gender +
                ", mood=" + mood +
                ", number=" + number +
                ", person=" + person +
                ", pos=" + pos +
                ", tense=" + tense +
                ", voice=" + voice +
                '}';
    }

    /* ============================================================================ */
    /* ============================================================================ */
    /*                       ENUMS                                                  */
    /* ============================================================================ */
    /* ============================================================================ */

    public enum Case{
        abl, acc, dat, gen, nom, voc
    }

    public enum Degree {
        degree, comp, superl
    }

    public enum Gender {
        fem, masc, neut
    }

    public enum Mood {
        gerundive, imperat, ind, inf, part, subj, supine
    }

    public enum Number {
        pl, sg
    }

    public enum Person {
        first("1st"),
        second("2nd"),
        third("3rd");

        private String person;
        Person(String person){
            this.person = person;
        }

        public String getPerson() {
            return person;
        }

        public static Person fromString(String person){
            for (Person p: Person.values()){
                if (p.getPerson().equalsIgnoreCase(person) || p.toString().equalsIgnoreCase(person)){
                    return p;
                }
            }
            throw new IllegalArgumentException("Unkown person: " + person);
        }
    }

    public enum POS {
     adj, adv, adverbial, conj, exclam, noun,numeral,part,prep,pron, verb
    }

    public enum Tense{
     fut, futperf, imperf, perf, plup, pres
    }

    public enum Voice {
        act, pass
    }
}
