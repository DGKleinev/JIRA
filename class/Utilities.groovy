//Example of methods - it will be updated
package com.omninecs.jira_rest

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.mindprod.csv.CSVWriter
import com.atlassian.jira.user.util.UserUtil
import org.apache.log4j.Logger
import org.apache.log4j.Category
import com.atlassian.jira.user.*
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.index.IssueIndexingService
import com.atlassian.jira.project.version.VersionManager
import com.atlassian.jira.bc.project.component.ProjectComponent
import com.atlassian.jira.bc.project.component.ProjectComponentManager
import com.atlassian.jira.bc.project.component.MutableProjectComponent
import com.atlassian.jira.application.ApplicationAuthorizationService
import com.atlassian.jira.application.ApplicationKeys
import java.text.SimpleDateFormat
import java.util.Date
import com.atlassian.jira.security.login.LoginManager
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.issue.search.SearchQuery
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.jql.query.IssueIdCollector
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.util.SimpleErrorCollection
import com.atlassian.jira.bc.projectroles.ProjectRoleService
import com.atlassian.jira.security.roles.actor.GroupRoleActorFactory.GroupRoleActor

public class NewUtilities {

        //Logger log = Logger.getLogger(NewUtilities.getClass())
    Category log = Category.getInstance(NewUtilities.getClass()) // per stampare i log

    /*---------------------------------------- USERS PART ----------------------------------------*/

    // 💡💡 this closure set user's property (key-value) 💡💡 OK Funziona
    public static void setUserProperty (String propertyKey, String propertyValue, String userName) {
        def userPropertyManager = ComponentAccessor.getUserPropertyManager()
                def user = ComponentAccessor.getUserManager().getUserByName(userName)

        userPropertyManager.getPropertySet(user).setString("jira.meta.${propertyKey}", propertyValue)
    }

    // 💡💡 this closure add users from defined groups 💡💡 OK Funziona
    public static void addUsersToGroups (ArrayList <String> users, ArrayList <String> groupsName) {
        def groupManager = ComponentAccessor.getGroupManager()
        def userManager = ComponentAccessor.getUserManager()

        users.each{ userName ->
            def currUser = userManager.getUserByName(userName)

            //if (currUser.active) {
                groupsName.each{
                    def group = groupManager.getGroup(it)
                    groupManager.addUserToGroup(currUser,group)
                }
            //}
        }
    }

    // 💡💡 this closure remove the specified group to the users array 💡💡 OK Funziona
    public static void removeUsersFromGroups(ArrayList <String> users, ArrayList <String> groupsName) {

        def groupManager = ComponentAccessor.getGroupManager()
        def userManager = ComponentAccessor.getUserManager()
        def userUtil = ComponentAccessor.userUtil

        users.each{ userName ->
            def currUser = userManager.getUserByName(userName)

            //if (currUser.active) {
                groupsName.each{
                    def group = groupManager.getGroup(it)
                    userUtil.removeUserFromGroup(group,currUser);
                }
            //}
        }
    }

