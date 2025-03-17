package com.increff.invoice.api;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.fop.apps.FOUserAgent;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.increff.invoice.dao.InvoiceDao;
import com.increff.invoice.data.InvoiceData;
import com.increff.invoice.data.InvoiceItemData;
import com.increff.invoice.db.InvoicePojo;
import com.increff.invoice.exception.ApiException;
import com.increff.invoice.helper.InvoiceHelper;
@Service
public class InvoiceApiImpl implements InvoiceApi {
    @Autowired
    private InvoiceDao invoiceDao;
        @Override
        public String generateInvoice(InvoiceData invoiceData) throws Exception {
            
             // Define the directory and file path
             String directoryPath = "/Users/riteshsingh/Desktop/POS/Invoice-app/Invoices";
             String pdfFilePath = directoryPath + "/" + invoiceData.getInvoiceId() + ".pdf";
             File pdfFile = new File(pdfFilePath);
     
             // Create the directory if it does not exist
             File directory = new File(directoryPath);
             if (!directory.exists()) {
                 directory.mkdirs(); // Create the directory
             }
     
             if (!pdfFile.exists()) {
                 // Create PDF using Apache FOP
                 FopFactory fopFactory = FopFactory.newInstance(new File(".").toURI());
                 FOUserAgent foUserAgent = fopFactory.newFOUserAgent();
                 OutputStream out = new FileOutputStream(pdfFile);
     
                 try {
                     Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, foUserAgent, out);
                     TransformerFactory factory = TransformerFactory.newInstance();
                     Transformer transformer = factory.newTransformer(); // identity transformer
     
                     // Setup input for XSLT transformation
                     Source src = new StreamSource(new StringReader(convertInvoiceDataToXml(invoiceData)));
     
                     // Resulting SAX events (the generated FO) must be piped through to FOP
                     Result res = new SAXResult(fop.getDefaultHandler());
     
                     // Start XSLT transformation and FOP processing
                     transformer.transform(src, res);
                 } finally {
                     out.close();
                 }
             }
             InvoicePojo invoicePojo = InvoiceHelper.convertToPojo(invoiceData);
             invoicePojo.setInvoiceFilePath(pdfFilePath);
             invoiceDao.save(invoicePojo);
             return pdfFilePath;
    }
    @Override
    public String downloadInvoice(String invoiceId) throws ApiException {
        return invoiceDao.findByInvoiceId(invoiceId).getInvoiceFilePath();
    }
    private String convertInvoiceDataToXml(InvoiceData invoiceData) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xmlBuilder.append("<fo:root xmlns:fo=\"http://www.w3.org/1999/XSL/Format\">");

        xmlBuilder.append("<fo:layout-master-set>");
        xmlBuilder.append("<fo:simple-page-master master-name=\"simple\" page-height=\"11in\" page-width=\"8.5in\" margin=\"0.5in\">");
        xmlBuilder.append("<fo:region-body/>");
        xmlBuilder.append("</fo:simple-page-master>");
        xmlBuilder.append("</fo:layout-master-set>");

        xmlBuilder.append("<fo:page-sequence master-reference=\"simple\">");
        xmlBuilder.append("<fo:flow flow-name=\"xsl-region-body\">");

        // Header section with better spacing
        xmlBuilder.append("<fo:block font-size=\"24pt\" font-weight=\"bold\" text-align=\"center\" margin-bottom=\"20pt\">INVOICE</fo:block>");
        xmlBuilder.append("<fo:block font-size=\"12pt\" margin-bottom=\"5pt\">Invoice Number: ").append(invoiceData.getInvoiceId()).append("</fo:block>");
        xmlBuilder.append("<fo:block font-size=\"12pt\" margin-bottom=\"5pt\">Customer Name: ").append(invoiceData.getCustomerName()).append("</fo:block>");
        xmlBuilder.append("<fo:block font-size=\"12pt\" margin-bottom=\"5pt\">Customer Email: ").append(invoiceData.getCustomerEmail()).append("</fo:block>");
        xmlBuilder.append("<fo:block font-size=\"12pt\" margin-bottom=\"20pt\">Invoice Date: ").append(invoiceData.getInvoiceDate()).append("</fo:block>");

        // Items table with borders and better spacing
        xmlBuilder.append("<fo:block font-size=\"16pt\" font-weight=\"bold\" margin-top=\"20pt\" margin-bottom=\"10pt\">Order Details</fo:block>");

        xmlBuilder.append("<fo:table border=\"1pt solid black\" table-layout=\"fixed\" width=\"100%\" margin-bottom=\"20pt\">");
        xmlBuilder.append("<fo:table-column column-width=\"10%\"/>"); // For S.No
        xmlBuilder.append("<fo:table-column column-width=\"35%\"/>");
        xmlBuilder.append("<fo:table-column column-width=\"15%\"/>");
        xmlBuilder.append("<fo:table-column column-width=\"20%\"/>");
        xmlBuilder.append("<fo:table-column column-width=\"20%\"/>");

        // Table header with background color
        xmlBuilder.append("<fo:table-header>");
        xmlBuilder.append("<fo:table-row background-color=\"#f0f0f0\">");
        xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block font-weight=\"bold\">S.No</fo:block></fo:table-cell>");
        xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block font-weight=\"bold\">Product Name</fo:block></fo:table-cell>");
        xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\" text-align=\"right\"><fo:block font-weight=\"bold\">Quantity</fo:block></fo:table-cell>");
        xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\" text-align=\"right\"><fo:block font-weight=\"bold\">Price (Rs.)</fo:block></fo:table-cell>");
        xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\" text-align=\"right\"><fo:block font-weight=\"bold\">Total (Rs.)</fo:block></fo:table-cell>");
        xmlBuilder.append("</fo:table-row>");
        xmlBuilder.append("</fo:table-header>");

        xmlBuilder.append("<fo:table-body>");
        
        // Calculate total while generating rows
        double orderTotal = 0;
        int index = 1;
        for (InvoiceItemData item : invoiceData.getInvoiceItems()) {
            orderTotal += item.getTotal();
            xmlBuilder.append("<fo:table-row>");
            xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block>").append(index++).append("</fo:block></fo:table-cell>");
            xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block>").append(item.getProductName()).append("</fo:block></fo:table-cell>");
            xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block text-align=\"right\">").append(item.getQuantity()).append("</fo:block></fo:table-cell>");
            xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block text-align=\"right\">").append(String.format("%.2f", item.getSellingPrice())).append("</fo:block></fo:table-cell>");
            xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block text-align=\"right\">").append(String.format("%.2f", item.getTotal())).append("</fo:block></fo:table-cell>");
            xmlBuilder.append("</fo:table-row>");
        }

        // Add total row
        xmlBuilder.append("<fo:table-row background-color=\"#f0f0f0\">");
        xmlBuilder.append("<fo:table-cell number-columns-spanned=\"4\" border=\"1pt solid black\" padding=\"5pt\"><fo:block font-weight=\"bold\" text-align=\"right\">Order Total:</fo:block></fo:table-cell>");
        xmlBuilder.append("<fo:table-cell border=\"1pt solid black\" padding=\"5pt\"><fo:block font-weight=\"bold\" text-align=\"right\">").append(String.format("%.2f", orderTotal)).append("</fo:block></fo:table-cell>");
        xmlBuilder.append("</fo:table-row>");
        
        xmlBuilder.append("</fo:table-body>");
        xmlBuilder.append("</fo:table>");

        // Footer
        xmlBuilder.append("<fo:block font-size=\"10pt\" margin-top=\"30pt\" text-align=\"center\">Thank you! Have a great day!</fo:block>");

        xmlBuilder.append("</fo:flow>");
        xmlBuilder.append("</fo:page-sequence>");
        xmlBuilder.append("</fo:root>");
        return xmlBuilder.toString();
    }
}
