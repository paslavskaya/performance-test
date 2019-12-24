#!/usr/bin/env groovy
import groovy.json.*
import hudson.model.*
import org.apache.commons.csv.CSVFormat

def workspace = pwd()
def parametersFilePath = "$workspace/Parameters.json"
def shopAccountsFilePath = "$workspace/users.csv"
def searchTermsFilePath = "$workspace/searchTerms.csv"
def testDataCSVFilePath = "$workspace/TestData.csv"
def saveTestDataScriptPath = "$workspace/SaveTestData.groovy"

def testsPath = "$JENKINS_HOME/workspace/Performance test"
def configurationSchemaFilePath = "$testsPath/ConfigurationSchema.groovy"

pipeline {
    agent none
    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
    }
    parameters {
        extendedChoice(
            name: 'Config',
            bindings: "parametersFilePath=$parametersFilePath",
            description: '',
            groovyScriptFile: "$configurationSchemaFilePath",
            type: 'PT_JSON')     
    }
    
    stages {
        stage ('Save configuration') {
            steps {
                script {
                    if (params){
                        fileOperations([folderCopyOperation(destinationFolderPath: "$workspace", sourceFolderPath: "$testsPath")])

                        saveParameters("$parametersFilePath")
                        saveTestDataIntoCSV("$parametersFilePath", "$testDataCSVFilePath", "$saveTestDataScriptPath")
                        saveShopAccounts("$shopAccountsFilePath")       
                        saveSearchTerms("$searchTermsFilePath")    
                        archiveArtifacts artifacts: 'TestData.csv,users.csv,searchTerms.csv,Parameters.json,results/**'                        
                    }                     
                }  
                stash includes: '**', name: 'everything'                       
            }  
        }    

        stage ('Run tests') {
            agent {
                kubernetes {
                    label 'jnlp'
                    defaultContainer 'jnlp'
                    containerTemplate(name: 'jmeter', image: 'egaillardon/jmeter', command: 'cat', ttyEnabled: true, alwaysPullImage: true)
                    containerTemplate(name: 'pluggin', image: 'egaillardon/jmeter-plugins', command: 'cat', ttyEnabled: true, alwaysPullImage: true)
                }
            }
            steps {
                container('jnlp'){
                   unstash 'everything'                   
                }

                container('jmeter') {
                    container('pluggin'){
                         script{
                            def containerWorkspace = pwd()
                            sh 'jmeter -n -t DemoSana.jmx -JPath="' + "${containerWorkspace}" + '" -l test_results.jtl -j test_results.log'
                            archiveArtifacts artifacts: 'test_results.log'
                        }
                    }
                } 
            }  
        }
    }
    post{
        always{
            script{
                def grafana_report = "http://13.95.69.163/d/ltaas/jmeter-metric-template?orgId=1&refresh=30s"
                // get build start and end time
                def start = currentBuild.getStartTimeInMillis();
                def end = currentBuild.getDuration();
                // replace time
                grafana_report = String.format(grafana_report, start, end);
                def link = "<a href='%s'>%s</a><br/>";
                def sb = new StringBuilder();
                sb.append(String.format(link, grafana_report, "Grafana Performance Result"));
                currentBuild.setDescription(sb.toString());
            }
        }
    }
}

