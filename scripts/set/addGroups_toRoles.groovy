import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.groups.GroupManager
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.user.ApplicationUser 
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActor
import com.atlassian.jira.util.SimpleErrorCollection

UserManager userManager = ComponentAccessor.getUserManager()
GroupManager groupManager = ComponentAccessor.getGroupManager()
ProjectRoleManager projRolManager = ComponentAccessor.getComponent(ProjectRoleManager)
ProjectRoleService projRolService = ComponentAccessor.getComponent(ProjectRoleService)
ProjectManager projectManager = ComponentAccessor.getProjectManager()

List project_groups = Arrays.asList("CMFP#Generic_Administrators, O&T_Demand, Sys.Ops_PM, Sys.Ops_Operations, Sys.DBA_PM, Sys.DBA_Operations, NOC_Operations, Operations_Users, Generic_Vendor, O&T_Tester",
"OAM#Generic_Administrators, O&T_Demand, Sys.Ops_Operations, Sys.DBA_Operations, NOC_Operations, NOC_PM, Operations_Users, Generic_Vendor, O&T_Tester",
"OIM#Generic_Administrators, O&T_Demand, Sys.Ops_Operations, Sys.DBA_Operations, NOC_Operations, NOC_PM, Operations_Users, Generic_Vendor, O&T_Tester",
"RFIO#Generic_Administrators, O&T_Demand, Sys.Ops_PM, Sys.Ops_Operations, Sys.DBA_PM, Sys.DBA_Operations, NOC_Operations, Operations_Users, Generic_Vendor, O&T_Tester",
"JIRA#O&T_Demand, Generic_Administrators, Generic_Vendor, O&T_Tester",
"APPM#DIG_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"BIRS#BI_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"BIP#BIP_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"BPM#SAP_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"BETO#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"BETPS#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"BETPV#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"CAST#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"CLB#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"SSI#DIG_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"LGP#Gaming_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"PRAWP#Gaming_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"PRVLT#Gaming_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"PAG#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"PHX#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"RPA#SAP_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"SAP#SAP_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"SELFS#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"SBI#DIG_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"SINT#DIG_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"SPORT#DIG_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"TIBCO#SAP_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"TRM#BET_Developers, Generic_Administrators, O&T_Demand, Product_PM, O&T_Tester, Product_Users, Generic_Vendor",
"AWP#Gaming_Customer, Generic_Administrators, O&T_Demand, Gaming_HDU, Program_PM, Program_Users, Generic_Vendor, Gaming_Developers, SAP_Developers, BI_Developers",
"AFC#AFC_Customer, Generic_Administrators, O&T_Demand, Program_PM, Program_Users, Generic_Vendor, BET_Developers, DIG_Developers, Gaming_Developers, SAP_Developers, BI_Developers",
"BET#BET&Dig_Customer, Generic_Administrators, O&T_Demand, BET_HDU, Program_PM, Program_Users, Generic_Vendor, BET_Developers, SAP_Developers, BI_Developers, BIP_Developers",
"CCLO#CCLO_Customer, Generic_Administrators, O&T_Demand, Program_PM, Program_Users, Generic_Vendor, BET_Developers, DIG_Developers, Gaming_Developers, SAP_Developers, BI_Developers",
"DIG#BET&Dig_Customer, Generic_Administrators, O&T_Demand, DIG_HDU, Program_PM, Program_Users, Generic_Vendor, DIG_Developers, SAP_Developers, BI_Developers",
"OT#O&T_Demand, Generic_Administrators, Program_PM, O&T_Tester, Program_Users, Generic_Vendor, BET_Developers, DIG_Developers, Gaming_Developers, SAP_Developers, BI_Developers",
"OTH#OTH_Customer, Generic_Administrators, O&T_Demand, Program_PM, Program_Users, Generic_Vendor, BET_Developers, DIG_Developers, Gaming_Developers, SAP_Developers, BI_Developers",
"VLT#Gaming_Customer, Generic_Administrators, O&T_Demand, Gaming_HDU, Program_PM, Program_Users, Generic_Vendor, Gaming_Developers, SAP_Developers, BI_Developers"
)

