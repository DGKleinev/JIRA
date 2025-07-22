/* 
COME UTILIZZARE LO SCRIPT:
1) Copiare ed incollare all'interno della console PJ
2) Copiare ed incollare la richiesta NGUP così come sta sul sito ed incollarla all'interno della variabile "text" (solo se 1 solo erProfile)
    2bis) IMPORTANTE: non copiare la prima riga della richiesta NGUP (Company) altrimenti entra in conflitto con la company dell'utente
3) Controllare che la variabile createUser sia settata su false
4) Eseguire lo script
5) Cliccare sul nome del gruppo customer all'interno della tabella che uscirà, verrete reindirizzati ad una nuova pagina contenente tutti gli utenti customer di quel progetto
6) Inserire il nome utente nella variabile userName con la logica utilizzata per gli altri utenti già esistenti
7) Modificare il valore della variabile createUser su true
8) eseguire lo script e controllare se l'utente è stato creato correttamente (link sul nome utente nella tabella)


TODO:
✔️ prendere gruppo customer progetto (creata la funzione e messa all'interno del file con funzioni)
✔️ aggiungere l'utente al gruppo per renderlo un customer (creata la funzione e messa all'interno del file con funzioni)
✔️ creare l'oggetto per l'inserimento dei dati
✔️ aggiungere Unit = Company (creata la funzione e messa all'interno del file con funzioni)
✔️ eseguire la creazione con pjadmin
✔️ creare l'utente con i dati che vengono estrapolati dalla richiesta, solo se una variabile di controllo è settata su false
✔️ stampa il link verso il nuovo profilo
✔️ stampa il link verso utenti gruppo customer per vedere l'username
✔️ per più progetti rivedere la funzione per aggiungere l'utente al gruppo customer
✔️ controllare accesso per più progetti (erprofile)
✔️ rendere l'email minuscola quando si crea l'utente
✔️ se l'utente già presente prendere username in base all'email
✔️ considerare che l'utente con una mail non presente in jira non venga creato con una username già esistente
✔️ quando in un progetto non trova customer, cerca in customer restricted e se trova il gruppo con quella logica prender quel ruolo
✔️ gestire errore nel metodo getProjectRoleActors che ritorna vuoto quando prendere ad esempio utenti (problema I2B prod)
✔️ gestire progetti PJNOME
    [16:45 24/02/2022] Ballabio, Daniele (Consultant)
    Project Name                        Current Project         KeyFuture Project Key
    Pennsylvania Live                   PENN                    PJPENN
    Bet365                              BET                     PJBET
    Caliber                             CAL                     PJCAL
    Casino App LTM                      CASAPP                  PJCASAPP
    Newcastle marketing Services        NMS                     PJNMS
    NPP                                 BSNPP                   PJNPP
    BetFred                             BETFRED                 PJBETFRED
✔️ [16:46 24/02/2022] Ballabio, Daniele (Consultant)
    Current Project Name        Future Project Name
    Lottomatica Interactive     Partnerjira Lottomatica Interactive
    Norsk Tipping               Partnerjira Norsk Tipping
    Casino App LTM              Partnerjira Casino App LTM

- considerare se arriva nome o chiave progetto in erprofile
- suggerire username
- gestire progetti rosati (projectKey)

ACTIVITY THE SCRIPT SHOULD DO:
✔️ Creazione utente su PJ  se email user @IGT RIGETTARE 
✔️ 1)	Verificare che l’utente esiste
2)	Cercare altri utenti stessa email
✔️ 3)	eroperationalnames  cosa fare (es. ADD)
4)	additionalinfo 
5)	Create User
    ✔️ a.	Verificare directory (Jira Internal Directory)
    ✔️ b.	Send Notification
    ✔️ c.	Rimuovere password
    ✔️ d.	Email (ersponsor)
    ✔️ e.	Full name (cn)
    f.	Username (con logica punto 2)
✔️ 6)	erprofile per chiave Progetto (far vedere solo quello)
    ✔️ a.	possono essere più di un progetto
✔️ 7)	Cercare progetto e prendere ruolo necessario
    ✔️ a.	Controllare gruppo necessario
    ✔️ b.	Aggiungere utente al gruppo (così ha i permessi)
✔️ 8)	Su profilo utente
    ✔️ a.	Actions Edit Properties
    ✔️ b.	Key  Unit, Value  (company)

*/

