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

import org.apache.commons.cli.*;
import java.io.File;

/**
 * @author Roger Grantham
 * @since 6/4/11
 */
public class GetOpts {
    private final CommandLineParser parser      = new GnuParser();
    private final HelpFormatter     formatter   = new HelpFormatter();
    private final Options           options     = new Options();

    private String dbDir;
    private String sqlDir;

    public GetOpts() {
        options.addOption("d", "buildDatabase", true, "Builds the SQLite database file from XML sources. The "
                                                          + "argument to this option is the directory to which the "
                                                          +  "file is written.");
        options.addOption("s", "buildSQL", true, "Builds the SQL scripts which can be used to import lexicographic "
                                                    + "and morphology data into a databse. The argument to this option "
                                                    + "is the directory to which these files are to be written");
    }

    public File getDbDir() {
        return checkDirectory(dbDir);
    }

    private static File checkDirectory(String dir){
        final File file = dir == null ? null : new File(dir);
        if (file != null && (!file.isDirectory() || !file.canWrite())){
            throw new RuntimeException(String.format("%s is not a writeable directory.", file.getAbsolutePath()));               
        }
        return file;
    }

    public File getSqlDir() {
        return checkDirectory(sqlDir);
    }

    public void parse(String[] args){
        try {
            final CommandLine cmd = parser.parse(options,  args);
            dbDir = cmd.getOptionValue("d");
            sqlDir = cmd.getOptionValue("s");
        } catch (ParseException e) {
            formatter.printHelp(" ", options);
            throw new RuntimeException(e);
        }
    }
}
