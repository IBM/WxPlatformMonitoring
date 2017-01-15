package wx.platformMonitoring.services_1.impl.processes;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 00:58:14 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
// --- <<IS-END-IMPORTS>> ---

public final class monitoring

{
	// ---( internal utility methods )---

	final static monitoring _instance = new monitoring();

	static monitoring _newInstance() { return new monitoring(); }

	static monitoring _cast(Object o) { return (monitoring)o; }

	// ---( server methods )---




	public static final void analyzeProcessSteps (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(analyzeProcessSteps)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required steps
		// [i] object:0:required compiledSpec
		// [o] field:0:required match {"true","false"}
		// [o] field:0:required cause
		// [o] object:0:required firstIterationStartDate
		// [o] object:0:required lastIterationStartDate
		// [o] object:0:required lastIterationEndDate
		// [o] field:0:required lastIterationDurationInSec
		// pipeline
		IDataCursor pipelineInCursor = pipeline.getCursor();
		
		ProcessSpec procSpec = (ProcessSpec) IDataUtil.get(pipelineInCursor, "compiledSpec");
		IData[] steps = IDataUtil.getIDataArray( pipelineInCursor, "steps" );
		
		pipelineInCursor.destroy();
		
		if (procSpec == null) {
		    throw new ServiceException("'compiledSpec' may not be null");
		}
		
		if (steps == null) {
		    steps = new IData[0];
		}
		
		//
		// Schritte vorsortieren und die Iteration mit der h\u00F6chsten Nummer ermitteln
		//
		
		Set<String> inclStepIds = new HashSet<String>(); // Steps, die noch gefunden werden m\u00FCssen
		for (int i = 0; i < procSpec.inclSteps.length; i++) {
		    inclStepIds.add(procSpec.inclSteps[i].getStepId());
		}
		Set<String> exclStepIds = new HashSet<String>(); // Steps, die nicht gefunden werden d\u00FCrfen
		for (int i = 0; i < procSpec.exclSteps.length; i++) {
		    exclStepIds.add(procSpec.exclSteps[i].getStepId());
		}
		
		int minIteration = Integer.MAX_VALUE;
		int maxIteration = -1;
		
		// Schritte, gruppiert nach Prozessiteration
		Map<Integer, List<IData>> stepsByProcIteration = new HashMap<Integer, List<IData>>();
		
		for (IData step : steps)
		{
		    IDataCursor stepCursor = step.getCursor();
		    String stepId = IDataUtil.getString( stepCursor, "STEPID" );
		    int iter = IDataUtil.getInt( stepCursor, "INSTANCEITERATION", -1 );
		    stepCursor.destroy();
		
		    if (iter > maxIteration) {
		        maxIteration = iter;
		    }
		
		    if (iter < minIteration) {
		        minIteration = iter;
		    }
		
		    if (iter == -1) {
		        // Komisch, sollte nie vorkommen. Was tun?
		        // TODO: ProcessIteration nicht vorhanden. Was tun?
		    }
		    List<IData> iterSteps = stepsByProcIteration.get(iter);
		    if (iterSteps == null) {
		        iterSteps = new ArrayList<IData>();
		        stepsByProcIteration.put(iter, iterSteps);
		    }
		    iterSteps.add(step);
		
		    inclStepIds.remove(stepId); // Wir haben diesen Schritt "gesehen"
		}
		
		boolean match = inclStepIds.isEmpty();
		String cause = null;
		
		if (match) {
		    // Schauen, dass die Iteration mit der h\u00F6chsten Nummer keinen verbotenen Schritt enth\u00E4lt
		    List<IData> lastIterSteps = stepsByProcIteration.get(maxIteration);
		    for (IData step : lastIterSteps) {
		        IDataCursor stepCursor = step.getCursor();
		        String stepId = IDataUtil.getString( stepCursor, "STEPID" );
		        int iter = IDataUtil.getInt( stepCursor, "INSTANCEITERATION", -1 );
		        stepCursor.destroy();
		        
		        if (exclStepIds.contains(stepId)) {
		            match = false;
		            cause = "Step '" + stepId + "' has been executed (highest proc iteration = " + maxIteration + ")";
		            break;
		        }
		    }
		} else {
		    cause = "Step '" + inclStepIds.iterator().next() + "' has not been executed";
		}
		
		// Start der ersten Prozessiteration bestimmen
		Date firstIterStart = null;
		for (IData step : stepsByProcIteration.get(minIteration)) {
		    IDataCursor stepCursor = step.getCursor();
		    Number stamp = (Number) IDataUtil.get( stepCursor, "AUDITTIMESTAMP");
		    stepCursor.destroy();
		    if (stamp == null) {
		        // Sollte nie vorkommen, aber sicherheitshalber pr\u00FCfen
		        continue;
		    }
		    Date date = new Date(stamp.longValue());
		    if ((firstIterStart == null) || (date.compareTo(firstIterStart) < 0)) {
		        firstIterStart = date;
		    }
		}
		
		// Start und Ende der letzten Prozessiteration bestimmen
		Date lastIterStart = null;
		Date lastIterEnd = null;
		for (IData step : stepsByProcIteration.get(maxIteration)) {
		    IDataCursor stepCursor = step.getCursor();
		    Number stamp = (Number) IDataUtil.get( stepCursor, "AUDITTIMESTAMP");
		    stepCursor.destroy();
		    if (stamp == null) {
		        // Sollte nie vorkommen, aber sicherheitshalber pr\u00FCfen
		        continue;
		    }
		    Date date = new Date(stamp.longValue());
		    if ((lastIterStart == null) || (date.compareTo(lastIterStart) < 0)) {
		        lastIterStart = date;
		    }
		    if ((lastIterEnd == null) || (date.compareTo(lastIterEnd) > 0)) {
		        lastIterEnd = date;
		    }
		}
		
		
		// pipeline
		IDataCursor pipelineOutCursor = pipeline.getCursor();
		IDataUtil.put( pipelineOutCursor, "match", Boolean.toString(match) );
		if (!match) {
		    IDataUtil.put( pipelineOutCursor, "cause", cause );
		}
		IDataUtil.put( pipelineOutCursor, "firstIterationStartDate", firstIterStart );
		IDataUtil.put( pipelineOutCursor, "lastIterationStartDate", lastIterStart );
		IDataUtil.put( pipelineOutCursor, "lastIterationEndDate", lastIterEnd );
		IDataUtil.put( pipelineOutCursor, "lastIterationDurationInSec",
		        Long.toString((lastIterEnd.getTime() - lastIterStart.getTime()) / 1000));
		
		pipelineOutCursor.destroy();
		// --- <<IS-END>> ---

                
	}



