package Utils;
import Main.InvoiceFrame;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class Customer {
    //main.Util.Customer info variable declarations
    String date, billTo, name, dateOfBirth, gender, passportNum, symptoms, attendingDoctor;
    //TreeMap that contains invoice(s)
    TreeMap<String, LinkedHashMap<String, Double>> totalInvoice;
    //Destination of where the file will be saved
    String fileDest;
    public Customer(String date, String billTo, String name, String dateOfBirth, String gender, String passportNum, String fileDest) {
        //Initialize variables
        this.date = date;
        this.billTo = billTo;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.passportNum = passportNum;
        this.symptoms = "";
        this.attendingDoctor = "";
        this.fileDest = fileDest;

        //This sorts the invoices by date. Earliest to Oldest
        Comparator<String> sortByDate = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                String[] splitDate1 = o1.split("/");
                int day1 = Integer.parseInt(splitDate1[0]);
                int month1 = Integer.parseInt(splitDate1[1]);
                int year1 = Integer.parseInt(splitDate1[2]);

                String[] splitDate2 = o2.split("/");
                int day2 = Integer.parseInt(splitDate2[0]);
                int month2 = Integer.parseInt(splitDate2[1]);
                int year2 = Integer.parseInt(splitDate2[2]);

                if (year1 > year2 //If the year for o1 is > the year for o2
                        || (year1 == year2 && month1 > month2) //If they both have the same year but month of o1 > month of o2
                        || (year1 == year2 && month1 == month2 && day1 > day2)) { //Same month and year but day of o1 > day of
                    return 1;
                } else if ((year1 == year2 && month1 == month2 && day1 == day2)) { //If they have the same year, month and day
                    return 0;
                } else {
                    return -1;
                }
            }
        };

        //Initialize TreeMap
        totalInvoice = new TreeMap<>(sortByDate);
    }

    //Getters and Setters
    public String getDate() {
        return date;
    }

    public String getBillTo() {
        return billTo;
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public int getGenderIndex(String gender){
        if(gender.equals("Male")){
            return 1;
        } else if(gender.equals("Female")){
            return 2;
        } else {
            return 0;
        }
    }

    public String getPassportNum() {
        return passportNum;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getAttendingDoctor() {
        return attendingDoctor;
    }

    public void setAttendingDoctor(String attendingDoctor) {
        this.attendingDoctor = attendingDoctor;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setFileDest(String fileDest){
        this.fileDest = fileDest;
    }

    public String getFileDest(){
        return this.fileDest;
    }

    public Double getTotalPrice(){
        double totalPrice = 0.0;

        for(LinkedHashMap<String, Double> entry : totalInvoice.values()){
            for(Double d : entry.values()){
                totalPrice+=d;
            }
        }
        return totalPrice;
    }

    public TreeMap<String, LinkedHashMap<String, Double>> getAllInvoices(){
        return totalInvoice;
    }

    //Adds new invoice to TreeMap
    public void addToInvoiceList(String date, LinkedHashMap<String, Double> purchases){
        this.totalInvoice.put(date, purchases);
    }

    //Checks for duplicate keys in Treemap
    public boolean dateExists(String date){
        return totalInvoice.containsKey(date);
    }

    //Exports generated HTML file into PDF
    public void printInvoice(InvoiceFrame sf) throws IOException{
        //Invoice HTML template
        InputStream invoiceTemp = this.getClass().getResourceAsStream("/html/invoice_template.html");
        InputStream invoiceTempCss = this.getClass().getResourceAsStream("/html/invoice_template.css");
        //New file to save final invoice
        String dateString = getDate();
        String formatDate = dateString.replace("/", ""); //Formats the date from eg: 11/12/2020 to 11122020
        //Final invoice in HTML
        File finalInvoice = new File(getFileDest()+"/html/"+getName()+"_"+getPassportNum()+"_"+formatDate+".html");

        try {
            finalInvoice.createNewFile(); //Creates a new html file where the final invoice will be generated (in HTML)
            finalInvoice.deleteOnExit(); //The HTML file will be deleted when the use exits from the application
            Files.copy(invoiceTempCss, Paths.get(getFileDest()+"/html/invoice_template.css"), StandardCopyOption.REPLACE_EXISTING);
            FileWriter fw = new FileWriter(finalInvoice);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(generateInvoiceHTML(invoiceTemp)); //Writes the HTML code to temp HTML file - finalInvoice
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String inputFile = finalInvoice.getPath(); //Path to final invoice in HTML
        //Path to where the final invoice in PDF will be saved to
        String outputFile = fileDest+"/"+getName()+"_"+getPassportNum()+"_"+formatDate+".pdf";

        String html = new String(Files.readAllBytes(Paths.get(inputFile)));
        //Parse HTML into XHTML
        final Document document = Jsoup.parse(html);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);

        //Using JSoup and flyingsaucer api to generate PDF
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(document.html());
        renderer.layout();

        try(OutputStream os = Files.newOutputStream(Paths.get(outputFile))){
            //IN PROGRESS - Show JProgressBar while PDF is being created//

            renderer.createPDF(os); //Creates PDF
            Desktop desktop = Desktop.getDesktop();
            File output = new File(outputFile); //Gets PDF file that was just created
            if(output.exists()){ //If the file exists, open it
                desktop.open(output);
            }
        }


    }

    //Generates Invoice in HTML
    public String generateInvoiceHTML(InputStream f){
        Document d = null;
        try {
            d = Jsoup.parse(f, "UTF-8", ""); //Try to parse invoice_template.html
        } catch (IOException err) {
            err.printStackTrace();
        }

        //Input

        //Set the absolute  path to CSS file
        Element abs = d.select("link").first()
                .attr("href", "file://"+getFileDest()+"/html/invoice_template.css");
        abs = d.select("link").last()
                .attr("href", "file://"+getFileDest()+"/html/invoice_template.css");

        //Adding HTML code for main.Util.Customer Info
        Elements e = d.select("span#billTo").append(" "+getBillTo());
        e = d.select("span#name").append(" "+getName());
        e = d.select("span#dateOfBirth").append(" "+getDateOfBirth());
        e = d.select("span#gender").append(" "+getGender());
        e = d.select("span#passportNo").append(" "+getPassportNum());
        e = d.select("span#date").append(" "+getDate());

        //Adding HTML code for Invoice
        //Loop through Invoice TreeMap
        for(Map.Entry<String, LinkedHashMap<String, Double>> entry : totalInvoice.entrySet()){
            String date = entry.getKey();
            int productCount = 0;

            //Adds a new <table> and <tr> for the Invoice
            Element tableInvoiceElement = d.getElementById("invoiceColumn")
                    .appendElement("table")
                    .appendElement("tr").addClass("date")
                    .appendElement("td").text(date);

            //Adds a new <table> and <tr> for the Amount(Total)
            Element tableAmountElement = d.getElementById("amountColumn")
                    .appendElement("table")
                    .appendElement("tr").appendElement("td").text("-"); // Empty row since the first row is for the Date
            //Loop through inner LinkedHashMap
            for(Map.Entry<String, Double> map : entry.getValue().entrySet()){
                 String product = map.getKey(); //Product name
                 double price = map.getValue(); //Product price
                productCount += 1; //Keeps count of number of products

                //Adds product and its price
                tableInvoiceElement = d.getElementById("invoiceColumn")
                        .getElementsByTag("table").last()
                        .appendElement("tr")//Product No. & Description
                        .appendElement("td").addClass("productCount").text(Integer.toString(productCount));
                tableInvoiceElement.appendElement("td").addClass("product").text(product);

                //Adds product price
                tableAmountElement = d.getElementById("amountColumn") //Add an empty row for date
                        .getElementsByTag("table").last()
                        .appendElement("tr")
                        .appendElement("td").text(Double.toString(price));
            }
        }

        //Adding Symptoms HTML
        Element symptomsHTML = d.getElementById("symptoms")
                .appendElement("td").text("*Symptoms: "+getSymptoms());
        symptomsHTML = d.getElementById("symptoms")
                .appendElement("td");

        //Adding Total HTML
        Element totalHTML = d.getElementById("totalPrice")
                .appendElement("td");
        totalHTML = d.getElementById("totalPrice")
                .appendElement("td").text("Total: " + getTotalPrice());

        //Adding doctot HTML
        Element doctorHTML = d.getElementById("doctor")
                .text("(Dr. "+getAttendingDoctor()+")");

        //Returns the generated HTML as a String
        return d.toString();
    }

    //Delete single invoice, given the date (Key)
    public boolean deleteInvoice(String date){
        //If invoice exists, remove from TreeMap
        return totalInvoice.remove(date) != null;

    }

}
