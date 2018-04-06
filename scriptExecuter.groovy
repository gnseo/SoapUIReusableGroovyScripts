/************
 ************ in Groovy Script TestStep
************/
def obj = context.getTestCase().getTestSuite().getProject().myObject(context)
obj.loadCredentials(context)
obj.transferValue(context,"//ProductionOrderOutputProducts[1]/MaterialOutputUUID","//MaterialOutput[1]/MaterialOutputUUID")

/************
************ To change username, password of requests of binding endpoints
************ "Load Script" in Project level
************/
def alert = com.eviware.soapui.support.UISupport;

for( itf in project.getInterfaceList()){
	for( opr in itf.getOperationList()){
		//if( opr.getName() == "FindByCommunicationData" ){
			for( child in opr.getChildren() ){
				if ( child.getClass().getName() == "com.eviware.soapui.impl.wsdl.WsdlRequest" ){
					child.setUsername("\${#Project#ID}");
					child.setPassword("\${#Project#PW}");
				}
			}
		//}
	}
}
/************
 ************ "Load Script" in Project level
************/
import groovy.lang.Binding
import groovy.util.GroovyScriptEngine

def groovyUtils = new com.eviware.soapui.support.GroovyUtils( context )

// location of script file is relative to SOAPUI project file.
//String scriptPath = groovyUtils.projectPath + "/groovy-scripts/"
String scriptPath = groovyUtils.projectPath;
scriptPath = scriptPath.find(~/(.*)\\(.*$)/) { match, parentPath, lastFolder -> return parentPath }
scriptPath = scriptPath + "/groovy-scripts/"

// Create Groovy Script Engine to run the script.
GroovyScriptEngine gse = new GroovyScriptEngine(scriptPath)

// Load the Groovy Script file
externalScript = gse.loadScriptByName("reuse.groovy")

project.metaClass.myObject {
    //externalScript.newInstance(context: it, log: log, testRunner: it.testRunner)
    externalScript.newInstance()
}
/*
// Create a runtime instance of script
instance = externalScript.newInstance()

// Sanity check
assert instance!= null

// run the foo method in the external script
instance.loadCredentials(context)
*/