Map <String, String> map = new HashMap<String, String>()
List <String> users = new ArrayList <String> ()
String groupActor = ProjectRoleActor.GROUP_ROLE_ACTOR_TYPE
def errorCollection = new SimpleErrorCollection();
ApplicationUser gmt_user = userManager?.getUserByName("gmt_jirauser")
final ProjectRole rAdmin = projRolManager.getProjectRole("Administrators")
final ProjectRole rCustomer = projRolManager.getProjectRole("Customer")
final ProjectRole rDemand = projRolManager.getProjectRole("Demand")
final ProjectRole rDeveloper = projRolManager.getProjectRole("Developers")
final ProjectRole rHDU = projRolManager.getProjectRole("HDU")
final ProjectRole rTest = projRolManager.getProjectRole("Tester")
final ProjectRole rOperations = projRolManager.getProjectRole("Operations")
final ProjectRole rPM = projRolManager.getProjectRole("Project Manager")
final ProjectRole rUsers = projRolManager.getProjectRole("Users")
final ProjectRole rVendor = projRolManager.getProjectRole("Vendor")


for (String split_concat : project_groups){
    String [] splitString = split_concat.split("#")
    String project = splitString[0]
    Project currentProject = projectManager.getProjectObjByKey(project)
    String [] groups = splitString[1].split(", ")
    
    for (int i = 0; i < groups.size(); i++){
        List <String> group = Arrays.asList(groups[i])
        
        if (group[0].equals("Generic_Administrators")){ //role Administrator
            log.warn("il gruppo ${group} è entrato nella if admin per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rAdmin, currentProject, groupActor, errorCollection)
        }
        
        else if (group[0].equals("O&T_Demand")){ //role Demand
            log.warn("il gruppo ${group} è entrato nella if demand per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rDemand, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("Program_PM")){ //role Project Manager
            log.warn("il gruppo ${group} è entrato nella if pm per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rPM, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("Product_PM")){ //role Project Manager, Developers
            log.warn("il gruppo ${group} è entrato nella if pm, developers per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rPM, currentProject, groupActor, errorCollection)
            projRolService.addActorsToProjectRole(group, rDeveloper, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("O&T_Tester")){ //role Tester
            log.warn("il gruppo ${group} è entrato nella if tester per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rTest, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("Generic_Vendor")){ //role Vendor
            log.warn("il gruppo ${group} è entrato nella if vendor per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rVendor, currentProject, groupActor, errorCollection)
            
        } 
        
        else if (group[0].equalsIgnoreCase("Bet&Dig_Customer") || group[0].equals("Gaming_Customer") ||
                 group[0].equals("CCLO_Customer") || group[0].equals("AFC_Customer") ||
                 group[0].equals("OTH_Customer")) { //role Customer, Tester
            log.warn("il gruppo ${group} è entrato nella if customer, tester per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rCustomer, currentProject, groupActor, errorCollection)
            projRolService.addActorsToProjectRole(group, rTest, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("BET_Developers") || group[0].equals("DIG_Developers") ||
                 group[0].equals("Gaming_Developers") || group[0].equals("SAP_Developers") ||
                 group[0].equals("BI_Developers") || group[0].equals("BIP_Developers")) { //role Developers
            log.warn("il gruppo ${group} è entrato nella if developers per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rDeveloper, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("BET_HDU") || group[0].equals("DIG_HDU") ||
                 group[0].equals("Gaming_HDU")) { //role HDU, Tester
            log.warn("il gruppo ${group} è entrato nella if hdu, tester per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rHDU, currentProject, groupActor, errorCollection)
            projRolService.addActorsToProjectRole(group, rTest, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("Program_Users") || group[0].equals("Product_Users") ||
                 group[0].equals("Operations_Users")) { //role Users
            log.warn("il gruppo ${group} è entrato nella if users per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rUsers, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("Sys.DBA_Operations") || group[0].equals("Sys.Ops_Operations") ||
                 group[0].equals("NOC_Operations")){ //role Operations
            log.warn("il gruppo ${group} è entrato nella if operations per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rOperations, currentProject, groupActor, errorCollection)
            
        }
        
        else if (group[0].equals("Sys.DBA_PM") || group[0].equals("Sys.Ops_PM") ||
                 group[0].equals("NOC_PM")){ //role Project Manager, Operations
            log.warn("il gruppo ${group} è entrato nella if pm, operations per il progetto ${currentProject}")
            projRolService.addActorsToProjectRole(group, rPM, currentProject, groupActor, errorCollection)
            projRolService.addActorsToProjectRole(group, rOperations, currentProject, groupActor, errorCollection)
            
        }
        
        else {
            log.warn("il seguente gruppo non rientra: ${group}")
        }

    }

}

