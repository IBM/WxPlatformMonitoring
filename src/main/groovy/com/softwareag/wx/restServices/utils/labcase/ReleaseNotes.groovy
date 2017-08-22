package com.softwareag.wx.restServices.utils.labcase

import org.apache.http.entity.FileEntity
import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.Method

@Log4j
class ReleaseNotes {

	String labcaseToken = null;
	String projectId = null;
	// this is the id of the folder under Assets > build
	String uploadDirId = "ce714497-bdcd-4d1d-8f13-7826d3dc05d3_x";
	
	def issueId = null;
	File releaseNotesFile = null;
	def version = null;
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
		releaseNotesFile.write notes
	}
	
	void init() {
		initClient();
		getProjectId();
		getToken();
	}

	void parseCLI(def args) {
		def cli = new CliBuilder (usage:'ReleaseNotes.groovy -issueId LABCASE_ISSUE_ID -version VERSION -filePath PATH_TO_RELEASE_NOTES')
		cli.with {
			h longOpt:'help', 'Usage information'
			version longOpt:'version',argName:'version', args:1, 'The version for this release'
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
		assert opts.version
		
		this.version = opts.version
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
			listVersions()
			getReleaseNotesForVersion(versionId)
		} else {
		}
	}
	
	void listVersions() {
		client.get( path: 'projects/${projectId}/versions.json' ) { resp, json ->
			println "> having ${json.versions.@total_count} versions"
			json.versions.each { version ->
					println "${version.id}: ${version.name}"
			}
		}
	}
	
	String getReleaseNotesForVersion(versionId) {
		println "Getting release info for version ${versionId}"
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

	void getProjectId() {
		client.get( path: 'projects/wxrestservices.json' ) { resp, json ->
//			println resp.status
//			println "Project id: ${json.project}"
			projectId = json.project.id
		}
		println "> Project id for wxrestservices is $projectId"
	}

	void uploadPackage() {
		String zipDestination = zipPackage();
		def file = new File(zipDestination)
		String filetoken = uploadDocument(file);
		println "got file token "  + filetoken
		String assetId = getPackageAssetIdIfExists();
		if( assetId == null ) {
			createDocument(filetoken);
		} else {
			updateDocument(filetoken, assetId);
		}
	}

	void updateDocument(String filetoken, String assetId) {
		println "updating document in labcase (filetoke: " + filetoken + ", assetId: "  + assetId + ")"
		String jsonBody = '{"asset":{"token":"' + filetoken + '"}}';
		String updateDocumentPath = "projects/wxrestservices/alfresco/documents/" + assetId + ".json"
		println "jsonBody: " + jsonBody;
		println "path: " + updateDocumentPath;
		client.request( Method.PUT, ContentType.JSON ) { req ->
			uri.path = updateDocumentPath
			headers.Accept = 'application/json'
			body = jsonBody
			response.success = { resp, reader ->
				println "Got response: ${resp.statusLine}"
				println "Content-Type: ${resp.headers.'Content-Type'}"
			}
		}
	}

	String createDocument(String filetoken) {
		println "creating new document in labcase for uploaded package"
		String jsonBody = '{"assets":[{"type":"file","name":"' + packageAssetName + '","title":"' + packageAssetName + '","description":"' + packageAssetName + '","token":"' + filetoken + '"}]}';
		// hardcoded to Assets > build folder in labcase
		String createDocumentPath = "projects/wxrestservices/alfresco/documents/" + uploadDirId + ".json"
		println "jsonBody: " + jsonBody;
		println "path: " + createDocumentPath;

		client.request( Method.POST, ContentType.JSON ) { req ->
			uri.path = createDocumentPath
			headers.Accept = 'application/json'
			body = jsonBody
			response.success = { resp, reader ->
				println "Got response: ${resp.statusLine}"
				println "Content-Type: ${resp.headers.'Content-Type'}"
				print reader.text
			}
		}
	}



	String getPackageAssetIdIfExists() {
		println "checking if package has been uploaded already"
		String uploadDirPath = "projects/wxrestservices/alfresco/documents/" + uploadDirId + ".json"
		String id = null;
		client.get(path: uploadDirPath) { resp, json ->
			println "folder listing: "  + json
			def children = json.asset.children;
			children.each() { child ->
				println "child: " + child
				if( child.name == packageAssetName ) {
					println "package has been uploaded already (id: ${child.id})"
					id= child.id;
				}
			}
		}
		return id;
	}

	String uploadDocument(File zipFile) {
		println "uploading document to labcase.... this could take a while..."
		client.encoder.'application/octet-stream' = this.&encodeZipFile
		client.post( path: "uploads.json", body: zipFile, requestContentType: 'application/octet-stream' ) { resp, json ->
			println resp.status
			println json.upload.token
			return json.upload.token
		}
	}

	def encodeZipFile( Object data ) throws UnsupportedEncodingException {
		if ( data instanceof File ) {
			def entity = new FileEntity( (File) data, "application/octet-stream" );
			entity.setContentType( "application/octet-stream" );
			return entity
		} else {
			throw new IllegalArgumentException(
			"Don't know how to encode ${data.class.name} as a zip file" );
		}
	}

	String zipPackage() {
		def destination = "build/WxPlatformMonitoring.zip";
		// on ClassNotFoundException java.lang.NoClassDefFoundError: org/apache/tools/ant/BuildException
		// http://stackoverflow.com/questions/13216875/antbuilder-works-in-groovy-console-but-not-in-eclipse#13222973
		def ant = new AntBuilder()
		ant.zip(destfile: destination, basedir: 'is_packages/WxPlatformMonitoring')
		return destination;
		//		println "!!!!!!!!!!!!!!!!!!!!!!! returning dummy zip"
		//		return "build/tmp.zip";
	}
}
