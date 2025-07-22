public void pasteCommentsandLabels (String srcKey, String destKey, ApplicationUser gmt_user){
  CommentManager commentManager = ComponentAccessor.getCommentManager()
  PermissionManager permissionManager = ComponentAccessor.getPermissionManager()
  LabelManager labelManager = ComponentAccessor.getComponent(LabelManager)
  Project destProject = projectManager.getProjectByCurrentKey(destKey.split("-")[0])
  Issue srcIssue = issueManager.getIssueObject(srcKey)
  Issue destIssue = issueManager.getIssueObject(destKey)
  List  comments = commentManager.getComments(srcIssue)
  ProjectPermissionKey permissionKey = new ProjectPermissionKey("ADD_COMMENTS")
  Set labels = labelManager.getLabels(srcIssue.id)

  for (Label currentLabel : labels) {
      labelManager.addLabel(gmt_user, destIssue.id, currentLabel.toString(), false)
  }

  for (Comment currentComment : comments){
      def currentAuthor = currentComment.getAuthorApplicationUser()
      String body = currentComment.getBody()
      String groupLevel = currentComment.getGroupLevel()
      Long roleLevelId = currentComment.getRoleLevelId()
              Date created = currentComment.getCreated()
      Date updated = currentComment.getUpdated()
      boolean createCommentPermission = permissionManager.hasPermission(permissionKey, destProject, currentAuthor)
      if(!body.contains("Le seguenti issue")){

          if (createCommentPermission){
              commentManager.create(destIssue, currentAuthor, gmt_user,
                                      body, groupLevel, roleLevelId, created, updated, true )
          } else{
              String newBody = """Creato da: ${currentAuthor}. ${body}"""
              commentManager.create(destIssue, gmt_user, gmt_user,
                                      body, groupLevel, roleLevelId, created, updated, true )
          }
      }
  }
}
