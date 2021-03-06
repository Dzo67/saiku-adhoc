package org.saiku.adhoc.model.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.function.CountDistinctFunction;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultGroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultWizardSpecification;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.engine.classic.wizard.model.LengthUnit;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.saiku.adhoc.exceptions.ReportException;
import org.saiku.adhoc.model.master.SaikuColumn;
import org.saiku.adhoc.model.master.SaikuGroup;
import org.saiku.adhoc.model.master.SaikuMasterModel;
import org.saiku.adhoc.utils.TemplateUtils;

public class WizardBuilder {

	private static final Log log = LogFactory.getLog(WizardBuilder.class);

	public WizardSpecification build(SaikuMasterModel model) throws ReportException{

		WizardSpecification wizardSpec = new DefaultWizardSpecification();		
		wizardSpec.setAutoGenerateDetails(false);

		List<SaikuColumn> columns = model.getColumns();
		int columnCount = 0;
		for (SaikuColumn saikuColumn : columns) {
			if(!saikuColumn.isHideOnReport()){
				columnCount++;
			}			
		}

		//final int columnCount = columns.size();		
		final DetailFieldDefinition[] detailFields = new DetailFieldDefinition[columnCount];

		//Add the columns
		int i = 0;
		Double widthCumul = Double.valueOf(0);
		for (SaikuColumn saikuColumn : columns) {

			if(!saikuColumn.isHideOnReport()){

				String name = saikuColumn.getName();

				DefaultDetailFieldDefinition detailFieldDef = new DefaultDetailFieldDefinition(name);		

				detailFieldDef.setDisplayName(name);

				detailFieldDef.setAggregationFunction(TemplateUtils.strToAggfunctionClass(saikuColumn.getSelectedSummaryType()));

				//TODO: Wann ist die breite hier 0?
				Float colWidth = saikuColumn.getColumnHeaderFormat().getWidth();
				if(colWidth!=null){		

					if(columns.indexOf(saikuColumn) == columnCount -1){
						//	colWidth = Double.valueOf(100) - widthCumul;
					}
					Length width = new Length(LengthUnit.PERCENTAGE, Math.round(colWidth.doubleValue()*1000)/1000);
					log.info("col["+i+"]:" + colWidth);
					detailFieldDef.setWidth(width);				
					widthCumul+=colWidth;
				}

				detailFieldDef.setDataFormat(saikuColumn.getFormatMask());

				detailFields[i] = detailFieldDef;
				i++;			
			}
		}

		//Add the groups
		final List<GroupDefinition> groupDefs = new ArrayList<GroupDefinition>();

		List<SaikuGroup> sGroups = model.getGroups();

		final Class<CountDistinctFunction> aggFunctionClass = CountDistinctFunction.class;

		for (SaikuGroup saikuGroup : sGroups) {
			final GroupDefinition def = new DefaultGroupDefinition(GroupType.RELATIONAL, saikuGroup.getColumnName());			
			def.setAggregationFunction(aggFunctionClass);
			def.setDisplayName(saikuGroup.getColumnName());
			def.setGroupTotalsLabel(saikuGroup.getGroupTotalsLabel());
			groupDefs.add(def);
		}

		wizardSpec.setDetailFieldDefinitions(detailFields);	
		GroupDefinition[] groups = groupDefs.toArray(new GroupDefinition[groupDefs.size()]);
		wizardSpec.setGroupDefinitions(groups);

		return wizardSpec;
	}

}
