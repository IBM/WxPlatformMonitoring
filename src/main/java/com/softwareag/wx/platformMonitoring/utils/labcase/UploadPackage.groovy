package com.softwareag.wx.platformMonitoring.utils.labcase


@Grapes([
    @Grab(group='commons-logging', module='commons-logging', version='1.2'),
	@Grab(group='org.springframework', module='spring-core', version='4.3.5.RELEASE'),
	@Grab(group='org.apache.httpcomponents', module='httpcore', version='4.3.3'),
	@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.0'),
	@Grab(group='org.apache.ant', module='ant', version='1.9.8'),
	@Grab(group='org.apache.ant', module='ant-launcher', version='1.9.8')
]
)
import org.apache.http.entity.FileEntity
import org.apache.log4j.PropertyConfigurator

import groovy.lang.Grab
import groovy.lang.Grapes
import groovy.swing.SwingBuilder
import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.Method


@Log4j
class UploadPackage {

	String labcaseToken = null;
	String projectId = null;
	// this is the id of the folder under Assets > build
	String uploadDirId = "ce714497-bdcd-4d1d-8f13-7826d3dc05d3";
	String packageAssetName = "WxPlatformMonitoring.zip";

	def client = null;

	static void main(def args){
		UploadPackage u = new UploadPackage();
		u.init();
		u.uploadPackage();

	}

	void init() {
		initLogging();
		initClient();
		getToken();
		getProjectId();
	}

	void initLogging() {
		def config = new ConfigSlurper().parse(new File('config/log4j.groovy').toURL())
		PropertyConfigurator.configure(config.toProperties())
	}



	void initClient() {
		client = new groovyx.net.http.RESTClient('https://labcase.softwareag.com/')

		//		def readln = javax.swing.JOptionPane.&showInputDialog
		//		def pwd = readln 'Labcase Password for user waa?'

		def pwd = ''
		if(System.console() == null) {
			new SwingBuilder().edt {
				dialog(modal: true, // Otherwise the build will continue running before you closed the dialog
				title: 'Enter password for labcase', // Dialog title
				alwaysOnTop: true, // pretty much what the name says
				resizable: false, // Don't allow the user to resize the dialog
				locationRelativeTo: null, // Place dialog in center of the screen
				pack: true, // We need to pack the dialog (so it will take the size of it's children)
				show: true // Let's show it
				) {
					vbox {
						// Put everything below each other
						label(text: "Please enter labcase password for waa:")
						input = passwordField()
						button(defaultButton: true, text: 'OK', actionPerformed: {
							pwd = input.password; // Set pass variable to value of input field
							dispose(); // Close dialog
						})
					} // vbox end
				} // dialog end
			} // edt end
		} else {
			pwd = System.console().readPassword("\nPlease enter key passphrase: ")
		}

		if(pwd.size() <= 0) {
			throw new RuntimeException("You must enter a password to proceed.")
		}
		pwd = new String(pwd)
		client.auth.basic 'waa', pwd
	}

	void getToken() {
		client.post( path: 'alfresco/login.json' ) { resp, json ->
			println resp.status
			println "Token: ${json.labcase.token}"
			labcaseToken = json.labcase.token;
		}
	}

	void getProjectId() {
		client.get( path: 'projects/wxplatformmon/alfresco/documents.json' ) { resp, json ->
			println resp.status
			println "Project id: ${json.asset.id}"
			projectId = json.asset.id
		}
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
		String updateDocumentPath = "projects/wxplatformmon/alfresco/documents/" + assetId + ".json"
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
		String createDocumentPath = "projects/wxplatformmon/alfresco/documents/" + uploadDirId + ".json"
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
		String uploadDirPath = "projects/wxplatformmon/alfresco/documents/" + uploadDirId + ".json"
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
