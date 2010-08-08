package org.vafer.lzo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;

import junit.framework.TestCase;

import org.apache.commons.io.IOUtils;

public final class LzoIndexerTestCase extends TestCase {

    public void testShouldCreateIndex() throws IOException, DataFormatException {

        String[] resources = new String[] { "100.txt.lzo", "1000.txt.lzo", "100000.txt.lzo" };
        for (String resource : resources) {
            InputStream lzo = getClass().getClassLoader().getResourceAsStream(resource);
            InputStream index_expected = getClass().getClassLoader().getResourceAsStream(resource + ".index");

            ByteArrayOutputStream index = new ByteArrayOutputStream();

            LzoIndexer indexer = new LzoIndexer();
            indexer.createIndex(lzo, index);

            index.close();
            lzo.close();

            InputStream index_actual = new ByteArrayInputStream(index.toByteArray());

            IOUtils.contentEquals(index_expected, index_actual);            
        }
     }
}