/*** Create random UUID
${=java.util.UUID.randomUUID()}
***/

/*** assign custom property to other property
${#TestCase#UserID}
***/

Map loadCredentials(context){
	def groovyUtils = new com.eviware.soapui.support.GroovyUtils(context)
	def projectPath = groovyUtils.projectPath //gets the path of the project root

	def filename = "";
	//filename = "CarPark.properties";
	filename = context.testRunner.testCase.getPropertyValue( "propertiesFileName" );//context.expand( "${#TestCase#propertiesFileName}" );

	def response = new File(projectPath, "/"+filename);
	def properties = new Properties();
	response.withInputStream{ stream -> properties.load(stream) };

	def propsMap = [ : ]

	for ( prop in properties ){
		if ( prop.getKey()[0..1] != "//" ){
			//prop.minus([prop])
			//prop.remove(prop.getKey())
			propsMap.put(prop.getKey(), prop.getValue())
		}
	}

	return propsMap;
}

void setCredentialsInCustomProperties(context){
	def properties = loadCredentials(context);

	for ( propKey in properties.keySet() ){
		//context.testRunner.testCase.setPropertyValue(prop.getKey(), prop.getValue());
		context.testRunner.testCase.setPropertyValue(propKey, properties.get(propKey));
	}
}

void removeCredentialsInCustomProperties(context){
	def properties = loadCredentials(context);

	for ( propKey in properties.keySet() ){
		context.testRunner.testCase.removeProperty(propKey);
	}
}

void transferValue(context, fromPath, toPath){
	//import groovy.lang.Binding
	//import groovy.util.GroovyScriptEngine
	//import com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep

	def groovyUtils = new com.eviware.soapui.support.GroovyUtils( context )
	//int totalIdx = context.getTestCase().getTestStepCount()
	def targetPrevStep = context.getTestCase().findPreviousStepOfType(context.getCurrentStep(), com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep);
	def targetNextStep = context.getTestCase().findNextStepOfType(context.getCurrentStep(), com.eviware.soapui.impl.wsdl.teststeps.WsdlTestRequestStep);
	/*
	//int curIdx = context.getCurrentStepIndex()
	while(curIdx > 0){
		curIdx--;
		targetPrevStep = context.getTestCase().getTestStepAt(curIdx);
		if ( targetPrevStep.hasProperty("Endpoint") == true ){
			break;
		}else{
			targetPrevStep = null;
		}
	}
	*/

	def responseHolder = null;
	def targetValue = null;
	if ( targetPrevStep != null ){
		responseHolder = groovyUtils.getXmlHolder( targetPrevStep.getPropertyValue("Response"));
		//def responseHolder = groovyUtils.getXmlHolder( com.eviware.soapui.impl.wsdl.teststeps.WsdlTestStepWithProperties.RESPONSE );
		//def responseHolder = groovyUtils.getXmlHolder( "QueryByElements - Request 1#Response" );//"TestStepName#Response"
		//responseHolder.namespaces["ser"] = ...;
		targetValue = responseHolder.getNodeValue(fromPath);
		//String mySection = responseHolder["//CarPark[1]/CarParkID"];
		//log.info(targetValue);
	}

	def requestHolder = null;
	if ( targetNextStep != null ){
		requestHolder = groovyUtils.getXmlHolder( targetNextStep.getPropertyValue("Request"));
		if ( requestHolder.getDomNode(toPath) != null ){
			requestHolder.setNodeValue(toPath, targetValue);

			// write updated request back to teststep
			//requestHolder.updateProperty()
			targetNextStep.setPropertyValue("Request", requestHolder.xml)
			//context.requestContent = requestHolder.xml
		}
	}
}

Boolean check_existance(context, xPath){
	def responseHolder = null;
	def targetValue = null;

	def groovyUtils = new com.eviware.soapui.support.GroovyUtils( context )
	responseHolder = groovyUtils.getXmlHolder( context.getCurrentStep().getPropertyValue("Response"));
	targetValue = responseHolder.getNodeValue(xPath);
	if ( targetValue == null ){
		return false
	}else{
		return true
	}
}