    // 💡💡 this closure gets all information about some users and return a table 💡💡 OK Funziona
    public static StringBuilder getUsersStatistics(ArrayList <String> users) {

        def loginManager = ComponentAccessor.getComponentOfType(LoginManager.class)
        def userManager = ComponentAccessor.getUserManager()
        def groupManager = ComponentAccessor.getGroupManager()
        def applicationAuthorizationService = ComponentAccessor.getComponent(ApplicationAuthorizationService)

        def userGroups
        def licensedUser

        List tableNames = new ArrayList<String>()
        tableNames += ['User Name', 'Full Name', 'eMail Address', 'Last Login', 'Licenced', 'Groups', 'Active']
        StringBuilder builder = createTable(tableNames)

        users.each{
            def currUser = userManager.getUserByName(it)

            if (currUser){
                userGroups = groupManager.getGroupNamesForUser(currUser).join("<br/>")
                if (!userGroups) {
                    userGroups = '-'
                }
                licensedUser = applicationAuthorizationService.canUseApplication(currUser, ApplicationKeys.SOFTWARE)

                Long lastLoginTime = loginManager.getLoginInfo(it).getLastLoginTime()

                if(currUser.active=="false") {
                    builder = createTable([it, currUser.displayName, currUser.emailAddress, "Inactive User", licensedUser, userGroups, currUser.active], builder)
                } else if(lastLoginTime==null) {
                    builder = createTable([it, currUser.displayName, currUser.emailAddress,"Logon not found", licensedUser, userGroups, currUser.active], builder)
                } else{
                    Date date = new Date(lastLoginTime);
                    SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yy hh:mm");
                    String dateText = df2.format(date);
                    builder = createTable([it, currUser.displayName, currUser.emailAddress, dateText, licensedUser, userGroups, currUser.active], builder)
                }
            }
        }
        return builder
    }

    // 💡💡 this closure tell if the user is inside a group or not  💡💡 OK Funziona
    public static Boolean isUserInGroup(String userName, String groupName) {
        def user = ComponentAccessor.getUserManager().getUserByName(userName)
        ComponentAccessor.getGroupManager().with {
            isUserInGroup(user, getGroup(groupName))
        }
    }




    /*---------------------------------------- GROUPS PART ----------------------------------------*/

    // 💡💡 this closure return the users from one or more groups without duplies💡💡 OK Funziona
    public static List getUsersInsideGroups (ArrayList <String> groups) {
        List allUsers = new ArrayList<ApplicationUser>()
        def allUsersHas = new HashSet <ApplicationUser> ()

        def groupManager = ComponentAccessor.getGroupManager()
        groups.each{ group ->
            allUsersHas.addAll(groupManager.getUsersInGroup(group))
        }

        allUsers.addAll(allUsersHas)
        return allUsers
    }

    // 💡💡 this closure get the users from one or more groups and places them in a csv file which is saved in a certain path 💡💡 OK Funziona
    public static void getUsersInsideGroupsCSV (ArrayList <String> groups, String pathFile) {

        def sw = new StringWriter()
        def csv = new CSVWriter(sw)

        ["UserName","Full Name","e-mail","Group"].each{csv.put(it.toString())}
        csv.nl()

        def groupManager = ComponentAccessor.getGroupManager()

        groups.each{ group ->
            groupManager.getUsersInGroup(group).each{
                csv.put(it.username)
                csv.put(it.displayName)
                csv.put(it.emailAddress)
                csv.put(group)
                csv.nl()
            }
        }

        csv.close()

        def issueFile = new File(pathFile,"Requested_Users_${new Date().format("yyyyMMdd")}.csv")
        issueFile.write sw.toString()
    }




    /*---------------------------------------- PROJECTS PART ----------------------------------------*/

    // 💡💡 this closure get all projects 💡💡 OK Funziona
    public static List <Project> getAllProjects() {
        ComponentAccessor.getProjectManager().getProjectObjects()
    }

    // 💡💡 this closure get the projects using key 💡💡 OK Funziona
    public static Project getProject(String key) {
        ComponentAccessor.getProjectManager().getProjectObjByKey(key)
    }

    // 💡💡 this closure get the projects using id 💡💡 OK Funziona
    public static Project getProject(Long id) {
        ComponentAccessor.getProjectManager().getProjectObj(id)
    }

    // 💡💡 this closure get the project role actors 💡💡 OK Funziona
    public static ArrayList getProjectRoleActors (String pRole, String pKey) {
        def projectManager = ComponentAccessor.getProjectManager().getProjectObjByKey(pKey)
        def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
        ProjectRole role = projectRoleManager.getProjectRole(pRole)

        def allActors = []

        ProjectRoleActors actors = projectRoleManager.getProjectRoleActors(role, projectManager)
        actors.each{actor -> actor.roleActors.each{lastActor -> allActors.add(lastActor.getParameter()) }}

        return allActors
    }

