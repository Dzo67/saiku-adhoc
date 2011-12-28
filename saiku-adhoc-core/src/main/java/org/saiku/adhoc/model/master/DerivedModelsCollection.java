/* Copyright (C) 2011 Marius Giepz
*
* This program is free software; you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the Free
* Software Foundation; either version 2 of the License, or (at your option)
* any later version.
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*
*/

package org.saiku.adhoc.model.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.LogicalModel;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.query.model.util.QueryXmlHelper;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.saiku.adhoc.exceptions.SaikuAdhocException;
import org.saiku.adhoc.service.SaikuProperties;

import pt.webdetails.cda.connections.Connection;
import pt.webdetails.cda.connections.metadata.MetadataConnection;
import pt.webdetails.cda.dataaccess.DataAccess;
import pt.webdetails.cda.dataaccess.MqlDataAccess;
import pt.webdetails.cda.settings.CdaSettings;

public class DerivedModelsCollection {
	
	//These are the derived models, they are 
	//transformed from the master model and stored here whenever
	//necessary
	protected Query query;
	protected CdaSettings cda;
	protected WizardSpecification wizardSpec;
	private MasterReport reportTemplate;
		
	protected ArrayList<String> groups;
	protected Map<String, String> groupIdToName;
	protected Map<String,Query> filterQueries;
	//private Map<String,ArrayList<String>> filterValues;	
	protected DefaultParameterDefinition paramDef; 

	protected String sessionId;
	protected Domain domain;
	protected LogicalModel logicalModel;
	protected QueryXmlHelper xmlHelper; 
	protected DataFactory cdaDataFactory;
	
	protected Map<String,Object> rptIdToSaikuElement;
	
	protected Map<String,SaikuElementFormat> rptIdToElementFormat;

	public DerivedModelsCollection(String sessionId, Domain domain,
			LogicalModel model) throws SaikuAdhocException {
		this.sessionId = sessionId;
		this.domain = domain;
		this.logicalModel = model;
		
	
		
	}

	public void init() throws SaikuAdhocException{
	    //init all the stuff
        this.query = new Query(domain, logicalModel);      
        this.xmlHelper = new QueryXmlHelper();  
        this.groups = new ArrayList<String>();
        this.groupIdToName = new HashMap<String, String>();     
        this.filterQueries = new HashMap<String,Query>();
        //this.filterValues = new HashMap<String,ArrayList<String>>();  
        this.paramDef = new DefaultParameterDefinition();
        
        this.rptIdToSaikuElement = new HashMap<String, Object>();
        this.rptIdToElementFormat = new HashMap<String,SaikuElementFormat>();       

        try{
            //init cda
            this.cda = new CdaSettings("cda" + sessionId, null);
            String[] domainInfo = domain.getId().split("/");
            Connection connection = new MetadataConnection("1", domainInfo[0], domainInfo[1]);
            DataAccess dataAccess = new MqlDataAccess(sessionId, sessionId, "1", "") ;
            cda.addConnection(connection);
            cda.addDataAccess(dataAccess);

            //init the wizard-spec
            this.wizardSpec = new DefaultWizardSpecification();     
            wizardSpec.setAutoGenerateDetails(false);

            //Init the Data Factory
            String solution = "system";
            String path = "saiku-adhoc/temp";
            
            CdaDataFactory f = new CdaDataFactory();        
            String baseUrlField = null;
            f.setBaseUrlField(baseUrlField);
            String name = this.sessionId;
            String queryString = this.sessionId;
            f.setQuery(name, queryString);          
            String baseUrl = PentahoSystem.getApplicationContext().getFullyQualifiedServerURL(); //"http://localhost:8080/pentaho";
            
            //Use this for the login
            //PentahoSessionHolder.getSession().getId();
            
            f.setBaseUrl(baseUrl);
            f.setSolution(solution);
            f.setPath(path);
            String file =  this.sessionId + ".cda";
            f.setFile(file);        
            String username = "joe";
            f.setUsername(username);
            String password = "password";
            f.setPassword(password);
            this.cdaDataFactory = f;
                        
        }catch(Exception e){
            throw new SaikuAdhocException("heavy failure");
        }
	}
//	public void setFilterValues(Map<String,ArrayList<String>> filterValues) {
//		this.filterValues = filterValues;
//	}
//
//	public Map<String,ArrayList<String>> getFilterValues() {
//		return filterValues;
//	}

	public Query getQuery() {
		return query;
	}

	public void setQuery(Query query) {
		this.query = query;
	}

	public CdaSettings getCda() {
		return cda;
	}

	public void setCda(CdaSettings cda) {
		this.cda = cda;
	}

	public WizardSpecification getWizardSpec() {
		return wizardSpec;
	}

	public void setWizardSpec(WizardSpecification wizardSpec) {
		this.wizardSpec = wizardSpec;
	}

	public MasterReport getReportTemplate() {
		return reportTemplate;
	}

	public void setReportTemplate(MasterReport reportTemplate) {
		this.reportTemplate = reportTemplate;
	}

	public ArrayList<String> getGroups() {
		return groups;
	}

	public void setGroups(ArrayList<String> groups) {
		this.groups = groups;
	}

	public Map<String, String> getGroupIdToName() {
		return groupIdToName;
	}

	public void setGroupIdToName(Map<String, String> groupIdToName) {
		this.groupIdToName = groupIdToName;
	}

	public Map<String, Query> getFilterQueries() {
		return filterQueries;
	}

	public void setFilterQueries(Map<String, Query> filterQueries) {
		this.filterQueries = filterQueries;
	}

	public DefaultParameterDefinition getParamDef() {
		return paramDef;
	}

	public void setParamDef(DefaultParameterDefinition paramDef) {
		this.paramDef = paramDef;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public Domain getDomain() {
		return domain;
	}

	public void setDomain(Domain domain) {
		this.domain = domain;
	}

	public LogicalModel getLogicalModel() {
		return logicalModel;
	}

	public void setLogicalModel(LogicalModel model) {
		this.logicalModel = model;
	}

	public QueryXmlHelper getXmlHelper() {
		return xmlHelper;
	}

	public void setXmlHelper(QueryXmlHelper xmlHelper) {
		this.xmlHelper = xmlHelper;
	}

	public void setCdaDataFactory(DataFactory cdaDataFactory) {
		this.cdaDataFactory = cdaDataFactory;
	}

	public DataFactory getCdaDataFactory() {
		return cdaDataFactory;
	}

	public void setRptIdToSaikuElement(Map<String,Object> rptIdToSaikuElement) {
		this.rptIdToSaikuElement = rptIdToSaikuElement;
	}

	public Map<String,Object> getRptIdToSaikuElement() {
		return rptIdToSaikuElement;
	}

	public void setRptIdToElementFormat(Map<String,SaikuElementFormat> rptIdToElementFormat) {
		this.rptIdToElementFormat = rptIdToElementFormat;
	}

	public Map<String,SaikuElementFormat> getRptIdToElementFormat() {
		return rptIdToElementFormat;
	}

 
}
