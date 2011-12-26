-- The contents of this file are subject to the Mozilla Public License
-- Version 1.1 (the "License"); you may not use this file except in
-- compliance with the License. You may obtain a copy of the License at
-- http://www.mozilla.org/MPL/
--
-- Software distributed under the License is distributed on an "AS IS"
-- basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
-- License for the specific language governing rights and limitations
-- under the License.
--
-- The Initial Developer of the Original Code is Roger Grantham.
-- Portions created by Roger Grantham are
-- Copyright (C) $today.year. All Rights Reserved.
--
-- Contributor(s): Roger Grantham
--
-- Alternatively, the contents of this file may be used under the terms
-- of the GNU Public License license (the  "[GPL] License"), in which case the
-- provisions of [GPL] License are applicable instead of those
-- above.  If you wish to allow use of your version of this file only
-- under the terms of the [GPL] License and not to allow others to use
-- your version of this file under the MPL, indicate your decision by
-- deleting  the provisions above and replace  them with the notice and
-- other provisions required by the [GPL] License.  If you do not delete
-- the provisions above, a recipient may use your version of this file
-- under either the MPL or the [GPL] License.
CREATE TABLE morphology
(form VARCHAR(250),
 lemma VARCHAR(250),
 grammaticalCase VARCHAR(20) CHECK (grammaticalCase in ("", "abl", "acc", "dat", "gen", "nom", "voc")),
 degree  VARCHAR(20) CHECK (degree in ("", "degree", "comp", "superl")),
 gender  VARCHAR(4) CHECK (gender in ("", "fem", "masc", "neut")),
 mood  VARCHAR(20)  CHECK (mood in ("", "gerundive", "imperat", "ind", "inf", "part", "subj", "supine")),
 number  VARCHAR(20) CHECK (number in ("", "pl", "sg")),
 person  VARCHAR(20) CHECK (person in ("", "first", "second", "third")),
 pos  VARCHAR(20) CHECK (pos in ("", "adj", "adv", "adverbial", "conj", "exclam", "noun", "numeral", "part", "prep", "pron", "verb")),
 tense  VARCHAR(20) CHECK (tense in ("", "fut", "futperf", "imperf", "perf", "plup", "pres")),
 voice  VARCHAR(20) CHECK (voice in ("", "act", "pass"))
);

CREATE INDEX form_lemma_index ON morphology (form, lemma);
CREATE INDEX form_index ON morphology (form);
CREATE INDEX lemma_index ON morphology (lemma);