import groovy.transform.Field
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.bc.user.search.UserSearchService
import org.apache.commons.lang.StringUtils
import com.atlassian.jira.project.ProjectManager
import com.atlassian.jira.bc.user.UserService
import com.atlassian.jira.project.Project
import com.atlassian.jira.security.roles.ProjectRole
import com.atlassian.jira.security.roles.ProjectRoleActors
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUser

ProjectManager projectManager = ComponentAccessor.getProjectManager()
def admin_user = ComponentAccessor.getUserManager().getUserByName("devjira_cm") //I2B

// the username of the new user - REQUIRED
def userName = ["grenville_dakota"]
def createUser = false

String text = """
UserID=id9092
ersponsor=name.surname@domain.com
eraliases=new
erprofile=Dakota Sioux Connection SD
eraccountownershiptype=Individual
eroperationnames=Add
Company=Dakota Sioux Connection SD
cn=Name LastName
e-Mail=name.lastname@domain.com (TO ADD)
First Name=name (TO ADD)
Last Name=lastName (TO ADD)
"""

User newUser = initUser(text, userName[0])
@Field List keys = new ArrayList<String> ()
List groups = new ArrayList<String> ()

groups += getProjectRoleActors("Customer", newUser.getErProfile())
for(int i = 0; i < newUser.getErProfile().size(); i++) {
    if(groups[i] == 'vuoto') {
        groups[i] = getProjectRoleActors("Customer (Restricted)", [newUser.getErProfile()[i]])[0]
    }
}

newUser.setCGroup(groups)
newUser.setProjectKeys(keys)

/*GET ALL INFORMATION REQUIRED, IF USER'S EMAIL IS @it RETURN, IF USER SHOULDN'T BE DELETED RETURN*/
if (newUser.erSponsor.substring(newUser.erSponsor.lastIndexOf("@")) == "@ddomain.com") {
    log.warn("attention to this user, @it.com")
    return
}

//check if the user should be added
if (newUser.erOperationNames != 'add') {
    log.warn("this user shouldn't be added")
    return "<b><a href='${ComponentAccessor.getApplicationProperties().getString("jira.baseurl")}/secure/admin/user/ViewUser.jspa?name=${userName[0]}' target='_blank'>${userName[0]}</a></b>"
}

if (newUser.isEmailPresent()){
    def user = ComponentAccessor.getUserSearchService().findUsersByEmail(newUser.erSponsor)
	userName[0] = user.first().getUsername()
    createUser = false
}

if(newUser.isUsernamePresent()){
    createUser = false
}

StringBuilder builder = newUser.seeAttributes(userName[0])

if (createUser) {
    ComponentAccessor.jiraAuthenticationContext.loggedInUser = admin_user
    def userService = ComponentAccessor.getComponent(UserService)
    
    def newCreateRequest = UserService.CreateUserRequest.withUserDetails(admin_user, userName[0], null, newUser.erSponsor, newUser.cn) //admin_user, username, password, emailAddress, displayName
        .inDirectory(1) //directory ID where you want to create the user
        .sendNotification(true) // true o false if you want to send notification

    def createValidationResult = userService.validateCreateUser(newCreateRequest)
    assert createValidationResult.valid : createValidationResult.errorCollection

    userService.createUser(createValidationResult)

    addUsersToGroups(userName, newUser.getCGroup())
    setProperty("Unit", newUser.company, ComponentAccessor.getUserManager().getUserByName(userName[0]))
}
return builder

//Methods

