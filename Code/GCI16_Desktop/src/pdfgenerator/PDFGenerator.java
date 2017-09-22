package pdfgenerator;

import backofficeclient.PaymentOrder;
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
 *
 * @author Mattia
 */

public class PDFGenerator {
    
    /* Costanti di vari font, grandezze e colori */
    private static final Font FONT1 = new Font(Font.FontFamily.COURIER, 30, Font.NORMAL, new BaseColor(90,131,219));
    private static final Font FONT2 = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD, new BaseColor(120,120,120));
    private static final Font FONT3 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, new BaseColor(0,0,0));
    
    public static void generate(PaymentOrder paym) {
        /* Creo oggetto documento */
        Document document = new Document(PageSize.A4);
        document.addTitle("PaymentOrder " + paym.getProtocol());
        document.addCreationDate();
        System.out.println("Documento PDF creato con successo");
        
        try{
            PdfWriter.getInstance(document, new FileOutputStream("PaymentOrder " + paym.getProtocol() + ".pdf"));
            document.open();
            if(document == null)
                System.out.println("\n\n\n\nDOCUMENT null\n\n\n\n");
            else
                System.out.println("\n\n\n\nDOCUMENT != null\n\n\n\n");
            
            document.add( new Paragraph("GCI16", FONT1) );
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add( new Paragraph("Payment order issued", FONT2) );
            document.add(Chunk.NEWLINE);
            // TODO paym.getBill()
            document.add( new Paragraph("Protocol:    " + paym.getProtocol(), FONT3) );
            document.add( new Paragraph("Debtor:    " + paym.getDebtor(), FONT3) );
            document.add( new Paragraph("Trimester:    " + paym.getTrimester(), FONT3) );
            document.add( new Paragraph("Year:    " + paym.getYear(), FONT3) );
            document.add( new Paragraph("Amount:    " + paym.getAmount(), FONT3) );
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add(Chunk.NEWLINE);
            document.add( new Paragraph("Goodbye!", FONT3) );
            
        }catch(FileNotFoundException | DocumentException exc){
            Logger.getLogger(PDFGenerator.class.getName()).log(Level.SEVERE, null, exc);
        }
        
        document.close();
    }
    
    
}