def saveParameters(String parametersFilePath) {
    echo 'Update Parameters.json file with user parameters'    
    def jsonParametersConfig = getParametersJsonConfig("$parametersFilePath")
    def jsonParams = readJSON text: "$params.Config"        

    jsonParametersConfig.websiteUrl = jsonParams.websiteUrl  
    jsonParametersConfig.language = jsonParams.language  
    jsonParametersConfig.ntPopupUserName = jsonParams.ntPopupUserName
    jsonParametersConfig.ntPopupPassword = jsonParams.ntPopupPassword
    jsonParametersConfig.rampUpTime = jsonParams.rampUpTime
    if (jsonParams.scenarios.a_login.enabled)
    {
        jsonParametersConfig.login_enabled = "true"
        jsonParametersConfig.login_usersCount = jsonParams.scenarios.a_login.usersCount
        jsonParametersConfig.login_users = jsonParams.scenarios.a_login.users
    }       
    else
        jsonParametersConfig.login_enabled = "false"
        jsonParametersConfig.login_usersCount = jsonParams.scenarios.a_login.guestsCount

    if (jsonParams.scenarios.b_plp.enabled)
    {
        jsonParametersConfig.plp_enabled = "true"
        jsonParametersConfig.plp_usersCount = jsonParams.scenarios.b_plp.usersCount
        jsonParametersConfig.plp_navigationItem = jsonParams.scenarios.b_plp.navigationItem
        jsonParametersConfig.plp_loopCount = jsonParams.scenarios.b_plp.loopCount
    }
    else    
        jsonParametersConfig.plp_enabled = "false"

    if (jsonParams.scenarios.c_search.enabled)
    {
        jsonParametersConfig.search_enabled = "true"
        jsonParametersConfig.search_usersCount = jsonParams.scenarios.c_search.usersCount
        jsonParametersConfig.search_loopCount = jsonParams.scenarios.c_search.loopCount
        jsonParametersConfig.searchTerms = jsonParams.scenarios.c_search.searchTerms
    }
    else    
        jsonParametersConfig.search_enabled = "false"

    if (jsonParams.scenarios.d_pdp.enabled)
    {
        jsonParametersConfig.pdp_enabled = "true"
        jsonParametersConfig.plpUrl = jsonParams.scenarios.d_pdp.plpUrl
        jsonParametersConfig.pdp_usersCount = jsonParams.scenarios.d_pdp.usersCount
        jsonParametersConfig.pdp_loopCount = jsonParams.scenarios.d_pdp.loopCount
    }
    else    
        jsonParametersConfig.pdp_enabled = "false"

    if (jsonParams.scenarios.e_addToBasket.enabled)
    {
        jsonParametersConfig.basket_enabled = "true"
        jsonParametersConfig.basket_usersCount = jsonParams.scenarios.e_addToBasket.usersCount
        jsonParametersConfig.basket_loopCount = jsonParams.scenarios.e_addToBasket.loopCount
        if (jsonParams.scenarios.e_addToBasket.addToBasketFrom.search)
            jsonParametersConfig.basket_addProductFrom = "search"
        if (jsonParams.scenarios.e_addToBasket.addToBasketFrom.plp)
            jsonParametersConfig.basket_addProductFrom = "plp"
    }
    else    
        jsonParametersConfig.basket_enabled = "false"

    if (jsonParams.scenarios.f_submitOrder.enabled)
    {
        jsonParametersConfig.order_enabled = "true"
        jsonParametersConfig.order_usersCount = jsonParams.scenarios.f_submitOrder.usersCount
    }

    else    
        jsonParametersConfig.order_enabled = "false"        

    writeJSON(file: "${parametersFilePath}", json: jsonParametersConfig, pretty: 4) 
}


def saveTestDataIntoCSV(String parametersFilePath, String testDataCSVFilePath, String saveTestDataScriptPath) { 
    echo 'Saving TestData.csv with user parameters'  
    def jsonParameters = getParametersJsonConfig("${parametersFilePath}")
    def columns = jsonParameters.keySet()
    def dataToSave = []
    def values = columns.each{ column -> 
        def row = []
        row << column
        row << jsonParameters[column]
        dataToSave << row
    }
    writeCSV file: "${testDataCSVFilePath}", records: dataToSave, format: CSVFormat.EXCEL
}


def saveShopAccounts(String shopAccountsFilePath) {
    echo 'Saving users.csv file with shop accounts'    
    def jsonParams = readJSON text: "$params.Config"   
    if (jsonParams.scenarios.a_login.enabled) 
    {
        def arrayOfUsers = []
        jsonParams.scenarios.a_login.users.split("\\r?\\n").each { line ->
            def user = []
            line.split(";").each{rec ->
            user << rec 
        }
         arrayOfUsers << user
        }
        writeCSV file: "${shopAccountsFilePath}", records: arrayOfUsers, format: CSVFormat.EXCEL
    }    
}

def saveSearchTerms(String searchTermsFilePath) { 
    def jsonParams = readJSON text: "$params.Config"  
    if (jsonParams.scenarios.c_search.enabled)
    {
        echo 'Saving searchTerms.csv file'     
        def searchTerms = []
        jsonParams.scenarios.c_search.searchTerms.split("\\r?\\n").each { line ->
        searchTerms << line
        }
        writeCSV file: "${searchTermsFilePath}", records: searchTerms, format: CSVFormat.EXCEL
    }
}

def getParametersJsonConfig(String parametersFilePath){
    def parametersJsonConfig
    if (fileExists(parametersFilePath))
        parametersJsonConfig = readJSON file: parametersFilePath
    else
        parametersJsonConfig = readJSON text: '{}'
    return parametersJsonConfig
}