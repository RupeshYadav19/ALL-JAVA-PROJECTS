package Hotel.Management.System;

import javax.swing.table.DefaultTableModel;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Vector;

public class TableHelper {
    public static DefaultTableModel resultSetToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            Vector<String> columnNames = new Vector<>();

            // Get the column names
            for (int column = 1; column <= numberOfColumns; column++) {
                columnNames.add(metaData.getColumnLabel(column));
            }

            // Get all rows
            Vector<Vector<Object>> rows = new Vector<>();
            while (rs.next()) {
                Vector<Object> newRow = new Vector<>();
                for (int i = 1; i <= numberOfColumns; i++) {
                    newRow.add(rs.getObject(i));
                }
                rows.add(newRow);
            }

            return new DefaultTableModel(rows, columnNames);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
