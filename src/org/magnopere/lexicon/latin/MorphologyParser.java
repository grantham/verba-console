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
import java.util.Stack;
import java.util.logging.Logger;

/**
 * @author Roger Grantham
 * @since May 29, 2011
 */
public class MorphologyParser  extends DefaultHandler implements RecordStack.RepositoryListener<MorphologyAnalysis> {
    private static final String LEXICON_PATH = "/org/perseus/lexicon/latin.morph.xml";
    private static final Logger LOG = Logger.getLogger(MorphologyParser.class.getName());
    
    private final InputStream lexicon;

    private final Stack<Elements> elementStack = new Stack<Elements>();

    private final RecordStack<MorphologyAnalysis> recordStack;

    private final PersistenceStrategy persister;

    private int recordCount;


    /**
     * New Instance
     * @param persister persistent data repository
     */
    public MorphologyParser(PersistenceStrategy persister) {
        if (persister == null) throw new IllegalArgumentException("null: persister");
        this.persister = persister;
        persister.buildMorphologyTable();
        lexicon = getClass().getResourceAsStream(LEXICON_PATH);
        recordStack = new RecordStack<MorphologyAnalysis>();
        recordStack.addListener(this);
    }


    /**
     * Performs the actual lexicon parsing
     */
    public void parse(){
        final SAXParserFactory factory = SAXParserFactory.newInstance();
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
    public void peekCalled(MorphologyAnalysis rec) {

    }

    @Override
    public void popCalled(MorphologyAnalysis rec) {
        recordCount++;
        if (recordCount % 2000 == 0){
            LOG.info(String.format("Wrote %d morphology records. Last record: %n%s", recordCount, rec));
        }
        persister.writeAnalysis(rec);
    }

    @Override
    public void pushCalled(MorphologyAnalysis rec) {

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        final Elements handler = Elements.fromName(qName);
        elementStack.push(handler);
        handler.open(recordStack, attributes);
    }

    private enum Elements {
        ANALYSIS("analysis"){
            @Override
            void open(RecordStack<MorphologyAnalysis> stack, Attributes attributes) {
                stack.push(new MorphologyAnalysis());
            }

            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.pop();
            }
        },
        CASE("case"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setGrammaticalCase(getPcData());
            }
        },
        DEGREE("degree"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setDegree(getPcData());
            }
        },
        FORM("form"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setForm(getPcData());
            }
        },
        GENDER("gender"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setGender(getPcData());
            }
        },
        LEMMA("lemma"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setLemma(getPcData());
            }
        },
        MOOD("mood"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setMood(getPcData());
            }
        },
        NUMBER("number"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setNumber(getPcData());
            }
        },
        PERSON("person"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setPerson(getPcData());
            }
        },
        POS("pos"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setPos(getPcData());
            }
        },
        TENSE("tense"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setTense(getPcData());
            }
        },
        VOICE("voice"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack){
                stack.peek().setVoice(getPcData());
            }
        },

          UNHANDLED("unhandled"){
            @Override
            void close(RecordStack<MorphologyAnalysis> stack) {
            }
          };


        private final String name;
        private StringBuilder       pcData = new StringBuilder();

        Elements(String name){
            this.name = name;
        }

        void open(RecordStack<MorphologyAnalysis> stack, Attributes attributes){};

        abstract void close(RecordStack<MorphologyAnalysis> stack);

        void pcData(char[] ch, int start, int length){
            pcData.append(ch, start, length);
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
