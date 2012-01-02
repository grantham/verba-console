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
 * Copyright (C) 2012. All Rights Reserved.
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

import java.io.*;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Roger Grantham
 * @since 1/1/12
 */
public class Compression {

    private static final int BUF_SIZE = 1024;
    
    /** Not to be implemented */
    private Compression() {
        // no op
    }
    
    private static byte[] compress(String data){
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        BestCompressionGZIPOutputStream gzipOut = null;
        try {
            gzipOut = new BestCompressionGZIPOutputStream(baos);
            gzipOut.write(data.getBytes("UTF-8"));
            gzipOut.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (gzipOut != null) gzipOut.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }    
        return baos.toByteArray();
    }


    /**
     * Accepts a Blob which is excepted to be a GZIPed string and deflates it.
     * @param blob to deflated
     * @return deflated string
     */
    public static String deflateToString(byte[] blob){
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ByteArrayInputStream bais = new ByteArrayInputStream(blob);
        GZIPInputStream gzipIn = null;
        try {
            final byte[] buf = new byte[BUF_SIZE];
            gzipIn = new GZIPInputStream(bais);
            for (int read = gzipIn.read(buf); read > 0; read = gzipIn.read(buf)){
                baos.write(buf, 0, read);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (gzipIn != null) gzipIn.close();
            } catch (IOException e) {
                // give up
            }
        }
        final String deflated;
        try {
            deflated = baos.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return deflated;
    }
    
    
    public static InputStream toCompressedInputStream(String data){
        return new ByteArrayInputStream(compress(data));
    }

    public static String toCompressedHex(String data){
        return toHex(compress(data));
    }
    

    private static String toHex(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            sb.append(String.format("%1$02X", b));
        }
        return sb.toString();
    }


    private static final class BestCompressionGZIPOutputStream extends GZIPOutputStream {

        public BestCompressionGZIPOutputStream(final OutputStream out) throws IOException {
            super(out);
            def.setLevel(Deflater.BEST_COMPRESSION);
        }
    }

}
