

INSERT INTO `wechat_user_privilege` VALUES
  (1,'query_user','后台','查询用户',1499737601,1499737601,0),
  (2,'query_verify_history','后台','查询验证历史',1499737601,1499737601,0),
  (3,'query_user_profile','后台','查询用户资料',1499737601,1499737601,0),
  (4,'query_verify_history_all','后台','查询用户所有的验证历史',1499737601,1499737601,0),
  (5,'login_admin','登陆','后台查询',1499737601,1499737601,0),
  (6,'user_register','注册','用户注册',1499737601,1499737601,0),
  (7,'update_password','用户','密码修改',1499737601,1499737601,0);

INSERT INTO `wechat_user_role` VALUES
 (1,'admin','超级管理员','*',2,NULL,NULL,1503058775,1503456366,0),
 (2,'common','普通用户','2,3,5,7',1,NULL,NULL,1503058775,1503058775,0);

 INSERT INTO `wechat_user` (`id`, `name`, `password` , `roleId`, `status`, `version`) VALUES
 (1,'admin','c3c6b197f4b113bb777cb0f10c0734ef',1,0,0);

