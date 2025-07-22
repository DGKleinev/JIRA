import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.Project
import static com.atlassian.jira.issue.IssueFieldConstants.ISSUE_TYPE
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.issue.issuetype.IssueType

log.warn(getActionName())
if (! underlyingIssue){//(getActionName() in ["Create Issue", "Create", "Crea"]) {
    def currentUser = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
    ArrayList <IssueType> globalIssues = issueContext.getProjectObject().getIssueTypes() as ArrayList
    //PROD
    /*
    String [] id_customer = ["10102", "10109"]
    String [] id_developer = ["10112", "10109", "10114", "10002"]
    String [] id_hdu = ["10109"]
    String [] id_operations = ["10113", "10112", "10109", "10000", "10111", "10110", "10002"]
    String [] id_projectMember = []
    String [] id_tester = ["10004", "10005", "10006", "10009", "10007", "10008", "10114", "10112"]
    String [] id_userVendor = []
    */
    //Dev
    String [] programID_administrator = ["10110", "10000", "10103", "10107", "10102", "10002", "10003", "10004", "10007", "10005", "10006"]
    String [] programID_customer = ["10103", "10107", "10102", "10002", "10003", "10004", "10007", "10005", "10006"]
    String [] programID_demand = ["10110", "10000", "10103", "10107", "10102", "10002", "10003", "10004", "10007", "10005", "10006"]
    String [] programID_developer = []
    String [] programID_hdu = ["10107"]
    String [] programID_operations = []
    String [] programID_projectManager = ["10107"]
    String [] programID_tester = ["10107", "10002", "10003", "10004", "10007", "10005", "10006"]
    String [] programID_userVendor = []

    ArrayList <IssueType> issue_administrator = getIssueTypes_forRole(programID_administrator, globalIssues)
    ArrayList <IssueType> issue_customer = getIssueTypes_forRole(programID_customer, globalIssues)
    ArrayList <IssueType> issue_demand = getIssueTypes_forRole(programID_demand, globalIssues)
    ArrayList <IssueType> issue_developer = []
    ArrayList <IssueType> issue_hdu = getIssueTypes_forRole(programID_hdu, globalIssues)
    ArrayList <IssueType> issue_operations = []
    ArrayList <IssueType> issue_projectManager = getIssueTypes_forRole(programID_projectManager, globalIssues)
    ArrayList <IssueType> issue_tester = getIssueTypes_forRole(programID_tester, globalIssues)
    ArrayList <IssueType> issue_userVendor = []

    def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
    def remoteUserRoles = projectRoleManager.getProjectRoles(currentUser, issueContext.projectObject)*.name
    def issueTypeField = getFieldById(ISSUE_TYPE)
    Set <IssueType> issues_toShow = new HashSet<IssueType>()

    for (int i = 0; i < remoteUserRoles.size(); i++){
        switch(remoteUserRoles[i].toString()){
            case "Administrators":
                log.warn("Administrators")
                issues_toShow.addAll(issue_administrator)
                break

            case "Demand":
            	log.warn("Demand")
                issues_toShow.addAll(issue_demand)
                break

            case "Project Manager":
            	log.warn("Project Manager")
                issues_toShow.addAll(issue_projectManager)
                break

            case "Customer":
            	log.warn("Customer")
                issues_toShow.addAll(issue_customer)
                break

            case "Developers":
            	log.warn("Developers")
                issues_toShow.addAll(issue_developer)
                break

            case "HDU":
            	log.warn("HDU")
                issues_toShow.addAll(issue_hdu)
                break

            case "Operations":
            	log.warn("Operations")
                issues_toShow.addAll(issue_operations)
                break

            case "Tester":
            	log.warn("Tester")
                issues_toShow.addAll(issue_tester)
                break

            default:
               break       
        }
    }
    issueTypeField.setFieldOptions(issues_toShow)
}
private ArrayList <IssueType> getIssueTypes_forRole(String [] idIssues, ArrayList <IssueType> allIssues){
	List <IssueType> issues = new ArrayList <IssueType> ()
    for (int i = 0; i < idIssues.size(); i++){
    	issues.add( allIssues.find { it.getId().equals(idIssues[i])})    
	}   
    return issues
}