public String checkProjectName(String project) {
    def projectPj = ["Lottomatica Interactive", "Norsk Tipping", "Casino App LTM"]
    final String partnerJira = "Partnerjira "

    if(projectPj.contains(project)) {
        project = partnerJira + project
    }

    return project
}

public String checkProjectKeys(String key) {
    def keyI2B = ["PJPENN", "PJBET", "PJCAL", "PJCASAPP", "PJNMS", "PJNPP", "PJBETFRED"]

    if(keyI2B.contains(key)) {
        key = key.substring(2)
    }

    return key
}

public User initUser(String infoRequest, String username){
    String [] attributes = infoRequest.split("\\r?\\n")
    String erSponsor
    List erProfile = new ArrayList<String>()
    String erOperationNames
    String company
    String cn
    List cGroup = new ArrayList<String>()
    
    for (int i = 0; i < attributes.length; i++){
        String [] str = attributes[i].split("=")
        switch(str[0].toString()){
            case "ersponsor" : 
            	erSponsor = str[1].toLowerCase()
            	break
            case "erprofile" :
            	erProfile.add(checkProjectName(str[1]))
            	break
            case "eroperationnames" : 
            	erOperationNames = str[1].toLowerCase()
            	break
            case "Company" :
            	company = str[1]
            	break
            case "cn" : 
            	cn = str[1]
            	break
        }
    }

	return new User(erSponsor,erProfile, erOperationNames, company, cn, cGroup, username)
}


public ArrayList getProjectRoleActors(String roleWanted, ArrayList <String> projects){
    List groups = new ArrayList <String> ()
    ProjectManager projectManager = ComponentAccessor.getProjectManager()
    def projectRoleManager = ComponentAccessor.getComponent(ProjectRoleManager)
    ProjectRole role = projectRoleManager.getProjectRole(roleWanted)

    for(int i = 0; i < projects.size(); i++){    
        def currentProject = projectManager.getProjectObjByName(projects[i])
        String key = currentProject.getKey()
        keys.add(key)
        key = checkProjectKeys(key)
        ProjectRoleActors actors = projectRoleManager.getProjectRoleActors(role, currentProject)

        actors.roleActors.each{ actor ->
            if(actor.getParameter() == "partnerjira-partner_${key}") {
                groups.add(actor.getParameter())
            } /*else {
                groups.add('vuoto')
            }*/
        }

        if(groups[i] == null) {
            groups[i] = "vuoto"
        }

    }

    return groups
}

void addUsersToGroups (def users, ArrayList <String> groupsName) {
    def groupManager = ComponentAccessor.getGroupManager()
    def userManager = ComponentAccessor.getUserManager()

    List groupsToAdd = new ArrayList<String>()
    groupsToAdd += groupsName
    groupsToAdd += ["ngup_partnerjira_internal_users", "partnerjira-users"]

    users.each{ userName ->
        def currUser = userManager.getUserByName(userName)

        if (currUser.active) {
            groupsToAdd.each{
                def group = groupManager.getGroup(it)
            	groupManager.addUserToGroup(currUser,group)
            }
    	}
    }
}

void setProperty (String propertyKey, String propertyValue, def user) {
    def userPropertyManager = ComponentAccessor.getUserPropertyManager()
    userPropertyManager.getPropertySet(user).setString("jira.meta.${propertyKey}", propertyValue)
}

// new class for user variables
class User {
    
    String erSponsor
    ArrayList<String> erProfile
    String erOperationNames
    String company
    String cn
	ArrayList<String> cGroup
	ArrayList<String> projectKeys
    String username
    def baseurl
    boolean usernameExists
    boolean emailExists
    
    public User(String erSponsor, ArrayList <String> erProfile,String erOperationNames, String company, String cn, ArrayList <String> cGroup, String username){
        this.erSponsor = erSponsor
        this.erProfile = erProfile
        this.erOperationNames = erOperationNames
        this.company = company
        this.cn = cn
        this.cGroup = cGroup
        this.username = username
        baseurl = ComponentAccessor.getApplicationProperties().getString("jira.baseurl")

    }

