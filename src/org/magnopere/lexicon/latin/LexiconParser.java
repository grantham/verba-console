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

import org.magnopere.lexicon.sql.DataRepository;
import org.magnopere.lexicon.sql.PersistenceStrategy;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

/**
 *
 * <entry id="n13" type="main" key="abdico2" n="2">
 *     <form>
 *       <orth extent="full" lang="la">ab-d&imacr;c&omacr;</orth>
 *     </form>
 *     <gramGrp>
 *       <itype> d&imacr;x&imacr;, &mdash;, ere, in augury, </itype>
 *     </gramGrp>
 *     <sense id="n13.0" level="0" n="0">
 *       <trans>
 *         <tr>to forbid by an unfavorable omen, reject (opp. addico)</tr>
 *       </trans>, C.
 *     </sense>
 * </entry>
 * @author Roger Grantham
 * @since May 29, 2011
 */
public class LexiconParser extends DefaultHandler implements RecordStack.RepositoryListener<LexiconEntry> {
    private static final String LEXICON_PATH = "/org/perseus/lexicon/lewis.xml";
    private static final Logger LOG = Logger.getLogger(LexiconParser.class.getName());
    
    private final InputStream lexicon;

    private final Stack<Elements> elementStack = new Stack<Elements>();

    private final RecordStack<LexiconEntry> recordStack;

    private final PersistenceStrategy persister;

    private int recordCount = 0;


    /**
     * New Instance
     * @param persister persistent data repository
     */
    public LexiconParser(PersistenceStrategy persister) {
        if (persister == null) throw new IllegalArgumentException("null: persister");
        this.persister = persister;
        persister.buildLexiconTable();
        lexicon = getClass().getResourceAsStream(LEXICON_PATH);
        recordStack = new RecordStack<LexiconEntry>();
        recordStack.addListener(this);
    }


    /**
     * Performs the actual lexicon parsing
     */
    public void parse(){
        final SAXParserFactory factory = SAXParserFactory.newInstance();
        factory.setValidating(false);
        try {
            final SAXParser saxParser = factory.newSAXParser();
            final InputSource source = new InputSource(lexicon);
            source.setEncoding("UTF-8");
            saxParser.parse(source, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        elementStack.peek().pcData(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        final Elements handler = elementStack.pop();
        handler.close(recordStack);
    }

    public int getRecordCount() {
        return recordCount;
    }

    @Override
    public void peekCalled(LexiconEntry rec) {

    }

    @Override
    public void popCalled(LexiconEntry rec) {
        recordCount++;
        if (recordCount % 2000 == 0){
            LOG.info(String.format("Wrote %d lexicon records. Last record: %n%s", recordCount, rec));
        }
        persister.writeLexiconEntry(rec);
    }

    @Override
    public void pushCalled(LexiconEntry rec) {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final Elements handler = Elements.fromName(qName);
        if (handler.flushBeforeOpen()){
            recordStack.peek().appendToDefinition(elementStack.peek().getPcData());
        }
        elementStack.push(handler);
        handler.open(recordStack, attributes);
    }


    private static enum Elements {
        ENTRY("entry", false){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes) {
                final LexiconEntry rec = new LexiconEntry();
                final String keyVal = attributes.getValue("key");
                if (keyVal == null){
                   final StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < attributes.getLength(); i++){
                        sb.append(String.format("%s (qname %s): %s; ", attributes.getLocalName(i), attributes.getQName(i), attributes.getValue(i)));
                    }
                    throw new IllegalArgumentException("Bad entry record: " + sb.toString());
                } else {
                    String key = keyVal.split("\\d+$")[0];
                    final String[] ordinalVal = keyVal.split("^[^\\d]+");
                    if (ordinalVal.length > 0){
                        rec.setOrdinality(Integer.valueOf(ordinalVal[1]));
                    }
                    rec.setKey(key);
                }
                stack.push(rec);
            }

            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.pop();
            }},
        ORTH("orth", false){
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().setOrthography(getPcData());
            }},
        ITYPE("itype", false){
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().setiType(getPcData());
            }},
        POS("pos", false){
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().setPos(getPcData());
            }},
        GEN("gen", false){
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().setGender(getPcData());
            }},
        //<trans><tr>from, away from, out of</tr></trans>  
        TRANS("trans", true){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){
                stack.peek().appendToDefinition(getPcData());
            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().appendToDefinition(getPcData());
            }},
        TR("tr", true){
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().appendToDefinition(getPcData());
            }},
        //emph   <emph>pron</emph>
        EM("emph", true){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){
                stack.peek().appendToDefinition(getPcData()).appendToDefinition("<em>");
            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                 stack.peek().appendToDefinition(getPcData()).appendToDefinition("</em>");
            }
        },

        //etym  <etym lang="la">P. of abigo</etym>
        ETYM("etym", true){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){
                stack.peek().appendToDefinition(getPcData()).appendToDefinition("<em>");

            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().appendToDefinition(getPcData()).appendToDefinition("</em>");
            }

        },
        //foreign <foreign lang="la">laborare ab re frumentari&amacr;</foreign>
        FOREIGN("foreign", true){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){
                stack.peek().appendToDefinition(getPcData()).appendToDefinition("<em>");
            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().appendToDefinition(getPcData()).appendToDefinition("</em>");
            }

        },
        //form  <form><orth extent="full" lang="la"> A. a.</orth></form>
        FORM("form", false){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){

            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {

            }
        },
        //gramGrp  <gramGrp><itype> as an abbreviation, </itype></gramGrp>
        GRAM_GRP("gramGrp", false){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){

            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {

            }
        },
        //sense  <sense id="n0.0" level="3" n="1"> for the praenomen Aulus. </sense>
        SENSE("sense", false){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){
                final String senseID = attributes.getValue("n");
                if (!senseID.equals("0")){
                    stack.peek().appendToDefinition(getPcData()).appendToDefinition(String.format("(%s)", attributes.getValue("n")));
                }
            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().appendToDefinition(getPcData());
            }
        },
        //usg   <usg>V.</usg>
        USG("usg", true){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){
               stack.peek().appendToDefinition(getPcData()).appendToDefinition("<em>");
            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {
                stack.peek().appendToDefinition(getPcData()).appendToDefinition("</em>");
            }
        },
        UNHANDLED("unhandled", false){
            @Override
            void open(RecordStack<LexiconEntry> stack, Attributes attributes){

            }
            @Override
            void close(RecordStack<LexiconEntry> stack) {

            }};


        private final String        name;
        private StringBuilder       pcData = new StringBuilder();
        private final boolean       flushBeforeOpen;

        Elements(String name, boolean flushBeforeOpen){
            this.name = name;
            this.flushBeforeOpen = flushBeforeOpen;
        }

        void open(RecordStack<LexiconEntry> stack, Attributes attributes){}

        abstract void close(RecordStack<LexiconEntry> stack);

        void pcData(char[] ch, int start, int length){
            pcData.append(ch, start, length);
        }

        public boolean flushBeforeOpen(){
            return flushBeforeOpen;
        }

        public String getName() {
            return name;
        }

        public String getPcData() {
            final String pcd = pcData.toString();
            pcData.delete(0, pcData.length());
            return pcd;
        }

        public static Elements fromName(String name){
            for (Elements elem: Elements.values()){
                if (elem.getName().equals(name)){
                    return elem;
                }
            }
            return UNHANDLED;
        }
    }
}