    // 💡💡 this closure remove all versions from a list of projects 💡💡 OK Funziona
    public static void removeProjectsVersions (ArrayList <String> projects) {
        VersionManager versionManager = ComponentAccessor.getVersionManager()

        projects.each{
            def project = ComponentAccessor.projectManager.getProjectObjByKey(it);
            versionManager.deleteAllVersions(project.getId())
        }
    }

    // 💡💡 this closure remove all components from a list of projects 💡💡 OK Funziona
    public static void removeProjectsComponents (ArrayList <String> projects) {
        projects.each{
            def project = ComponentAccessor.projectManager.getProjectObjByKey(it);
            ComponentAccessor.projectComponentManager.deleteAllComponents(project.getId())
        }
    }

    // 💡💡 this closure gets all components from a project 💡💡 OK Funziona
    public static Collection<ProjectComponent> getProjectComponents (String projectKey) {
        def projectComponentManager = ComponentAccessor.getComponent(ProjectComponentManager)
        def projectManager = ComponentAccessor.getComponent(ProjectManager)

        def project = projectManager.getProjectByCurrentKey(projectKey)
        Collection<ProjectComponent> projectComponents = projectComponentManager.findAllForProject(project.getId())

        return projectComponents
    }

    // 💡💡 this closure get a specific componet from a single project 💡💡 OK Funziona
    public static ProjectComponent getComponentByName(String componentName, String projectName) {
        Long projectId = getProject(projectName).getId()
        ComponentAccessor.projectComponentManager.findByComponentName(projectId, componentName)
    }

    // 💡💡 this closure create a new compontent for a project 💡💡 OK Funziona
    public static void createComponent (String name, String description, String lead, long assigneeType, Long projectId, Boolean archived) {
        def projectComponentManager = ComponentAccessor.getComponent(ProjectComponentManager)
        projectComponentManager.create(name, description, lead, assigneeType , projectId)

        if(archived) {
            ProjectComponent componentToUpdate = projectComponentManager.findByComponentName(projectId, name)
            MutableProjectComponent mutableComponent = MutableProjectComponent.copy(componentToUpdate)
            mutableComponent.setArchived(true)
            projectComponentManager.update(mutableComponent)
        }
    }

    // 💡💡 this closure copy components from a project to another 💡💡 OK Funziona
    public static void copyComponents (String sourceProjectKey, String targetProjectKey) {
        def projectManager = ComponentAccessor.getComponent(ProjectManager)
        def targetProject = projectManager.getProjectByCurrentKey(targetProjectKey)

        Collection<ProjectComponent> projectComponents = getProjectComponents(sourceProjectKey)

        projectComponents.each{
            createComponent(it.name, it.description, it.lead, it.assigneeType , targetProject.getId(), it.isArchived())
        }

    }

    // 💡💡 this closure add groups to project role 💡💡 OK Funziona
    public static void addGroupsToProjectRole(Collection<String> actors, String projectRoleName, String projectKey) {
        ProjectManager projectManager = ComponentAccessor.getProjectManager()
        ProjectRoleManager projectRoleManager = (ProjectRoleManager) ComponentAccessor.getComponentOfType(ProjectRoleManager.class)
        ProjectRoleService projectRoleService = ComponentAccessor.getComponentOfType(ProjectRoleService.class)

        Project project = projectManager.getProjectObjByKey(projectKey)
        ProjectRole projectRole = projectRoleManager.getProjectRole(projectRoleName)

        projectRoleService.addActorsToProjectRole(actors, projectRole, project, GroupRoleActor.GROUP_ROLE_ACTOR_TYPE, new SimpleErrorCollection())
    }

