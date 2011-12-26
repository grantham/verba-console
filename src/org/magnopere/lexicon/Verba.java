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

import java.io.File;
import java.io.IOException;

import jline.ConsoleReader;

import org.magnopere.lexicon.latin.LexiconParser;
import org.magnopere.lexicon.latin.MorphologyParser;
import org.magnopere.lexicon.latin.Slurper;
import org.magnopere.lexicon.sql.DataRepository;
import org.magnopere.lexicon.sql.PersistenceStrategy;
import org.magnopere.lexicon.sql.QueryResult;
import org.magnopere.lexicon.sql.SQLWriter;

/**
 * @author Roger Grantham
 * @since May 30, 2011
 */
public class Verba {

    private static final String NOTICE_PATH = "/org/magnopere/lexicon/notice.txt";
    private static final String PROMPT      = "uerba> ";

    private final DataRepository    repo;
    private final String            notice;
    private final ConsoleFormatter  formatter;
    private final ConsoleReader     consoleReader;

    public Verba(File dbDir) throws IOException {
        if (dbDir == null) dbDir = new File(".");
        repo        = new DataRepository(dbDir.getAbsolutePath());
        notice      = Slurper.slurp(getClass().getResourceAsStream(NOTICE_PATH));
        formatter   = new ConsoleFormatter();
        consoleReader = new ConsoleReader();
        printNotice();
    }

    public static void main(String[] args) throws Exception {
        final GetOpts opts = new GetOpts();
        opts.parse(args);
        final File sqlDir = opts.getSqlDir();
        final File dbDir = opts.getDbDir();
        final Verba verba = new Verba(dbDir);
        if (dbDir != null){
            verba.parseXMLSource(verba.repo);
        } else if (sqlDir != null){
            verba.parseXMLSource(new SQLWriter(sqlDir));
        } else {
            verba.consoleLoop();
        }
    }

    private void parseXMLSource(PersistenceStrategy persister){
        new LexiconParser(persister).parse();
        System.out.println("Finished writing lexicographic information.");
        new MorphologyParser(persister).parse();
        System.out.println("Finished writing morphology information.");
        try {
            persister.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void printNotice(){
        System.out.printf("%s%n", notice);
    }

    public void consoleLoop() throws IOException {
        while (true){
            final String input = consoleReader.readLine(PROMPT);
            if (":quit".equalsIgnoreCase(input) || ":q".equalsIgnoreCase(input)){
                break;
            } else {
                //dispatch query
                final String[] words = input.split("\\s+");
                for (String form: words){
                    QueryResult result = repo.lookup(form);
                    System.out.printf("%n%s%n%n", result.formatResult(formatter));
                }
            }
        }
    }


}
