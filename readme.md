# WxPlatformMonitoring
The following describes the most important steps for creating a new version.


## 1 Checkout
Checkout the Git Project, make sure that nested projects are imported. You should see two projects in the Java Perspective: 
* wxplatformmon... the main project
* WxPlatformMonitoring_Package ... the actual package


## 2 Build the package and install it at the IS
Configure desired build version and the build environment (e.g. 10.15) in the file build.properties

Execute Target "installWxPlatformMonitoringPackage" in wxplatformmon/build.xml. This tasks performs these actions:

1. Copy package to the Integration Server into a temp package
2. Execute Jcode in order to build and frag all java services
3. Delete the temp package on the Integration Server
4. Copy the package back to wxplatformmon/build/tmp
5. Update the version and the build number (current git revision) in the manifest.v3
6. Zip the package and save it to the build directory at: wxplatformmon/build/<version>/WxPlatformMonitoring.zip
7. Unzip the build into the IS packages directory
8. Activate the package


## 3 Fetch the package from the IS
When finished modifying the package, the package needs to be copied back to the GIT project.

Execute Target "fetchWxPlatformMonitoringPackageFromIntegrationServer" in wxplatformmon/build.xml. 


## 4 Checkin to GIT
Check-in all updates.


## 5 Rebuild the package
Execute Target "createWxPlatformMonitoringBuild" in wxplatformmon/build.xml. 


## 6 Publish the new build to Labcase
Copy the wxplatformmon/build/<version>/WxPlatformMonitoring.zip to Labcase assets
