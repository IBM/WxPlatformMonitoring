package com.softwareag.wx.restServices.utils.labcase

import org.apache.http.entity.FileEntity
import groovy.swing.SwingBuilder
import groovy.transform.EqualsAndHashCode
import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.Method

@Log4j
class ReleaseNotes {

	String labcaseToken = null;

	File releaseNotesFile = null;
	def projectVersion = null

	def client = null;

	static void main(def args){
		ReleaseNotes u = new ReleaseNotes();
		u.parseCLI(args);
		u.init();
		String releaseNotes = u.getReleaseNotes();
		u.writeReleaseNotes(releaseNotes)
	}

	void writeReleaseNotes(String notes) {
		println "Writing release notes to file " + releaseNotesFile.absolutePath
		println "Release Notes: "  + notes 
		releaseNotesFile.write notes
	}
	
	void init() {
		initClient();
		getToken();
	}

	void parseCLI(def args) {
		def cli = new CliBuilder (usage:'ReleaseNotes.groovy -issueId LABCASE_ISSUE_ID -version VERSION -filePath PATH_TO_RELEASE_NOTES')
		cli.with {
			h longOpt:'help', 'Usage information'
			projectVersion longOpt:'projectVersion',argName:'projectVersion', args:1, '[optional] The project version, e.g. 1.1.0'
			filePath longOpt:'filePath', argName:'filePath', args:1, 'The path to where to store the release notes'
		}
		def opts = cli.parse(args)
		if(!opts) return
			if(opts.help) {
				cli.usage()
				return
			}
		
		assert opts
		assert opts.projectVersion
		assert opts.filePath
		
		this.projectVersion = opts.projectVersion
		this.releaseNotesFile = new File(opts.filePath)
		
		if( !releaseNotesFile.parentFile.exists() ) {
			releaseNotesFile.parentFile.mkdirs()
		}		
	}

	void initClient() {
		client = new groovyx.net.http.RESTClient('https://labcase.softwareag.com/')
		def pwd = ''
		def file = new File('labcase.pwd')
		if( file.exists() ) {
			pwd = file.text
		} else {
			println "password file 'labcase.pwd' does not exist in root dir " + new File(".").getAbsolutePath()
		}
		pwd = new String(pwd)
		client.auth.basic 'waa', pwd
	}

	String getProjectId() {
		client.get( path: 'projects/wxinterceptor.json' ) { resp, json ->
			println "> Project id for WxInterceptor is " + json.project.id
			return json.project.id
		}
	}
	
	/**
	 * Getting labcase version by converntion: 
	 * Labcase Version Name must end with the same version number as the project version
	 * E.g. "New Release 1.1.1", or "Version 1.0.0"
	 * @param projectVersion
	 * @param projectId
	 * @return
	 */
	String getLabcaseVersionByProjectVersion(projectVersion, projectId) {
		def labcaseVersionId = null
		println "Getting labcase version for project ${projectId} and release version ${projectVersion}"
		client.get( path: "projects/${projectId}/versions.json" ) { resp, json ->
			json.versions.each { version ->
					def versionId = version.id
					def versionName = version.name
					if( versionName.endsWith(projectVersion) ) {
						println "found Labcase version ${versionId} (Name: '${versionName}') for project version"
						labcaseVersionId = versionId
					}
			}
		}
		if( !labcaseVersionId ) {
			throw new RuntimeException("Did not find a Labcase Version for project version " + projectVersion)
		}
		return labcaseVersionId
	}
	
	String getReleaseNotes() {
		if( this.projectVersion ) {
			// lets try to get the release notes by 
			def projectId = this.getProjectId()
			def versionId = getLabcaseVersionByProjectVersion(this.projectVersion, projectId)
			getReleaseNotesForVersion(versionId)
		} else {
			throw new RuntimeException("Cannot create Release Notes without neither issueId, versionId nor project version number...");
		}
	}
	
	String getReleaseNotesForVersion(versionId) {
		println "Getting release info for version ${versionId}"
		// get all versions: https://labcase.softwareag.com/projects/${projectId}/versions.xml
		// get specific version: https://labcase.softwareag.com/versions/5227.json
		// get iisues for given version: https://labcase.softwareag.com/issues.xml?fixed_version_id=5227&project_id=6363
		client.get(
				path: "/versions/${versionId}.json",
				contentType: groovyx.net.http.ContentType.TEXT ) { resp, json ->
				println resp.status
				def jsonSlurper = new groovy.json.JsonSlurper()
				def version = jsonSlurper.parse(json).version;
				def versionSubject = version.name
				def versionDesc = version.description
				def projectId = version.project.id
				println "version: " + version
				println "Subject: " + versionSubject
				println "Project: " + projectId
				
				String nl = System.getProperty("line.separator");
				StringBuilder releaseNotes = new StringBuilder();
				releaseNotes.append("WxInterceptor");
				releaseNotes.append(nl);
				releaseNotes.append("Version @version@");
				releaseNotes.append(nl);
				releaseNotes.append("-------------------");
				if( versionDesc != null && !''.equals(versionDesc) ) {
					releaseNotes.append(nl);
					releaseNotes.append(nl);
					releaseNotes.append(versionDesc);
					releaseNotes.append(nl);
					releaseNotes.append(nl);
				}
				releaseNotes.append(nl);
				releaseNotes.append(nl);
				releaseNotes.append("Contents:");
				releaseNotes.append(nl);
				releaseNotes.append("---------");
				releaseNotes.append(nl);
				releaseNotes.append(getIssuesForVersion(versionId, projectId));
				return releaseNotes
		}
	}
	
	String getIssuesForVersion(versionId, projectId) {
		// get iisues for given version: https://labcase.softwareag.com/issues.xml?fixed_version_id=5227&project_id=6363
		/*
		 * Debug RestClient/HttpClient: https://stackoverflow.com/questions/7858238/how-to-output-the-generated-request-and-response-from-groovy-restclient
		 * Add the following parameters to groovy call:
		 * 	-Dorg.apache.commons.logging.Log=org.apache.commons.logging.impl.SimpleLog 
			-Dorg.apache.commons.logging.simplelog.showdatetime=true 
			-Dorg.apache.commons.logging.simplelog.log.org.apache.http=DEBUG 
		 */
		println "Getting Project Issues for project ${projectId} and version ${versionId}"
		client.get(
				path: "/issues.json",
				query: ['fixed_version_id': versionId, 'project_id': projectId],
				contentType: groovyx.net.http.ContentType.TEXT ) { resp, json ->
				def jsonSlurper = new groovy.json.JsonSlurper()
				def issues = jsonSlurper.parse(json).issues;
				StringBuilder issuesReleaseNotes = new StringBuilder() 
						String nl = System.getProperty("line.separator");
				for (def childIssue in issues) {
					println ">> Found issue ${childIssue.id} for version"
					issuesReleaseNotes.append("- [${childIssue.id}] ${childIssue.subject}");
					issuesReleaseNotes.append(nl);
				}
				return issuesReleaseNotes
		}
	}
	
	void getToken() {
		client.post( path: 'alfresco/login.json' ) { resp, json ->
			println resp.status
			println "Token: ${json.labcase.token}"
			labcaseToken = json.labcase.token;
		}
	}
}
