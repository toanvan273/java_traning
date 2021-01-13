package vn.com.vndirect.report.service;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterConfiguration;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleHtmlReportConfiguration;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@Scope(value = "prototype")
public class DataReportExporter {
	
	private final static Logger logger = LoggerFactory.getLogger(DataReportExporter.class);

    private JasperPrint jasperPrint;

    public DataReportExporter() {
    }

    public DataReportExporter(JasperPrint jasperPrint) { 
    	this.jasperPrint = jasperPrint; 
    }
    
    public JasperPrint getJasperPrint() { return jasperPrint; }
    public void setJasperPrint(JasperPrint jasperPrint) {  this.jasperPrint = jasperPrint; }

    public void exportToPdf(OutputStream outputStream, String author) throws JRException {
        JRPdfExporter exporter = new JRPdfExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
        reportConfig.setSizePageToContent(true);
        reportConfig.setForceLineBreakPolicy(false);

        SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
        exportConfig.setMetadataAuthor(author);
        exportConfig.setEncrypted(true);
        exportConfig.setAllowedPermissionsHint("PRINTING");

        exporter.setConfiguration(reportConfig);
        exporter.setConfiguration(exportConfig);
        
        exporter.exportReport();
    }

    public void exportToXlsx(OutputStream outputStream, String sheetName) throws JRException {
        JRXlsxExporter exporter = new JRXlsxExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));

        SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
        reportConfig.setSheetNames(new String[] { sheetName });

        exporter.setConfiguration(reportConfig);

        exporter.exportReport();
    }

    public void exportToCsv(OutputStream outputStream) throws JRException {
        JRCsvExporter exporter = new JRCsvExporter();

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleWriterExporterOutput(outputStream));

        exporter.exportReport();
    }
    
    public void exportToHtml(OutputStream outputStream) throws JRException {
    	exportToHtml(null, null, outputStream);
    }
    
    public void exportToHtml(SimpleHtmlExporterConfiguration shec, SimpleHtmlReportConfiguration shrc, OutputStream outputStream) throws JRException  {
        HtmlExporter exporter = new HtmlExporter();

        if(shec != null) exporter.setConfiguration(shec);
        if(shrc != null) exporter.setConfiguration(shrc);
        
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleHtmlExporterOutput(outputStream));

        exporter.exportReport();
    }
}