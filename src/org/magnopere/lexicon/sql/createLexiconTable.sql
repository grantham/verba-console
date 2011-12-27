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
CREATE TABLE lexicon
(_id INTEGER PRIMARY KEY AUTOINCREMENT,
 lemma VARCHAR(250),
 ordinality INTEGER,
 orthography VARCHAR(250),
 endings VARCHAR (250),
 gender VARCHAR (4)  CHECK (gender in ("", "fem", "masc", "neut")),
 pos VARCHAR (10)  CHECK (pos in ("", "adj", "adv", "adverbial", "conj", "exclam", "noun", "numeral", "part", "prep", "pron", "verb", "dep")),
 definition VARCHAR (1000)
);

CREATE INDEX lex_lemma_index ON lexicon (lemma);
CREATE INDEX lex_pos_index ON lexicon (pos);

