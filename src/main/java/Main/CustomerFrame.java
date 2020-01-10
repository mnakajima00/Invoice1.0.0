package Main;

import Utils.Customer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerFrame extends JFrame implements ActionListener {

    //Declaring JFrame Components
    //JMenu Components & FileChooser
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;
    JFileChooser fileChooser;
    String fileDest;
    //JFrame Widgets
    JLabel billToLabel, nameLabel, birthLabel, genderLabel, passportNumLabel, dateLabel, currentDateLabel;
    JTextField billToText, nameText, birthText, passportText, dateText;
    JComboBox genderCombo;
    JButton nextBtn, clearBtn;
    //Borders
    Border errorBorder, defaultBorder, defaultJComboBorder;

    //Date format
    DateTimeFormatter dFormat;

    //Initializes the JFrame
    public CustomerFrame() {
        super("Invoice");

        setSize(550, 600);
        setLocationRelativeTo(null); // Set frame to center
        setResizable(false);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        //Make "Invoice" folder if it doesn't exist to keep all Invoices and get all resources
        File invoiceFolder = new File(FileSystemView.getFileSystemView().getDefaultDirectory()+"/Invoices");
        File htmlFolder = new File(invoiceFolder.getPath()+"/html");
        File imagesFolder = new File(htmlFolder+"/images");
        InputStream logo = this.getClass().getResourceAsStream("/images/DrJungLogo.png");
        InputStream img = this.getClass().getResourceAsStream("/images/logo-trans3.png");
        if(!invoiceFolder.exists()){
            invoiceFolder.mkdirs();
            htmlFolder.mkdirs();
            imagesFolder.mkdirs();
            try {
                Files.copy(logo, Paths.get(htmlFolder.getPath()+"/images/DrJungLogo.png"), StandardCopyOption.REPLACE_EXISTING);
                Files.copy(img, Paths.get(htmlFolder.getPath()+"/images/logo-trans3.png"), StandardCopyOption.REPLACE_EXISTING);
            } catch(IOException err){
                err.printStackTrace();
            }
        }
        //Set default file location to "Invoice"
        fileDest = invoiceFolder.getPath();

        //Set up menu
        setupMenu();

        addWidgets(); //Adds all widgets inside JFrame

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //Constructor to be called when user returns from SecondFrame
    public CustomerFrame(String date, String billTo, String dob, String name, int gender, String passport, String fileDest) {
        super("Invoice");

        setSize(550, 600);
        setLocationRelativeTo(null); // Set frame to center
        setResizable(false);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));

        //Set up menu
        setupMenu();

        addWidgets(); //Adds all widgets inside JFrame

        dateText.setText(date);
        billToText.setText(billTo);
        birthText.setText(dob);
        nameText.setText(name);
        genderCombo.setSelectedIndex(gender);
        passportText.setText(passport);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    //Adds all the components into JFrame
    public void addWidgets(){

        //Initialize date format
        dFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        //Current Date
        currentDateLabel = new JLabel(LocalDate.now().format(dFormat));

        /* JPANEL STRUCTURE: VISUALIZED
         *   ___________________________________________
         *  |   container                               |
         *  |    ___________________________________    |
         *  |   |   innerContainer                  |   |
         *  |   |    ___________________________    |   |
         *  |   |   |   widgetPanel             |   |   |
         *  |   |   |                           |   |   |
         *  |   |   |                           |   |   |
         *  |   |   |                           |   |   |
         *  |   |   |___________________________|   |   |
         *  |   |___________________________________|   |
         *  |___________________________________________|
         */

        //JPanels:

        //Main Container
        JPanel container = new JPanel(new GridLayout(0, 1));
        Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20); //Padding
        container.setBorder(padding);

        //innerContainer JPanel
        JPanel innerContainer = new JPanel(new GridLayout(1, 1));
        innerContainer.setBorder(BorderFactory.createTitledBorder("CUSTOMER INFORMATION")); //Legend (Border with title)

        //widgetPanel - All widgets will go inside this JPanel
        JPanel widgetPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(); //GridBagConstraints required for GridBagLayout

        //Initialize widgets:
        //Labels:
        dateLabel = new JLabel("Date:");
        billToLabel = new JLabel("Bill To:");
        nameLabel = new JLabel("Name:");
        birthLabel = new JLabel("Date of Birth:");
        genderLabel = new JLabel("Gender:");
        passportNumLabel = new JLabel("Passport No:");
        //TextFields
        dateText = new JTextField(12);
        dateText.setText(LocalDate.now().format(dFormat));
        billToText = new JTextField(12);
        nameText = new JTextField(12);
        birthText = new JTextField(12);
        passportText = new JTextField(12);
        //JButton
        nextBtn = new JButton("Next");
        clearBtn = new JButton("Clear");
        //JButton ActionListener
        nextBtn.addActionListener(this);
        clearBtn.addActionListener(this);
        //JComboBox
        String[] genders = {"", "Male", "Female", "Other"};
        genderCombo = new JComboBox(genders);

        ////////// FIRST ROW  - Date //////////
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(10, 8, 8, 0);
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        widgetPanel.add(dateLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        widgetPanel.add(dateText, gbc);

        ////////// SECOND ROW - Bill to //////////
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 8, 8, 0);
        gbc.weightx = 0.1;
        gbc.weighty = 0.1;
        widgetPanel.add(billToLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        widgetPanel.add(billToText, gbc);

        ////////// THIRD ROW - Name and Date of Birth//////////
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.1;
        widgetPanel.add(nameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        widgetPanel.add(nameText, gbc);

        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 8, 8, 8);
        gbc.weightx = 0.1;
        widgetPanel.add(birthLabel, gbc);

        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        widgetPanel.add(birthText, gbc);

        ////////// FOURTH ROW - Gender//////////
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 8, 8, 0);
        gbc.weightx = 0.1;
        widgetPanel.add(genderLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.LINE_START;
        widgetPanel.add(genderCombo, gbc);

        ////////// FIFTH ROW - Passport Number//////////
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.weightx = 0.1;
        widgetPanel.add(passportNumLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.LINE_START;
        widgetPanel.add(passportText, gbc);

        ////////// JSEPARATOR //////////
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(10, 8, 10, 8);
        widgetPanel.add(new JSeparator(JSeparator.HORIZONTAL),gbc);


        ////////// SUBMIT / CLEAR //////////
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weighty = 0;
        gbc.weightx = 0.1;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(0, 0, 8, 0);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.LINE_END;
        widgetPanel.add(clearBtn, gbc);

        gbc.gridx = 3;
        widgetPanel.add(nextBtn, gbc);

        //Adding JPanels to parent JPanel
        innerContainer.add(widgetPanel);

        container.add(innerContainer);

        //Add container to JFrame
        add(container, BorderLayout.PAGE_START);
    }

    //This method gets called when either "Clear", "Next" or "Export To" button is clicked
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Clear":  //If Clear button is clicked
                clear();
                break;
            case "Next":  //If Next button is clicked
                if (validateInputs()) { //CHANGE TO MAKE VALIDATION WORK!!!!!!
                    //Go to next step: Entering information for main.Util.Products/Services purchased
                    Customer c = new Customer(
                            dateText.getText(),
                            billToText.getText(),
                            nameText.getText(),
                            birthText.getText(),
                            genderCombo.getSelectedItem().toString(),
                            passportText.getText(),
                            fileDest);
                    new InvoiceFrame(c);
                    setVisible(false);
                }
                break;
            case "Export To":
                //User clicks menu item "Save As"
                fileDestinationChooser();
                break;
        }
    }

    //Clears entered values and resets borders to default
    private void clear(){
        dateText.setText("");
        dateText.setBorder(defaultBorder);
        billToText.setText("");
        billToText.setBorder(defaultBorder);
        nameText.setText("");
        nameText.setBorder(defaultBorder);
        birthText.setText("");
        birthText.setBorder(defaultBorder);
        genderCombo.setSelectedIndex(0);
        genderCombo.setBorder(defaultJComboBorder);
        passportText.setText("");
        passportText.setBorder(defaultBorder);
    }

    //Makes sure values entered are valid
    private boolean validateInputs() {
        //Custom borders for invalid input values
        errorBorder = BorderFactory.createLineBorder(Color.RED, 1);
        defaultBorder = new JTextField().getBorder();
        defaultJComboBorder = new JComboBox().getBorder();

        boolean valid = true;

        if(!(validateDate() && validateText() && validateGender())){
            valid = false;
        }

        return valid;

    }

    //Validates inputs from date and date of birth
    private boolean validateDate(){
        boolean valid = true;
        DateFormat dFormat = new SimpleDateFormat("dd/MM/yyyy");
        //Input to be parsed should strictly follow the defined date format above
        dFormat.setLenient(false);

        try { //Try to parse the date text to see if they are in the right format
            dFormat.parse(dateText.getText());
            dateText.setBorder(defaultBorder);
        } catch(ParseException e ){
            dateText.setBorder(errorBorder);
            valid = false;
        }

        try { //Try to parse the birth date text to see if they are in the right format
            dFormat.parse(birthText.getText());
            birthText.setBorder(defaultBorder);
        } catch (ParseException e){
            birthText.setBorder(errorBorder);
            valid = false;
        }

        if(!valid) { //Show JOptionPane if the dates are in the wrong format
            JOptionPane.showMessageDialog(this, "The date should be in the format 'dd/MM/yyyy'");
        }

        return valid;
    }

    //Validates inputs from name, billTo and passport number
    private boolean validateText(){

        boolean valid = true;

        if(billToText.getText().isEmpty()){
            valid = false;
            billToText.setBorder(errorBorder);
        } else {
            billToText.setBorder(defaultBorder);
        }

        if(nameText.getText().isEmpty()){
            valid = false;
            nameText.setBorder(errorBorder);
        } else {
            nameText.setBorder(defaultBorder);
        }

        if(passportText.getText().isEmpty()){
            valid = false;
            passportText.setBorder(errorBorder);
        } else {
            passportText.setBorder(defaultBorder);
        }

        if(!valid){
            JOptionPane.showMessageDialog(this,"Please enter valid text.");
        }

        return valid;
    }

    //Validates gender JCombobox
    private boolean validateGender(){
        boolean valid = true;
        if(genderCombo.getSelectedIndex() == 0){
            valid = false;
            genderCombo.setBorder(errorBorder);
            JOptionPane.showMessageDialog(this, "Select a valid gender.");
        } else {
            genderCombo.setBorder(defaultJComboBorder);
        }

        return valid;
    }

    private void setupMenu(){
        //Set up the "File" menu
        menuBar = new JMenuBar();
        menu = new JMenu("File");
        menuItem = new JMenuItem("Export To");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);
    }

    //Lets user choose file export destination
    private void fileDestinationChooser(){

        fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
        fileChooser.setDialogTitle(fileDest);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            fileDest = fileChooser.getSelectedFile().getPath();
        }

    }
}