	public static final void compileProcessSpec (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(compileProcessSpec)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] recref:0:required uncompiledSpec administrationruv.doc.process.monitoring:WatchedProcessSpec
		// [o] object:0:required compiledSpec
		// pipeline
		IDataCursor pipelineInCursor = pipeline.getCursor();
		
		// procSpec
		IData procSpec = IDataUtil.getIData(pipelineInCursor, "uncompiledSpec");
		
		pipelineInCursor.destroy();
		
		if (procSpec == null) {
		    throw new ServiceException("'uncompiledSpec' may not be null");
		}
		
		IDataCursor procSpecCursor = procSpec.getCursor();
		
		// must-be-steps
		List<StepSpec> inclStepList = new ArrayList<StepSpec>();
		IData must_be_steps = IDataUtil.getIData( procSpecCursor, "must-be-steps" );
		if (must_be_steps != null)
		{
		    IDataCursor stepListCursor = must_be_steps.getCursor();
		    IData[] step_spec = IDataUtil.getIDataArray( stepListCursor, "step-spec" );
		    if (step_spec != null)
		    {
		        for (int i = 0; i < step_spec.length; i++)
		        {
		            IDataCursor stepCursor = step_spec[i].getCursor();
		            String step_id = IDataUtil.getString( stepCursor, "step-id" );
		            String step_name = IDataUtil.getString( stepCursor, "step-name" );
		            stepCursor.destroy();
		            inclStepList.add(new StepSpec(step_id, step_name));
		        }
		    }
		    stepListCursor.destroy();
		}
		
		// may-not-be-steps
		List<StepSpec> exclStepList = new ArrayList<StepSpec>();
		IData may_not_be_steps = IDataUtil.getIData( procSpecCursor, "may-not-be-steps" );
		if (may_not_be_steps != null)
		{
		    IDataCursor stepListCursor = may_not_be_steps.getCursor();
		    IData[] step_spec = IDataUtil.getIDataArray( stepListCursor, "step-spec" );
		    if (step_spec != null)
		    {
		        for (int i = 0; i < step_spec.length; i++)
		        {
		            IDataCursor stepCursor = step_spec[i].getCursor();
		            String step_id = IDataUtil.getString( stepCursor, "step-id" );
		            String step_name = IDataUtil.getString( stepCursor, "step-name" );
		            stepCursor.destroy();
		            exclStepList.add(new StepSpec(step_id, step_name));
		        }
		    }
		    stepListCursor.destroy();
		}
		
		procSpecCursor.destroy();
		
		ProcessSpec compiledSpec = new ProcessSpec(
		        inclStepList.toArray(new StepSpec[inclStepList.size()]),
		        exclStepList.toArray(new StepSpec[exclStepList.size()]));
		
		// pipeline
		IDataCursor pipelineOutCursor = pipeline.getCursor();
		IDataUtil.put( pipelineOutCursor, "compiledSpec", compiledSpec );
		pipelineOutCursor.destroy();
		// --- <<IS-END>> ---

                
	}

	// --- <<IS-START-SHARED>> ---
	private static class StepSpec {
	    private String stepId;
	    private String stepName;
	    private StepSpec(String id, String name) {
	        stepId = id;
	        stepName = name;
	    }
	    public String getStepId() {
	        return stepId;
	    }
	    public String getStepName() {
	        return stepName;
	    }
	    
	    @Override
	    public String toString() {
	        return stepId;
	    }
	}
	
	// Beschreibung eines Prozesses
	private static class ProcessSpec {
	    /** Alle Schritte, die mindestens einmal durchlaufen sein m\u00FCssen -- egal in welcher Prozessiteration */
	    private StepSpec[] inclSteps;
	
	    /** Schritte, die in der letzten Prozessiteration nicht durchlaufen sein d\u00FCrfen */
	    private StepSpec[] exclSteps;
	
	    public ProcessSpec(StepSpec[] inclSteps, StepSpec[] exclSteps) {
	        this.inclSteps = inclSteps;
	        this.exclSteps = exclSteps;
	    }
	    
	    @Override
	    public String toString() {
	        return "InclSteps: " + inclSteps + ", ExclSteps: " + exclSteps;
	    }
	}
	// --- <<IS-END-SHARED>> ---
}

