import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import java.math.BigDecimal;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import com.mysql.jdbc.PreparedStatement;

import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.UIManager;

public class GUI {

	JFrame employeeWindow;
	JTextField textField_ssn, textField_name, textField_bdate, textField_sex, textField_address, textField_salary,
			textField_search,textField_worksFor,textField_manages,textField_supervises;

	JLabel lblSsn, lblName, lblBdate, lblSex, lblAddress, lblSalary, lblSearchEmployees,employeeManager,
			lblWorksFor,lblManages,lblSupervises;

	JButton previous, next, goToFirst, goToLast, add, delete, search, update, quickAddRecord, 
			clearFields, listEmployees, createEmployeeTable, deleteAllRecords, dropEmployeeTable,exitProgram;

	private Connection con;
	private ResultSet rs;
	private Statement st;
	
	private int ssn,worksFor,manages,supervises;
	private BigDecimal salary;
	private String name, address;
	private Date bDate;
	private char sex;

	private int updateSsnField;
	ArrayList<String> list = new ArrayList<>(); // stores records to be viewed in list employees method


	private String jdbcDriver = "com.mysql.jdbc.Driver";
	private String dbAddress = "jdbc:mysql://localhost:3306/";
	private String dbName = "EmployeeDatabase";
	private String userName = "root";
	private String password = "";
	
	
	
	/**
	 * Constructor creates the Gui
	 * and Connects to Database at startup.
	 */
	public GUI() {
		buildGuiInterface();
		connectToDatabase();
	}


	
//-----------------METHODS----------------------------//	
	
	/**
	 * Method to connect to given Database
	 * @return con - the connection object
	 */
	public Connection connectToDatabase() {
		try {
			Class.forName(jdbcDriver);
			con = DriverManager.getConnection(dbAddress + dbName, userName, password);
			loadEmployees(); // gives error message at start up if no employees or table exit
		} catch (Exception e) {
			alertMessage("Error connecting to Database", "Error!");

		}
		return con;
	}
	
