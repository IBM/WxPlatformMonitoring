package com.softwaerag.gcs.wx.platformmonitoring.utils.labcase

import org.apache.http.entity.FileEntity
import org.apache.log4j.PropertyConfigurator

import groovy.util.logging.Log4j
import groovyx.net.http.ContentType
import groovyx.net.http.Method

@Log4j
class UploadPackage {

	String labcaseToken = null;
	String projectId = null;

	def client = null;

	static void main(def args){
		UploadPackage u = new UploadPackage();
		u.init();
		//		u.zipPackage();
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
		
		
		def readln = javax.swing.JOptionPane.&showInputDialog
		def pwd = readln 'Labcase Password for user waa?'
		
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
		createDocument(filetoken);
	}

	String createDocument(String filetoken) {
		String jsonBody = '{"assets":[{"type":"file","name":"WxPlatformMonitoring.zip","title":"WxPlatformMonitoring.zip","description":"WxPlatformMonitoring.zip","token":"' + filetoken + '"}]}';
		// hardcoded to Assets > build folder in labcase
		String createDocumentPath = "projects/wxplatformmon/alfresco/documents/ce714497-bdcd-4d1d-8f13-7826d3dc05d3.json"
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

	String uploadDocument(File zipFile) {
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
		def ant = new AntBuilder()
		ant.zip(destfile: destination, basedir: 'is_packages/WxPlatformMonitoring')
		return destination;
	}
}
