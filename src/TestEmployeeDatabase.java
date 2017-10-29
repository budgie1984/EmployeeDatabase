import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestEmployeeDatabase {

	Connection con;
	GUI gui;
	ResultSet rs;
	
	@Before
	public void before() {
		gui = new GUI();
	
		
		con = gui.connectToDatabase();
	}

	@After
	public void tearDown() throws Exception {
		if(!con.isClosed()){
			con.close();
		}
	}

	
	@Test
	public void testClearFields() {
		gui.clearFields();
		assertTrue(gui.textField_name.getText().equals(""));
		assertFalse(gui.textField_name.getText().equals("Brian"));
	}
	
	@Test
	public void testPreviousRecord() throws SQLException {
		gui.getPreviousRecord();
		assertFalse(gui.textField_name.getText().equals(""));
		assertTrue(gui.textField_name.getText() != "");
	}
	@Test
	public void testNextRecord() throws SQLException {
		gui.getNextRecord();
		assertFalse(gui.textField_name.getText().equals(""));
		assertTrue(gui.textField_name.getText() != "");

	}

	
	
	


}