	/**
	 * method to delete the database and exit
	 * the program
	 */
	private void exitProgram(){
		dropDatabase();
		getEmployeeApp().setVisible(false);
		employeeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		try {
			con.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
	}
	
	/**
	 * Method to Create A Databse
	 * Not used, was trying to create one on startup
	 */
	private void createDatabase(){
		String sql = "CREATE DATABASE EmployeeDatabase";
		runSqlCommand(sql, "Employee Database created");
	}
	
	/**
	 * method to drop the Database
	 * Used in exitProgram() 
	 */
	private void dropDatabase(){
		String sql = "DROP DATABASE EmployeeDatabase";
		runSqlCommand(sql, "couldn't drop database");
	}
	
	/**
	 * Creates Employee Table 
	 * called Employee in database
	 */
	private void createTable(){
		String sql = "CREATE TABLE Employee(Ssn int(100),Bdate DATE NOT NULL,Name varchar(80),Address varchar(160),Salary DECIMAL(65,0),Sex varchar(1),Works_For int(100),Manages int(100),Supervises int(100))";
		runSqlCommand(sql, "Employee table created");
	}
	
	/**
	 * Deletes the Employee table from database
	 */
	private void dropEmployeeTable(){
		String sql = "DROP TABLE Employee";
		runSqlCommand(sql, "Employee table deleted");
	}
	
	/**
	 * loads the employees in database at startup
	 * Used after most actions to reflect changes
	 */
	private void loadEmployees() {
		try {
			st = con.createStatement();
			rs = st.executeQuery("select * from Employee");
			goToFirstRecord();

		} catch (Exception e) {
			alertMessage("Error Loading employees! There may be no employess or Table", "Warning!");
		}
	}


	/**
	 * Executes string queries to update changes to database
	 * used in various methods add,delete etc.. to execute queries
	 * @param query - the given sql query
	 * @param message - displays update action
	 */
	private void runSqlCommand(String sql, String info) {
		try {
			st = con.createStatement();
			st.executeUpdate(sql);
			alertMessage(info, "Info!");
			st.close();

		} catch (Exception e) {
			alertMessage("Error Please try again!", "Warning!");
		}
	}
	
	/**
	 * Adds a manual record i.e those typed into Gui
	 * text fields
	 */
	private void addManualRecord() {

		String sql = "INSERT INTO `Employee` (Ssn, Bdate, Name, Address, Salary, Sex, Works_For, Manages, Supervises) "
				+ "VALUES (?,?,?,?,?,?,?,?,?)";

		try {
			PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
			pst.setString(1, textField_ssn.getText());
			pst.setString(2, textField_bdate.getText());
			pst.setString(3, textField_name.getText());
			pst.setString(4, textField_address.getText());
			pst.setString(5, textField_salary.getText());
			pst.setString(6, textField_sex.getText());
			pst.setString(7, textField_worksFor.getText());
			pst.setString(8, textField_manages.getText());
			pst.setString(9, textField_supervises.getText());

			pst.execute();
			pst.close();
			alertMessage("Employee: " + textField_name.getText() + " Ssn: " + textField_ssn.getText() + " succesfully added","");

		} catch (SQLException e) {
			alertMessage("Error adding record", "Warning!");
		}
		loadEmployees();
	}
	
	/**
	 * Updates a record in the database
	 * can't change SSn or commmand won't work
	 * as changes are based on the Ssn
	 */
	private void updateARecord(){
		String ssn = textField_ssn.getText();
		String bdate = textField_bdate.getText();
		String name = textField_name.getText();
		String address = textField_address.getText();
		String salary = textField_salary.getText();
		String sex = textField_sex.getText();
		String worksFor = textField_worksFor.getText();
		String manages = textField_manages.getText();
		String supervises = textField_supervises.getText();
		
		
		String sql = "update Employee set BDate='"+bdate+"', Name='"+name+ "', "
				+ "Address='"+ address +"', Salary='"+salary +"', Sex='"+sex +"',"
						+ "Works_For='"+worksFor+"', Manages='"+manages+"', Supervises='"+supervises+"' where Ssn='"+ssn+"'";

		runSqlCommand(sql, " Employee updated");
		loadEmployees();
	}

	/**
	 * Method quickly adds a record to database by 
	 * increasing Ssn by 1
	 */
	private void quickAddRecord() {

		String insertQuery = "INSERT INTO `Employee` (Ssn, Bdate, Name, Address, Salary, Sex, Works_For, Manages, Supervises) "
				+ "VALUES (" + updateSsnField + ", '2016-09-06', 'Brian Burroughs', 'Carlow', '100000', 'm', '1', '2', '3');";
		runSqlCommand(insertQuery, "Employee: Brian Burroughs SSn: " + updateSsnField + " has been added!");
		updateSsnField++;
		loadEmployees();
	}

	private void deleteRecord() {
		String deleteQuery = "DELETE FROM Employee WHERE Ssn = " + ssn + ";";
		runSqlCommand(deleteQuery, "Employee " + name + " SSn: " + ssn + " has been deleted!");
		loadEmployees();
	}
	
	private void deleteAllRecords(){
		String sql = "Delete FROM Employee";
		runSqlCommand(sql, "All Records Deleted");
	
		loadEmployees();
		clearFields();
		
	}

	private void searchForEmployee() {
		String findEmployee = textField_search.getText();
		String searchQuery = "SELECT * FROM Employee WHERE name like '%" + findEmployee + "%';";
	
		try {
			st = con.createStatement();
			rs = st.executeQuery(searchQuery);
			goToFirstRecord();
			alertMessage(findEmployee + " found", "Search Employee");
		} catch (Exception e) {
			alertMessage("Employee not found!", "Warning!");
		}
	}

	private void listEmployeesBySsn() {

		String sql = "SELECT * FROM Employee" + " ORDER BY Ssn ASC";
		try {
				st = con.createStatement();
				rs = st.executeQuery(sql);

			while (rs.next()) {

				String ssn = rs.getString("Ssn");
				String Bdate = rs.getString("Bdate");
				String name = rs.getString("Name");
				String address = rs.getString("Address");
				String salary = rs.getString("Salary");
				String sex = rs.getString("Sex");
	
				// Didn't want these fields displaying in the list
				
//				String worksFor = rs.getString("Works_For");
//				String manages = rs.getString("Manages");
//				String supervises = rs.getString("Supervises");
				
				list.add(ssn);
				list.add(Bdate);
				list.add(name);
				list.add(address);
				list.add(salary);
				list.add(sex);
//				list.add(worksFor);
//				list.add(manages);
//				list.add(supervises);
			}
			loadEmployees();
			JScrollPane pane = new JScrollPane(new JList(list.toArray()));
			JOptionPane.showMessageDialog(null, pane);
		
			list.clear(); // 
			

		} catch (Exception e) {
			alertMessage("No Employees found!", "Warning!");
		}
	}
	
	/**
	 * Simple pop up displays information about database actions i.e Successful or Unsuccessful
	 * 
	 * @param infoMessage - information about database action taken
	 * @param titleBar - name of the JOption pop up
	 */
	private void alertMessage(String infoMessage, String titleBar) {
		JOptionPane.showMessageDialog(employeeWindow, infoMessage, "" + titleBar, JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Displays the next record
	 */
	public void getNextRecord() throws SQLException {
	
		if (rs.next()) {
			getTextFields();
		} else {	
			alertMessage("No more records", "Info!");
		}
		setTextFields();
	}
	
	/**
	 * Displays the previous record
	 */
	public void getPreviousRecord() throws SQLException {

		if (rs.previous()) {
			getTextFields();
		} else {
			alertMessage("No previous records", "Info!");
		}
		setTextFields();
	}
	
	/**
	 * Go to first record in database
	 */
	public void goToFirstRecord() {
		try {
			if (rs.first()) {
				getTextFields();
			}
		} catch (Exception e) {
			e.printStackTrace();
			alertMessage("No record found \n please add some", "Warning!");
		}
		setTextFields();
	}
	
	/**
	 * Goes to last record in database
	 */
	public void goToLastRecord() {
		try {
			if (rs.last()) {
				getTextFields();
			}
		} catch (Exception e) {
			e.printStackTrace();
			alertMessage("No record found \n please add some", "Warning!");
		}
		setTextFields();
	}
	
	/**
	 * converts field types to strings
	 * so they can be displayed in text fields
	 */
	public void setTextFields() {
		
		textField_ssn.setText(Integer.toString(ssn));
		textField_bdate.setText(bDate.toString());
		textField_name.setText(name);
		textField_address.setText(address);
		textField_salary.setText(salary.toString());
		textField_sex.setText(Character.toString(sex));
		textField_worksFor.setText(Integer.toString(worksFor));
		textField_manages.setText(Integer.toString(manages));
		textField_supervises.setText(Integer.toString(supervises));
		
	}

	/**
	  * gets releveant data types from table
	  * and stores them in variables
	  */
	public void getTextFields() throws SQLException {
		
		ssn = rs.getInt("Ssn");
		bDate = rs.getDate(2); // second field in database
		name = rs.getString("Name");
		address = rs.getString("Address");
		salary = rs.getBigDecimal(5); // 5th field in database
		sex = rs.getString("Sex").charAt(0); // first character of female field -female put in will show f
		worksFor = rs.getInt("Works_For");
		manages = rs.getInt("Manages");
		supervises = rs.getInt("Supervises");
		
	}
	
	/**
	 * Removes text from text fields
	 */
	public void clearFields() {
		textField_ssn.setText("");
		textField_bdate.setText("");
		textField_name.setText("");
		textField_address.setText("");
		textField_salary.setText("");
		textField_sex.setText("");
		textField_search.setText("");
		textField_worksFor.setText("");
		textField_manages.setText("");
		textField_supervises.setText("");
	}


	// ---------Graphical User Interface-----------------------

	private void buildGuiInterface() {
		employeeWindow = new JFrame();
		employeeWindow.getContentPane().setForeground(Color.WHITE);
		employeeWindow.setBackground(Color.WHITE);
		employeeWindow.getContentPane().setBackground(UIManager.getColor("List.dropLineColor"));
		employeeWindow.getContentPane().setLayout(null);
		employeeWindow.setTitle("Employee Management System");
		employeeWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		employeeWindow.setBounds(100, 100, 628, 592);

		employeeManager = new JLabel("Employee Details");
		employeeManager.setForeground(Color.WHITE);
		employeeManager.setFont(new Font("Dialog", Font.BOLD, 18));
		employeeManager.setBounds(193, 24, 206, 22);
		employeeWindow.getContentPane().add(employeeManager);

		lblSsn = new JLabel("SSN");
		lblSsn.setForeground(Color.WHITE);
		lblSsn.setBounds(92, 141, 36, 15);
		employeeWindow.getContentPane().add(lblSsn);

		textField_ssn = new JTextField();
		textField_ssn.setForeground(Color.RED);
		textField_ssn.setBackground(new Color(255, 255, 204));
		textField_ssn.setBounds(127, 139, 114, 19);
		employeeWindow.getContentPane().add(textField_ssn);
		textField_ssn.setColumns(10);

		lblName = new JLabel("NAME");
		lblName.setForeground(Color.WHITE);
		lblName.setBounds(81, 73, 47, 15);
		employeeWindow.getContentPane().add(lblName);

		textField_name = new JTextField();
		textField_name.setForeground(Color.RED);
		textField_name.setBackground(new Color(255, 255, 204));
		textField_name.setBounds(127, 71, 206, 19);
		employeeWindow.getContentPane().add(textField_name);
		textField_name.setColumns(10);

		lblBdate = new JLabel("BDATE");
		lblBdate.setForeground(Color.WHITE);
		lblBdate.setBounds(308, 170, 47, 15);
		employeeWindow.getContentPane().add(lblBdate);

		textField_bdate = new JTextField();
		textField_bdate.setForeground(Color.RED);
		textField_bdate.setBackground(new Color(255, 255, 204));
		textField_bdate.setBounds(357, 168, 114, 19);
		employeeWindow.getContentPane().add(textField_bdate);
		textField_bdate.setColumns(10);

		lblSex = new JLabel("SEX");
		lblSex.setForeground(Color.WHITE);
		lblSex.setBounds(92, 170, 36, 15);
		employeeWindow.getContentPane().add(lblSex);

		textField_sex = new JTextField();
		textField_sex.setForeground(Color.RED);
		textField_sex.setBackground(new Color(255, 255, 204));
		textField_sex.setBounds(127, 168, 36, 19);
		employeeWindow.getContentPane().add(textField_sex);
		textField_sex.setColumns(10);

		lblAddress = new JLabel("ADDRESS");
		lblAddress.setForeground(Color.WHITE);
		lblAddress.setBounds(58, 102, 70, 15);
		employeeWindow.getContentPane().add(lblAddress);

		textField_address = new JTextField();
		textField_address.setForeground(Color.RED);
		textField_address.setBackground(new Color(255, 255, 204));
		textField_address.setBounds(127, 100, 272, 19);
		employeeWindow.getContentPane().add(textField_address);
		textField_address.setColumns(10);

		lblSalary = new JLabel("SALARY");
		lblSalary.setForeground(Color.WHITE);
		lblSalary.setBounds(303, 141, 52, 15);
		employeeWindow.getContentPane().add(lblSalary);

		textField_salary = new JTextField();
		textField_salary.setForeground(Color.RED);
		textField_salary.setBackground(new Color(255, 255, 204));
		textField_salary.setBounds(357, 135, 114, 19);
		employeeWindow.getContentPane().add(textField_salary);
		textField_salary.setColumns(10);
		
		lblWorksFor = new JLabel("WORKS FOR");
		lblWorksFor.setForeground(Color.WHITE);
		lblWorksFor.setBounds(35, 207, 93, 15);
		employeeWindow.getContentPane().add(lblWorksFor);
		
		textField_worksFor = new JTextField();
		textField_worksFor.setForeground(Color.RED);
		textField_worksFor.setBackground(new Color(255, 255, 204));
		textField_worksFor.setBounds(127, 205, 114, 19);
		employeeWindow.getContentPane().add(textField_worksFor);
		textField_worksFor.setColumns(10);
		
		lblManages = new JLabel("MANAGES");
		lblManages.setForeground(Color.WHITE);
		lblManages.setBounds(285, 207, 70, 15);
		employeeWindow.getContentPane().add(lblManages);
		
		textField_manages = new JTextField();
		textField_manages.setForeground(Color.RED);
		textField_manages.setBackground(new Color(255, 255, 204));
		textField_manages.setBounds(357, 203, 114, 19);
		employeeWindow.getContentPane().add(textField_manages);
		textField_manages.setColumns(10);
		
		lblSupervises = new JLabel("SUPERVISES");
		lblSupervises.setForeground(Color.WHITE);
		lblSupervises.setBounds(36, 234, 92, 15);
		employeeWindow.getContentPane().add(lblSupervises);
		
		textField_supervises = new JTextField();
		textField_supervises.setForeground(Color.RED);
		textField_supervises.setBackground(new Color(255, 255, 204));
		textField_supervises.setBounds(127, 232, 114, 19);
		employeeWindow.getContentPane().add(textField_supervises);
		textField_supervises.setColumns(10);
		
		lblSearchEmployees = new JLabel("Search Employees");
		lblSearchEmployees.setForeground(Color.WHITE);
		lblSearchEmployees.setBackground(Color.WHITE);
		lblSearchEmployees.setBounds(36, 431, 141, 22);
		employeeWindow.getContentPane().add(lblSearchEmployees);

		textField_search = new JTextField();
		textField_search.setForeground(Color.RED);
		textField_search.setBackground(new Color(255, 255, 204));
		textField_search.setBounds(175, 433, 189, 19);
		employeeWindow.getContentPane().add(textField_search);
		textField_search.setColumns(10);

		// -----------BUTTONS AND ACTION LISTENERS-----------------------

		previous = new JButton("Back");
		previous.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					getPreviousRecord();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		previous.setBounds(58, 312, 76, 25);
		employeeWindow.getContentPane().add(previous);

		next = new JButton("Next");
		next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					getNextRecord();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		});
		next.setBounds(140, 312, 86, 25);
		employeeWindow.getContentPane().add(next);

		goToFirst = new JButton("Go to First");
		goToFirst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToFirstRecord();
			}
		});
		goToFirst.setBounds(231, 312, 108, 25);
		employeeWindow.getContentPane().add(goToFirst);

		goToLast = new JButton("Go to Last");
		goToLast.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goToLastRecord();
			}
		});
		goToLast.setBounds(345, 312, 126, 25);
		employeeWindow.getContentPane().add(goToLast);

		add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addManualRecord();
			}
		});
		add.setBounds(58, 349, 76, 25);
		employeeWindow.getContentPane().add(add);

		delete = new JButton("Delete");
		delete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRecord();
			}
		});
		delete.setBounds(138, 349, 86, 25);
		employeeWindow.getContentPane().add(delete);

	

		search = new JButton("Search");
		search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchForEmployee();
			}
		});
		search.setBounds(376, 431, 114, 23);
		employeeWindow.getContentPane().add(search);

		quickAddRecord = new JButton("Quick Add");
		quickAddRecord.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				quickAddRecord();
			}
		});
		quickAddRecord.setBounds(26, 386, 163, 25);
		employeeWindow.getContentPane().add(quickAddRecord);

		clearFields = new JButton("Clear Fields");
		clearFields.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFields();
			}
		});
		clearFields.setBounds(345, 349, 126, 25);
		employeeWindow.getContentPane().add(clearFields);

		listEmployees = new JButton("List Employees");
		listEmployees.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listEmployeesBySsn();
			}
		});
		listEmployees.setBounds(196, 386, 159, 25);
		employeeWindow.getContentPane().add(listEmployees);

		update = new JButton("Update");
		update.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateARecord();
			}
		});
		update.setBounds(231, 349, 108, 25);
		employeeWindow.getContentPane().add(update);

		deleteAllRecords = new JButton("Delete All Records");
		deleteAllRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteAllRecords();
			}
		});
		deleteAllRecords.setBounds(362, 386, 163, 25);
		employeeWindow.getContentPane().add(deleteAllRecords);

		createEmployeeTable = new JButton("Create Employee Table");
		createEmployeeTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTable();
			}
		});
		createEmployeeTable.setBounds(58, 277, 197, 25);
		employeeWindow.getContentPane().add(createEmployeeTable);

		dropEmployeeTable = new JButton("Drop Employee Table");
		dropEmployeeTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dropEmployeeTable();
			}
		});
		dropEmployeeTable.setBounds(257, 277, 214, 25);
		employeeWindow.getContentPane().add(dropEmployeeTable);

	
		exitProgram = new JButton();
		exitProgram.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			int choice = JOptionPane.YES_NO_OPTION;
			int option = JOptionPane.showConfirmDialog (null, "Are you sure you want to destroy the Database?",
														"WARNING", choice);
			if (option == JOptionPane.YES_OPTION){
				exitProgram();
			} else {
			    // no option
			}
			
		}
		});
		exitProgram.setLayout(new BorderLayout());
		JLabel label1 = new JLabel("           Exit Program");
		JLabel label2 = new JLabel("       (Destroys Database)");
		exitProgram.add(BorderLayout.NORTH, label1);
		exitProgram.add(BorderLayout.SOUTH, label2);

		exitProgram.setBounds(156, 476, 224, 40);
		employeeWindow.getContentPane().add(exitProgram);
		
	}

	public JFrame getEmployeeApp() {
		return employeeWindow;
	}
}
