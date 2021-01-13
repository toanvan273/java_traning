package vn.com.vndirect.report.service;
import java.io.File;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.JRSaver;

@Component
@Scope(value = "prototype")
public class DataReportFiller {
	
	private final static Logger logger = LoggerFactory.getLogger(DataReportExporter.class);

    private String reportFileName;

    private JasperReport jasperReport;

    private JRDataSource dataSource;
    
    private String templateDir;

    @Autowired
    public DataReportFiller(Environment environment) { 
    	templateDir =  environment.getProperty("template.dir") + File.separator + "templates"  + File.separator;
    }

    public void compileReport() throws JRException {
        String jasperFileName = reportFileName.replace(".jrxml", ".jasper");
    	File jasperFile = new File(jasperFileName);
    	if(jasperFile.exists()) {
    		logger.info("Found jasper file:" + jasperFileName);
    		jasperReport = (JasperReport) JRLoader.loadObject(jasperFile);
    	} else {
    		logger.info("Not found jasper file, compile new:" + jasperFileName);
    		String templateFileName = templateDir + reportFileName;
            jasperReport = JasperCompileManager.compileReport(templateFileName);
            JRSaver.saveObject(jasperReport, jasperFileName);
    	}
    }

    public JasperPrint getJasperPrint(Map<String, Object> parameters) {
        try {
            return JasperFillManager.fillReport(jasperReport, parameters, dataSource);
        } catch (JRException ex) {
        	logger.error(ex.toString(), ex);
        }
        return null;
    }

    public JRDataSource getDataSource() { return dataSource; }
    public void setDataSource(JRDataSource dataSource) {  this.dataSource = dataSource; }

    public String getReportFileName() { return reportFileName; }
    public void setReportFileName(String reportFileName) { this.reportFileName = reportFileName; }

   /*private File findTemplatesFolder() throws URISyntaxException {
    	URL url = getClass().getResource("/");
    	File dir = new File(url.toURI());
    	File templateDir = new File(dir, "templates" + File.separator);
    	if(templateDir.exists() && templateDir.isDirectory()) return templateDir;
    	
    	logger.warn(templateDir.getAbsolutePath() + " not found.");
    	
    	for(int i = 0; i < 5; i++) {
    		dir = dir.getParentFile();
    		templateDir = new File(dir, "templates" + File.separator);
    		if(templateDir.exists() && templateDir.isDirectory()) return templateDir;
    		logger.warn(templateDir.getAbsolutePath() + " not found.");
    	}
    	
    	url = getClass().getResource(".");
    	dir = new File(url.toURI());
    	return new File(dir, "templates" + File.separator);
    }*/
    

}