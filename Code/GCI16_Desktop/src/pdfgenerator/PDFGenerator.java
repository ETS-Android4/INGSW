package pdfgenerator;

import backofficeclient.entities.PaymentOrder;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;



/**
 * Creates PDF
 * @author Mattia
 */

public class PDFGenerator {
    
    /* Font and color constants */
    private static final Font FONT1 = new Font(Font.FontFamily.COURIER, 30, Font.NORMAL, new BaseColor(90,131,219));
    private static final Font FONT2 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, new BaseColor(120,120,120));
    private static final Font FONT3 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, new BaseColor(0,0,0));
    
    /**
     * Generates PDF from a payment order.
     * @param paym 
     */
    public boolean generate(PaymentOrder paym) {
        /* Creates document */
        Document document = new Document(PageSize.A4);
        document.addTitle("PaymentOrder " + paym.getProtocol());
        document.addCreationDate();
        Paragraph p = null;
        try{
            PdfWriter.getInstance(document, new FileOutputStream("PaymentOrders/PaymentOrder-" + paym.getProtocol() + ".pdf"));
            document.open();
            
            document.add( new Paragraph("GCI16", FONT1) );
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add( new Paragraph("Due to lack of payment it has been issued"
                                      + " the following payment order.", FONT2) );
            document.add(Chunk.NEWLINE);
            p=new Paragraph("Protocol:    ");
            p.setFont(FONT3);
            p.add(String.valueOf(paym.getProtocol()));
            document.add(p);
            p = new Paragraph("Debtor:      ");
            p.setFont(FONT3);
            p.add(paym.getDebtor());
            document.add(p);
            p = new Paragraph("Bill:            ");
            p.setFont(FONT3);
            p.add(String.valueOf(paym.getId()));
            document.add(p);
            int trim = paym.getTrimester();
            String trimester = null;
            switch (trim) {
                case 1: trimester = "January - March"; break;
                case 2: trimester = "April - June"; break;
                case 3: trimester = "July - September"; break;
                case 4: trimester = "October - December"; break;
            }
            p = new Paragraph("Trimester:  "); 
            p.setFont(FONT3);
            p.add(trimester);
            document.add(p);
            p = new Paragraph("Year:         "); 
            p.setFont(FONT3);
            p.add(String.valueOf(paym.getYear()));
            document.add(p);
            p = new Paragraph("Amount:    "); 
            p.setFont(FONT3);
            p.add(String.valueOf(paym.getAmount()));
            document.add(p);
            
        }catch(FileNotFoundException | DocumentException exc){
            Logger.getLogger(PDFGenerator.class.getName()).log(Level.SEVERE, null, exc);
            return false;
        }
        document.close();
        return true;
    }
}
