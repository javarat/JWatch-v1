import java.awt.FontMetrics;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

//will resize columns
public class ColumnResizer {
	
	public static void resize(JTable clientList, DefaultTableModel model, TableModelEvent e) {
		if(e.getColumn() != 2) { //if it's not country flags..check and possibly update.
			String line;
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment( JLabel.CENTER );
			for(int i = 0; i < model.getColumnCount(); i++) {
				int width = 0;
				for(int j = 0; j < model.getRowCount(); j++) {
					System.out.println("COLUMN NUMBER IS: " + e.getColumn());
					line = (String)model.getValueAt(j, i);
					System.out.println("line " + line + "WIDTH: " + width);
					FontMetrics metrics = clientList.getFontMetrics(clientList.getFont());
					int temp = metrics.stringWidth(line);
					if(temp > width) {
						width = temp;
					}
				}
				if(width > 90) {
					clientList.getColumnModel().getColumn(i).setPreferredWidth(width + 5);
				}
				clientList.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			}
		}
	}
}


