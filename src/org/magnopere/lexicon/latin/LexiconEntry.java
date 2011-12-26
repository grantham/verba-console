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
 * A poorly-typed bag to collect values for each entry from the XML
 * @author Roger Grantham
 * @since May 29, 2011
 */
public class LexiconEntry implements Comparable {
    private static final String NOUN = "noun";

    private String key         = "";
    private int    ordinality  = 0;
    private String orthography = "";
    private StringBuilder definition  = new StringBuilder();
    private String iType       = "";
    private String pos         = "";
    private String gender      = "";

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof LexiconEntry)){
            throw new ClassCastException("Expected " + getClass().getName() + " found " + o.getClass().getName());
        }
        final LexiconEntry rec = (LexiconEntry)o;
        final int keyCompare = getKey().compareTo(rec.getKey());
        if (keyCompare == 0){
            return ((Integer)getOrdinality()).compareTo(rec.getOrdinality());
        } else {
            return keyCompare;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LexiconEntry that = (LexiconEntry) o;

        if (ordinality != that.ordinality) return false;
        if (iType != null ? !iType.equals(that.iType) : that.iType != null) return false;
        if (key != null ? !key.equals(that.key) : that.key != null) return false;
        if (orthography != null ? !orthography.equals(that.orthography) : that.orthography != null) return false;

        return true;
    }

    public String getDefinition() {
        return definition.toString().replaceAll("\\s\\s+", " ");
    }

    public String getGender() {
        return gender;
    }

    // Parts of speech found: [prep] [adj] [dep] [adv]
    // Genders found: [f, n, m]

    public String getKey() {
        return key;
    }

    public int getOrdinality() {
        return ordinality;
    }

    public void setOrdinality(int ordinality) {
        this.ordinality = ordinality;
    }

    public String getOrthography() {
        return orthography;
    }

    public String getPos() {
        return pos;
    }

    public String getiType() {
        return iType;
    }

    @Override
    public int hashCode() {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + ordinality;
        result = 31 * result + (orthography != null ? orthography.hashCode() : 0);
        result = 31 * result + (iType != null ? iType.hashCode() : 0);
        return result;
    }

    public void setDefinition(String definition) {
        if (this.definition.length() == 0){
            this.definition.append(prune(definition));
        } else {
            this.definition.append("; ").append(prune(definition));
        }
    }
    
    public LexiconEntry appendToDefinition(String def){
        this.definition.append(def);
        return this;
    }
    

    /**
     * Trims the string and minimizes whitespace
     * @param text to prune
     * @return the pruned string
     */
    private static String prune(String text){
        return text.trim().replaceAll("\\s+", " ");
    }

    public void setGender(String gender) {
        //"fem", "masc", "neut"
        String g = stripNonAlpha(gender);
        if ("m".equals(g)){
            this.gender = "masc";
        } else if ("f".equals(g)){
            this.gender = "fem";
        } else if ("n".equals(g)){
            this.gender = "neut";           
        } else if (gender.equals("masc") || gender.equals("fem") || gender.equals("neut")){
            this.gender = gender;
        } else if (gender.isEmpty()) {
            this.gender = "";
        } else {
            throw new IllegalArgumentException("Unknown gender: " + g);
        }
        setPos(NOUN);
    }

    private static String stripNonAlpha(String text){
        return text.replaceAll("[^a-zA-Z]+", "");
    }

    public void setPos(String pos) {
        this.pos = stripNonAlpha(pos);
    }

    public void setKey(String key) {
        this.key = rectify(key);
    }

    public void setOrthography(String orthography) {
        this.orthography = rectify(prune(orthography));
    }

    public void setiType(String iType) {
        this.iType = rectify(prune(iType)).replaceFirst(",$", "");
    }

    @Override
    public String toString() {
        return "LexiconEntry{" +
                "key='" + key + '\'' +
                ", ordinality=" + ordinality +
                ", orthography='" + orthography + '\'' +
                ", iType='" + iType + '\'' +
                ", pos='" + pos + '\'' +
                ", gender='" + gender + '\'' +
                ", definition='" + getDefinition() + '\'' +
                '}';
    }
}
