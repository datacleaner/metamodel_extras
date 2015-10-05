package org.eobjects.metamodel.sas;

import java.util.*;

/**
 * A SasReaderCallback that just collects the values it is given.
 */
public class CollectingSasReaderCallback implements SasReaderCallback {

    public Map<Integer,Object[]> rows = new LinkedHashMap<>();
    public Map<Integer,ColInfo> cols = new LinkedHashMap<>();
    public Map<String,ColInfo> colsByName = new LinkedHashMap<>();

    public static class ColInfo {
        public String columnName, columnLabel;
        public int index;
        public SasColumnType type;

        public ColInfo(int index, String name, String label, SasColumnType type) {
            this.columnLabel = label;
            this.columnName = name;
            this.index = index;
            this.type = type;
        }
    }

    @Override
    public void column(int columnIndex, String columnName, String columnLabel, SasColumnType columnType, int columnLength) {
        ColInfo info = new ColInfo(columnIndex, columnName, columnLabel, columnType);
        cols.put(columnIndex, info);
        colsByName.put(columnName, info);
    }

    @Override
    public boolean readData() {
        return true;
    }

    @Override
    public boolean row(int rowNumber, Object[] rowData) {
        rows.put(rowNumber, rowData);
        //return true;
        return rows.size() < 5;
    }
}