    public StringBuilder seeAttributes( String userName) {
        StringBuilder builder = new StringBuilder()
        builder.append("""<table border='1' cellpadding='5' style='border-collapse:collapse'><tr>
                            <td><b>erSponsor</b></td>
                            <td><b>exists</b></td>
                            <td><b>erProfile</b></td>
                            <td><b>projectKey</b></td>
                            <td><b>erOperationNames</b></td>
                            <td><b>company</b></td>
                            <td><b>cn</b></td>
                            <td><b>Customer Group</b></td>
                            <td><b>User profile</b></td>
                        </tr>""")
        builder.append("""<tr>
                            <td><b>${getErSponsor()}</b></td>
                            <td>${urserOrEmailExist()}</td>
                            <td><b>${getErProfile()[0]}</b></td>
                            <td><b><a href='${baseurl}/plugins/servlet/project-config/${getProjectKeys()[0]}/summary' target='_blank'>${getProjectKeys()[0]}</a></b></td>
                            <td><b>${getErOperationNames()}</b></td>
                            <td><b>${getCompany()}</b></td>
                            <td><b>${getCn()}</b></td>
                            <td><b><a href='${baseurl}/secure/admin/user/UserBrowser.jspa?group=${getCGroup()[0]}' target='_blank'>${getCGroup()[0]}</a></b></td>
                            <td><b><a href='${baseurl}/secure/admin/user/ViewUser.jspa?name=${userName}' target='_blank'>${userName}</a></b></td>
                        </tr>""")

        // Loop for more projects
        for(int i = 1; i < getErProfile().size(); i++){
            builder.append("""<tr>
                    <td></td>
                    <td></td>
                    <td><b>${getErProfile()[i]}</b></td>
                    <td><b><a href='${baseurl}/plugins/servlet/project-config/${getProjectKeys()[i]}/summary' target='_blank'>${getProjectKeys()[i]}</a></b></td>
                    <td></td>
                    <td></td>
                    <td></td>
                    <td><b><a href='${baseurl}/secure/admin/user/UserBrowser.jspa?group=${getCGroup()[i]}' target='_blank'>${getCGroup()[i]}</a></b></td>
                    <td></td>
                </tr>""")
        }

        builder.append("</table>")

        return builder
    }

    private String urserOrEmailExist (){
        String message = ""

        if (isEmailPresent() && isUsernamePresent()) {
            message = "<b style='color: red'>email and username exists</b>"
        } else if (isEmailPresent()) {
            message = "<b style='color: red'>email exists</b>"
        } else if (isUsernamePresent()){
            message = "<b style='color: red'>username exists</b>"
        } else {
            message = "<b style='color: green'>to be added</b>"
        }

        return message
    }

    private boolean isEmailPresent(){
        def userSearchService = ComponentAccessor.getUserSearchService()
        return userSearchService.findUsersByEmail(erSponsor) ? true : false        
    }

    private boolean isUsernamePresent(){
        def userManager = ComponentAccessor.getUserManager()
        return userManager.getUserByName(username) ? true : false

    }

    public ArrayList<String> getProjectKeys() { return this.projectKeys }
    public String getErSponsor() { return this.erSponsor }
    public ArrayList<String> getErProfile() { return this.erProfile }
    public String getErOperationNames() { return this.erOperationNames }
    public String getCompany() { return this.company }
    public String getCn() { return this.cn }
    public String getUsername() { return this.username }
    public ArrayList<String> getCGroup() { return this.cGroup }
    
    public void setEmailExists( boolean emailExists) { this.emailExists = emailExists}
    public void setCGroup( ArrayList <String> cGroup) { this.cGroup = cGroup }
    public void setProjectKeys( ArrayList <String> projectKeys) { this.projectKeys = projectKeys }
}
