/**
 * eobjects.org SassyReader
 * Copyright (C) 2011 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.sassy;

import java.io.File;
import java.util.List;

import junit.framework.TestCase;

import org.apache.metamodel.csv.CsvConfiguration;
import org.apache.metamodel.csv.CsvDataContext;
import org.apache.metamodel.data.DataSet;
import org.apache.metamodel.query.Query;
import org.apache.metamodel.schema.Table;

public class SasReaderTest extends TestCase {

    public void testCharsetMostlyLatin() throws Exception {
        readSas("charset_mostly_latin.sas7bdat", 1, 7,
                createComparisonDataSet("charset_mostly_latin.tsv"));
    }

    /**
     * TODO: Ignored for release
     */
//    public void testReadTestDataSet() throws Exception {
//        readSas(new File("C:/Users/kaspers/Dropbox/Kode/sas/test/test.sas7bdat"), 10, 100, null);
//    }

    /**
     * TODO: Ignored for release
     */
    // public void testCharsetCyrillicAndMore() throws Exception {
    // readSas("charset_cyrillic_and_more.sas7bdat", 1, 7,
    // createComparisonDataSet("charset_cyrillic_and_more.tsv"));
    // }

    public void testReadEvent2() throws Exception {
        readSas("event2.sas7bdat", 9, 1506, null);
    }

    public void testReadMathAttitudes() throws Exception {
        readSas("mathattitudes.sas7bdat", 15, 1907,
                createComparisonDataSet("mathattitudes.tsv"));
    }

    /**
     * TODO: Ignored for release
     */
    // public void testReadPhyseds2006() throws Exception {
    // List<Object[]> sampleRows = readSas("physeds2006.sas7bdat", 232, 51,
    // createComparisonDataSet("physeds2006.tsv"));
    //
    // assertEquals(51, sampleRows.size());
    //
    // assertEquals("Alabama", sampleRows.get(0)[1]);
    // assertEquals("Alaska", sampleRows.get(1)[1]);
    // assertEquals("Arizona", sampleRows.get(2)[1]);
    // assertEquals("Arkansas", sampleRows.get(3)[1]);
    // assertEquals("California", sampleRows.get(4)[1]);
    // assertEquals("Colorado", sampleRows.get(5)[1]);
    // assertEquals("Connecticut", sampleRows.get(6)[1]);
    // assertEquals("Delaware", sampleRows.get(7)[1]);
    // assertEquals("Wyoming", sampleRows.get(50)[1]);
    // }

    public void testReadBeef() throws Exception {
        readSas("beef.sas7bdat", 9, 30, createComparisonDataSet("beef.tsv"));
    }

    public void testReadMammals() throws Exception {
        readSas("mammals.sas7bdat", 31, 30,
                createComparisonDataSet("mammals.tsv"));
    }

    public void testReadPizza() throws Exception {
        readSas("pizza.sas7bdat", 9, 300, createComparisonDataSet("pizza.tsv"));
    }

    private List<Object[]> readSas(String filename, int columns, int rows,
            DataSet compareToDataSet) {
        File file = new File("src/test/resources/" + filename);
        return readSas(file, columns, rows, compareToDataSet);
    }

    private List<Object[]> readSas(File file, int columns, int rows,
            DataSet compareToDataSet) {
        SasReader reader = new SasReader(file);

        CountingSasReaderCallback callback = new CountingSasReaderCallback(
                true, compareToDataSet);
        reader.read(callback);
        assertEquals(columns, callback.getColumnCount());
        assertEquals(rows, callback.getRowCount());

        return callback.getSampleRows();
    }

    private DataSet createComparisonDataSet(String filename) {
        CsvConfiguration configuration = new CsvConfiguration(
                CsvConfiguration.DEFAULT_COLUMN_NAME_LINE, "UTF-8", '\t',
                CsvConfiguration.NOT_A_CHAR, CsvConfiguration.NOT_A_CHAR, true);
        CsvDataContext dc = new CsvDataContext(new File("src/test/resources/"
                + filename), configuration);

        Table table = dc.getDefaultSchema().getTables()[0];
        Query query = dc.query().from(table).select(table.getColumns())
                .toQuery();

        return dc.executeQuery(query);
    }

    public void testIsMagicNumber() throws Exception {
        int[] identical = new int[] { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
                0x0, 0x0, 0x0, 0x0, 0xc2, 0xea, 0x81, 0x60, 0xb3, 0x14, 0x11,
                0xcf, 0xbd, 0x92, 0x8, 0x0, 0x9, 0xc7, 0x31, 0x8c, 0x18, 0x1f,
                0x10, 0x11 };

        int[] diff1 = new int[] { 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0,
                0x0, 0x0, 0x0, 0xc2, 0xea, 0x81, 0x60, 0xb3, 0x14, 0x11, 0xcf,
                0xbd, 0x92, 0x8, 0x0, 0x9, 0xc7, 0x31, 0x8c, 0x18, 0x1f, 0x10,
                0x12 };
        int[] diff2 = new int[] { 0x0, 0x0 };
        int[] diff3 = new int[] {};
        int[] diff4 = null;

        assertTrue(SasReader.isMagicNumber(identical));
        assertFalse(SasReader.isMagicNumber(diff1));
        assertFalse(SasReader.isMagicNumber(diff2));
        assertFalse(SasReader.isMagicNumber(diff3));
        assertFalse(SasReader.isMagicNumber(diff4));
    }
}
