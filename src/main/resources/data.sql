

INSERT INTO `wechat_user_privilege` VALUES
  (1,'file_upload','文件','上传',1499737601,1499737601,0),
  (2,'login_web','登录','前台应用',1499737601,1499737601,0),
  (3,'user_edit','用户','前台编辑',1499737601,1499737601,0),
  (4,'user_get','用户','前台查询',1499737601,1499737601,0),
  (5,'login_admin','登陆','后台查询',1499737601,1499737601,0),
  (6,'user_register','注册','管理员注册',1499737601,1499737601,0);

INSERT INTO `wechat_user_role` VALUES
 (1,'admin','超级管理员','*',2,NULL,NULL,1503058775,1503456366,0),
 (2,'common','普通用户','1,2,3,4,5',1,NULL,NULL,1503058775,1503058775,0);

 INSERT INTO `wechat_user` (`id`, `name`, `password` , `roleId`, `status`, `version`) VALUES
 (1,'admin','c3c6b197f4b113bb777cb0f10c0734ef',1,0,0);