    // 💡💡 this closure get all projects inside list of categories 💡💡 OK Funziona
    public static ArrayList<Project> searchProjectsByCategory(ArrayList<String> categories) {
        ProjectManager projectManager = ComponentAccessor.getProjectManager()
        def prList = ComponentAccessor.getProjectManager().getProjectObjects()
        ArrayList<Project> projects = []

        prList.each { project ->
            if (project.projectCategory?.getName() in categories){
                projects.add(project)
            }
        }

        return projects
    }



    /*---------------------------------------- ISSUES PART ----------------------------------------*/

    // 💡💡 this closure get the attachments from one issue and paste them into another 💡💡
    public static void copyAttachments (String sourceIssueKey, String destinationIssueKey, String username = null) {
        //if username is not null, admin_user is equal to the username parameter, otherwise admin_user is devjira_cm
        def admin_user = ComponentAccessor.getUserManager().getUserByName(username?: "devjira_cm")
        def currentIssue = ComponentAccessor.getIssueManager().getIssueObject(sourceIssueKey) //i.e: "OLG-3173"
        def destinationIssue = ComponentAccessor.getIssueManager().getIssueObject(destinationIssueKey) //i.e: "CUSTOLG-14564"

        currentIssue.getAttachments().each {
            def currentAttachment = it
            if (!destinationIssue.getAttachments().find{it.filename == currentAttachment.filename && it.filesize == currentAttachment.filesize && it.mimetype == currentAttachment.mimetype}) {
                ComponentAccessor.attachmentManager.copyAttachment(it, admin_user, destinationIssue.key)

                //log.warn(currentAttachment.filename + " attached")
            } /*else {
                log.warn(currentAttachment.filename + " not attached")
            }*/
        }
    }

    // 💡💡 this closure change the assignee of the issue 💡💡 Ok Funziona
    public static void updateAssignee(String issueKey, String userName) {
                def issue = getIssue(issueKey)
                def user = ComponentAccessor.getUserManager().getUserByName(userName)
                issue.setAssignee(user)

                ComponentAccessor.getIssueManager().updateIssue(ComponentAccessor.jiraAuthenticationContext?.user, issue, EventDispatchOption.DO_NOT_DISPATCH, false)
                def issueIndexingService = ComponentAccessor.getComponent(IssueIndexingService)
                issueIndexingService.reIndex(issue)
        }

