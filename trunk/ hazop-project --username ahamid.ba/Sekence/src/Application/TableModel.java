package Application;
import java.awt.Point;
import java.util.Hashtable;
import javax.swing.table.AbstractTableModel;



@SuppressWarnings("serial")
public class TableModel extends AbstractTableModel
{
	  @SuppressWarnings("unchecked")
	  private Hashtable lookup;

	  private int rows;

	  private final int columns;

	  private final String headers[];

	  public TableModel(int rows, String columnHeaders[]) 
	  {
	    if ((rows < 0) || (columnHeaders == null)) 
	    {
	      throw new IllegalArgumentException(
	          "Invalid row count/columnHeaders");
	    }
	    
	    this.rows = rows;
	    this.columns = columnHeaders.length;
	    headers = columnHeaders;
	    lookup = new Hashtable();
	  }

	  public int getColumnCount() {
	    return columns;
	  }
	  
	  public int getRowCount() {
	    return rows;
	  }

	  public String getColumnName(int column) {
	    return headers[column];
	  }
	  

	  public Object getValueAt(int row, int column) {
	    return lookup.get(new Point(row, column));
	  }

	  public void setValueAt(Object value, int row, int column) {
	    if ((rows < 0) || (columns < 0)) {
	      throw new IllegalArgumentException("Invalid row/column setting");
	    }
	    if ((row < rows) && (column < columns)) {
	      lookup.put(new Point(row, column), value);
	    }
	    else
	    {
	    	if(row >= rows)
	    	{
	    		ajouterLigne(row);
	    		lookup.put(new Point(row, column), value);
	    	}
	    }
	  }
	  
	  public boolean isCellEditable (int row, int col)
	  {
		  return true; 
	  }

	  
	  public void ajouterLigne(int index)
	  {
		  int nbLigne = index - rows;
		  this.rows += nbLigne+1;
		  this.setValueAt("test", index, 0);
	  }
	  
	  public void supprimerLigne(int index)
	  {
		  //A FAIRE 
	  }

}
