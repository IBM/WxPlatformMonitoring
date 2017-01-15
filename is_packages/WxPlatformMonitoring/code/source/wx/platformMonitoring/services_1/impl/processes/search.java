package wx.platformMonitoring.services_1.impl.processes;

// -----( IS Java Code Template v1.2
// -----( CREATED: 2017-01-15 00:58:16 CET
// -----( ON-HOST: 192.168.221.165

import com.wm.data.*;
import com.wm.util.Values;
import com.wm.app.b2b.server.Service;
import com.wm.app.b2b.server.ServiceException;
// --- <<IS-START-IMPORTS>> ---
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
// --- <<IS-END-IMPORTS>> ---

public final class search

{
	// ---( internal utility methods )---

	final static search _instance = new search();

	static search _newInstance() { return new search(); }

	static search _cast(Object o) { return (search)o; }

	// ---( server methods )---




	public static final void filterStepList (IData pipeline)
        throws ServiceException
	{
		// --- <<IS-START(filterStepList)>> ---
		// @subtype unknown
		// @sigtype java 3.5
		// [i] record:1:required controlSteps
		// [i] - field:0:required stepId
		// [i] - field:0:required stepStatus {"Started","Completed","Failed"}
		// [i] record:1:required excludeSteps
		// [i] - field:0:required stepId
		// [i] - field:0:required stepStatus {"Started","Completed","Failed"}
		// [i] record:1:required instanceSteps
		// [i] field:0:required instanceIteration
		// [o] field:0:required foundExcludeStep
		// [o] field:0:required foundAllControlSteps
		IDataCursor pipelineCursor = pipeline.getCursor();
		
		Map controlStepsMap = new HashMap();
		Set excludeStepsSet = new HashSet();
		
		String instanceIteration = IDataUtil.getString(pipelineCursor, "instanceIteration");
		
		// controlSteps
		IData[] controlSteps = IDataUtil.getIDataArray(pipelineCursor, "controlSteps");
		if (controlSteps != null) {
			for (int i = 0; i < controlSteps.length; i++) {
				IDataCursor controlStepsCursor = controlSteps[i].getCursor();
				String stepId = IDataUtil.getString(controlStepsCursor, "stepId");
				String stepStatus = IDataUtil.getString(controlStepsCursor, "stepStatus");
				controlStepsMap.put(stepId, stepStatus);
				controlStepsCursor.destroy();
			}
		}
		
		// excludeSteps
		IData[] excludeSteps = IDataUtil.getIDataArray(pipelineCursor, "excludeSteps");
		if (excludeSteps != null) {
			for (int i_1 = 0; i_1 < excludeSteps.length; i_1++) {
				IDataCursor excludeStepsCursor = excludeSteps[i_1].getCursor();
				String stepId = IDataUtil.getString(excludeStepsCursor, "stepId");
				excludeStepsSet.add(stepId);
				excludeStepsCursor.destroy();
			}
		}
		
		// instanceSteps
		com.wm.util.Table instanceSteps = (com.wm.util.Table)IDataUtil.get(pipelineCursor, "instanceSteps");
		if (instanceSteps == null) {
			IDataUtil.put(pipelineCursor, "foundExcludeStep", "false");
			IDataUtil.put(pipelineCursor, "foundAllControlSteps", Boolean.toString(controlStepsMap.isEmpty()));
			pipelineCursor.destroy();
			return;
		}
		IData[] instanceStepData = instanceSteps.getItems();
		for (IData instanceStep : instanceStepData) {
			IDataCursor instanceStepCursor = instanceStep.getCursor();
			String stepId = IDataUtil.getString(instanceStepCursor, "STEPID");
			// check if this stepid is listed in the excludesteps
			if (excludeStepsSet.contains(stepId)) {
				String stepInstanceIteration = ((Integer) IDataUtil.getInt(
						instanceStepCursor, "INSTANCEITERATION", -1))
						.toString();
				if (stepInstanceIteration.equals(instanceIteration)) {
					// end here, we found one exclude step in the current
					// process iteration
					IDataUtil.put(pipelineCursor, "foundExcludeStep", "true");
					pipelineCursor.destroy();
					return;
				}
			}
			// check if this step id is listed in the control steps
			if (controlStepsMap.containsKey(stepId)) {
				String stepInstanceIteration = ((Integer) IDataUtil.getInt(
						instanceStepCursor, "INSTANCEITERATION", -1))
						.toString();
				if (stepInstanceIteration.equals(instanceIteration)) {
					// check if the status is ok
					String stepStatus = ((Integer) IDataUtil.getInt(
							instanceStepCursor, "STATUS", -1)).toString();
					String stepStatusText = IDataUtil.getString(instanceStepCursor, "STATUSDECODE");
					String controlSetStepStatus = controlStepsMap.get(stepId).toString();
					if (stepStatus.equals(controlSetStepStatus) || stepStatusText.equals(controlSetStepStatus)) {
						// we found a control step --> remove this step from the
						// controlset
						controlStepsMap.remove(stepId);
					}
				}
			}
		}
		
		// Gibt es noch "unerledigte" Schritte?
		String foundAllControlSteps = Boolean.toString(controlStepsMap.isEmpty());
		
		// pipeline
		IDataUtil.put(pipelineCursor, "foundExcludeStep", "false");
		IDataUtil.put(pipelineCursor, "foundAllControlSteps", foundAllControlSteps);
		pipelineCursor.destroy();
		// --- <<IS-END>> ---

                
	}
}

