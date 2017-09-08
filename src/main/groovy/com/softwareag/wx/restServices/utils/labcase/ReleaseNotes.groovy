package com.softwareag.wx.restServices.utils.labcase

import org.apache.http.entity.FileEntity
import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.Method

@Log4j
class ReleaseNotes {

	String labcaseToken = null;
//	String projectId = null;
	// this is the id of the folder under Assets > build
	String uploadDirId = "ce714497-bdcd-4d1d-8f13-7826d3dc05d3_x";
	
	def issueId = null;
	File releaseNotesFile = null;
	def versionId = null;

	def client = null;

	static void main(def args){
		ReleaseNotes u = new ReleaseNotes();
		u.parseCLI(args);
		u.init();
		//u.uploadPackage();
		String releaseNotes = u.getReleaseNotes();
		u.writeReleaseNotes(releaseNotes)
	}

	void writeReleaseNotes(String notes) {
//		def releaseNotesFile = new File(filePath)
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
			issueId longOpt:'issueId',argName:'issueId', args:1, '[optional] The Labcase Issue Id from which to get the relase data'
			versionId longOpt:'versionId',argName:'versionId', args:1, '[optional] The labcase version id which defines this release'
			filePath longOpt:'filePath', argName:'filePath', args:1, 'The path to where to store the release notes'
		}
		def opts = cli.parse(args)
		println "-----"  + opts.issueId + " + " + args
		if(!opts) return
			if(opts.help) {
				cli.usage()
				return
			}
		
		assert opts
		assert opts.issueId || opts.versionId
		assert opts.filePath
		
		this.issueId = opts.issueId
		this.versionId = opts.versionId
		this.releaseNotesFile = new File(opts.filePath)
		
		assert releaseNotesFile.parentFile.exists()		
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

	String getReleaseNotes() {
		if( issueId ) {
			getReleaseNotesForIssue(issueId)
		} else if( versionId ) {
			getReleaseNotesForVersion(versionId)
		} else {
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
				releaseNotes.append("WxPlatformMonitoring");
				releaseNotes.append(nl);
				releaseNotes.append(versionSubject + " [${versionId}]");
				releaseNotes.append(nl);
				releaseNotes.append("@version@");
				releaseNotes.append(nl);
				releaseNotes.append("-------------------");
				releaseNotes.append(nl);
				releaseNotes.append(nl);
				releaseNotes.append(versionDesc);
				releaseNotes.append(nl);
				releaseNotes.append(nl);
				releaseNotes.append("Contents:");
				releaseNotes.append(nl);
				releaseNotes.append("--------");
				releaseNotes.append(nl);
				releaseNotes.append(getIssuesForVersion(versionId, projectId));
				return releaseNotes
		}
	}

	String getIssuesForVersion(versionId, projectId) {
		// get iisues for given version: https://labcase.softwareag.com/issues.xml?fixed_version_id=5227&project_id=6363
		client.get(
			path: "/issues.json",
			query: [fixed_version : versionId, project_id: projectId],
			contentType: groovyx.net.http.ContentType.TEXT ) { resp, json ->
			def jsonSlurper = new groovy.json.JsonSlurper()
			def issues = jsonSlurper.parse(json).issues;
			StringBuilder issuesReleaseNotes = new StringBuilder() 
			String nl = System.getProperty("line.separator");
			for (def childIssue in issues) {
				issuesReleaseNotes.append("- [${childIssue.id}] ${childIssue.subject}");
				issuesReleaseNotes.append(nl);
			}
			return issuesReleaseNotes
		}
	}
		
	String getReleaseNotesForIssue(issueId) {
		println "Getting release info for issue ${issueId}"
		client.get( 
				path: "/issues/${issueId}.json",
				query: [include : "children"],
				contentType: groovyx.net.http.ContentType.TEXT ) { resp, json ->
				println resp.status
				def jsonSlurper = new groovy.json.JsonSlurper()
				def issue = jsonSlurper.parse(json).issue;
				def issueSubject = issue.subject
						def issueDesc = issue.description
						def childIssues = issue.children
						println "issue: " + issue
						println "Subject: " + issue.subject
						
						String nl = System.getProperty("line.separator");
				StringBuilder releaseNotes = new StringBuilder();
				releaseNotes.append(issueSubject + " [${issueId}]");
				releaseNotes.append(nl);
				releaseNotes.append("@version@");
				releaseNotes.append(nl);
				releaseNotes.append("-------------------");
				releaseNotes.append(nl);
				releaseNotes.append(nl);
				releaseNotes.append(issueDesc);
				releaseNotes.append(nl);
				releaseNotes.append(nl);
				releaseNotes.append("Contents:");
				releaseNotes.append(nl);
				releaseNotes.append("--------");
				releaseNotes.append(nl);
				for (def childIssue in childIssues) {
					releaseNotes.append("- [${childIssue.id}] ${childIssue.subject}");
					releaseNotes.append(nl);
				}
				return releaseNotes
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
