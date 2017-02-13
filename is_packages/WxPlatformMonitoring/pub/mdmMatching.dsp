
<HTML>
<HEAD>
	<META http-equiv="Pragma" content="no-cache">
	<META http-equiv='content-type' content='text/html; charset=UTF-8'>
	<META http-equiv="Expires" content="-1">
	<TITLE>Reiff ESB - ERP Update Mail</TITLE>
	<LINK rel="stylesheet" type="text/css" href="reiff_esb.css">
	<SCRIPT SRC="webMethods.js.txt"></SCRIPT>
</HEAD>
<BODY style="border-width:0px;">
   <h1>Reiff ESB - MDM Matching Statistics</h1>
	<div class="box" style="padding:1em;">
		<h2>Matching Statistik</h2>
		<p class="text">
			Matching Statistiken im MDM
		</p>
		<div style="margin-bottom:4em;">
		<FORM name="filter" target="body" action="mdmMatching.dsp" METHOD="POST">
		<select name="u1" onchange="this.form.submit()">
			<option value="null">Unterscheidungsmerkmal1</option>
			%invoke reiff_administration.adapter:selectU1Distinct%
				%loop selectU1DistinctOutput/results%
					<option value="%value unterscheidungsmerkmal1%" %ifvar ../u1 vequals(unterscheidungsmerkmal1)% selected %end%>%value unterscheidungsmerkmal1%</option>
				%endLoop%
			%endInvoke%
		</select>
		<select name="u2" onchange="this.form.submit()">
			<option value="null">Unterscheidungsmerkmal2</option>
			%invoke reiff_administration.adapter:selectU2Distinct%
				%loop selectU2DistinctOutput/results%
					<option value="%value unterscheidungsmerkmal2%" %ifvar ../u2 vequals(unterscheidungsmerkmal2)% selected %end%>%value unterscheidungsmerkmal2%</option>
				%endLoop%
			%endInvoke%
		</select>
		<select name="u3" onchange="this.form.submit()">
			<option value="null">Unterscheidungsmerkma3</option>
			%invoke reiff_administration.adapter:selectU3Distinct%
				%loop selectU3DistinctOutput/results%
					<option value="%value unterscheidungsmerkmal3%" %ifvar ../u3 vequals(unterscheidungsmerkmal3)% selected %end%>%value unterscheidungsmerkmal3%</option>
				%endLoop%
			%endInvoke%
		</select>
		<select name="u4" onchange="this.form.submit()">
			<option value="null">Unterscheidungsmerkma4</option>
			%invoke reiff_administration.adapter:selectU4Distinct%
				%loop selectU4DistinctOutput/results%
					<option value="%value unterscheidungsmerkmal4%" %ifvar ../u4 vequals(unterscheidungsmerkmal4)% selected %end%>%value unterscheidungsmerkmal4%</option>
				%endLoop%
			%endInvoke%
		</select>
		<select name="mfrnr" onchange="this.form.submit()">
			<option value="null">Hersteller</option>
			%invoke reiff_administration.adapter:selectMFRNRDistinct%
				%loop selectMFRNRDistinctOutput/results%
					<option value="%value mfrnr_beschreibung%" %ifvar ../mfrnr vequals(mfrnr_beschreibung)% selected %end%>%value mfrnr_beschreibung%</option>
				%endLoop%
			%endInvoke%
		</select>
		</form>
		</div>
		
		%invoke reiff_administration.services.impl.mdmMatchingStatistics:getMdmMatchingStatistics%
			<table>
			<tr>
			  <td>
				<table>
				  <tr>
					<td>Artikel Anzahl</td>
					<td>%value all%</td>
				  </tr>
				  <tr>
					<td>Resis Artikel</td>
					<td>%value resis%</td>
				  </tr>
				  <tr>
					<td>Krupp Artikel</td>
					<td>%value krupp%</td>
				  </tr>
				  <tr>
					<td>Hanse Artikel</td>
					<td>%value hanse%</td>
				  </tr>
				  <tr>
					<td>Resis+Krupp Artikel</td>
					<td>%value kruppResis%</td>
				  </tr>
				  <tr>
					<td>Resis+Hanse Artikel</td>
					<td>%value hanseResis%</td>
				  </tr>
				  <tr>
					<td>Krupp+Hanse Artikel</td>
					<td>%value hanseKrupp%</td>
				  </tr>
				  <tr>
					<td>Krupp+Hanse+Resis Artikel</td>
					<td>%value hanseKruppResis%</td>
				  </tr>
				</table>			  
			  </td>
			  <td style="padding-left:4em;">
				<div class="simple_example"></div>
			  </td>
			</tr>
			</table>
		%endinvoke%
	</div>
   
</BODY>
<script src="js/d3/d3.min.js"></script>
<script src="js/venn.js/venn.js"></script>
<script src="js/jquery-2.1.1.min.js"></script>
<script>
// define sets and set set intersections
var sets = [{label: "Resis", size: %value resis%}, {label: "Krupp", size: %value krupp%}, {label: "Hanse", size: %value hanse%}],
    overlaps = [{sets: [0,1], size: %value kruppResis%}, {sets: [0,2], size: %value hanseResis%}, {sets: [1,2], size: %value hanseKrupp%},{sets: [0,1,2], size: %value hanseKruppResis%}];

// get positions for each set
sets = venn.venn(sets, overlaps);

// draw the diagram in the 'simple_example' div
venn.drawD3Diagram(d3.select(".simple_example"), sets, 300, 300);


</script>
</HTML>