    // 💡💡 this closure add the user to issue's watcher list 💡💡 Ok Funziona
    public static void startWatching(String userName, String issueKey) {
        def issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)
        def user = ComponentAccessor.getUserManager().getUserByName(userName)
        ComponentAccessor.watcherManager.startWatching(user, issue)
    }

    // 💡💡 this closure remove the user to issue's watcher list 💡💡 Ok Funziona
    public static void stopWatching(String userName, String issueKey) {
        def issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)
        def user = ComponentAccessor.getUserManager().getUserByName(userName)
        ComponentAccessor.watcherManager.stopWatching(user, issue)
    }

    // 💡💡 this closure get a list of all watchers 💡💡 Ok Funziona
    public static List getAllWatchers(String issueKey) {
        def issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)
        return ComponentAccessor.watcherManager.getWatchers(issue, Locale.US)
    }

    // 💡💡 this closure return the inward issues linked 💡💡 Ok Funziona
    public static List getInwardIssues(String issueKey, long issueTypeId = 0) {
        def issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)

        if(issueTypeId != 0) {
            return ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id)*.getSourceObject().findAll {
                it.genericValue.type.equals(issueTypeId.toString())
            }
        } else {
            return ComponentAccessor.getIssueLinkManager().getInwardLinks(issue.id)*.getSourceObject()
        }
    }

    // 💡💡 this closure return the outward issues linked 💡💡 Ok Funziona
    public static List getOutwardIssues(String issueKey, long issueTypeId = 0) {
        def issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)

        if(issueTypeId != 0) {
            return ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id)*.getDestinationObject().findAll {
                it.genericValue.type.equals(issueTypeId.toString())
            }
        } else {
            return ComponentAccessor.getIssueLinkManager().getOutwardLinks(issue.id)*.getDestinationObject()
        }
    }

    // 💡💡 this closure return a list of inward/outward issues linked 💡💡 Ok Funziona
    public static ArrayList getAllLinkedIssues(String issue, long issueTypeId = 0) {
        def allLinkedIssue = new HashSet <String> ()
        ArrayList allLinkedIssueList = new ArrayList <String> ()

        allLinkedIssue.addAll(getInwardIssues(issue, issueTypeId))
        allLinkedIssue.addAll(getOutwardIssues(issue, issueTypeId))
        allLinkedIssueList.addAll(allLinkedIssue)

        return allLinkedIssueList
    }

    // 💡💡 this closure is used to add a comment to an issue (default author devjira_cm) 💡💡 Ok Funziona
    public static void addComment(String issueKey, String userName = "devjira_cm", String comment, Boolean dispatch = true){
        def issue = ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)
        def author = ComponentAccessor.getUserManager().getUserByName(userName)
        ComponentAccessor.commentManager.create(issue, author, comment, dispatch)
    }

    // 💡💡 this closure the issues inside a JQL 💡💡 Ok Funziona
    public static List getIssuesByJQL(String jql) {
        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser)
        def searchProvider = ComponentAccessor.getComponent(SearchProvider)
        def issueManager = ComponentAccessor.getIssueManager()
        def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()

        def query = jqlQueryParser.parseQuery(jql)
        SearchQuery searchQuery = SearchQuery.create(query, user)
        IssueIdCollector collector = new IssueIdCollector()
        searchProvider.search(searchQuery, collector)
        return collector.getIssueIds().collect { getIssue(it as Long) }
    }

    // 💡💡 this closure get the issue object with its key 💡💡 Ok Funziona
    public static MutableIssue getIssue(String issueKey) {
        ComponentAccessor.issueManager.getIssueByCurrentKey(issueKey)
    }

    // 💡💡 this closure get the issue object with its Id 💡💡 Ok Funziona
    public static MutableIssue getIssue(Long id) {
        ComponentAccessor.issueManager.getIssueObject(id)
    }




    /*---------------------------------------- OTHER PART ----------------------------------------*/

    // 💡💡 this closure create a table 💡💡 Ok Funziona
    public static StringBuilder createTable (ArrayList <String> values, StringBuilder builder = null) {

        StringBuilder newBuilder = new StringBuilder()

        if (!builder) {
            newBuilder.append("<table border='1' cellpadding='5' style='border-collapse:collapse'><tr>")
            values.each{
                newBuilder.append("<td><b>${it}</b></td>")
            }
        } else {
            newBuilder.append(builder.substring(0, builder.length() - 8))
            newBuilder.append('<tr>')
            values.each{
                newBuilder.append("<td>${it}</td>")
            }
        }

        newBuilder.append("</tr></table>")

        return newBuilder
    }

    // 💡💡 this closure return the link to its guide 💡💡 Ok Funziona
    public static StringBuilder info () {
        StringBuilder builder = createTable(["return", "method"])

        def methodReturn = []
        def methodName = []

        def methods = NewUtilities.declaredMethods.findAll { !it.synthetic }
        def methodToRemove = ["getLog", "setLog"]

        methods.each{
            if(!methodToRemove.contains(it.getName().toString())) {
                methodReturn.add(it.returnType.simpleName)
                def string = "<b>" + it.getName() + "</b> ("

                if (it.parameterTypes) {
                    it.parameterTypes.each{
                        string += it.getSimpleName() + ", "
                    }
                    string = string.substring(0, string.length() - 2)
                }
                methodName.add(string + ")")
            }

        }


        for(int i = 0; i < methodName.size(); i++) {
            builder = createTable([methodReturn[i],methodName[i]], builder)
        }
        return builder

    }
}
