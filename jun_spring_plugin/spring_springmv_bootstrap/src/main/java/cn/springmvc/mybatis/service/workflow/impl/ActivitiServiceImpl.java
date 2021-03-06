package cn.springmvc.mybatis.service.workflow.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import javax.annotation.Resource;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.identity.Group;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.springmvc.mybatis.entity.activiti.LeaveBill;
import cn.springmvc.mybatis.entity.activiti.WorkflowBean;
import cn.springmvc.mybatis.entity.auth.Role;
import cn.springmvc.mybatis.entity.auth.User;
import cn.springmvc.mybatis.mapper.activiti.LeaveBillMapper;
import cn.springmvc.mybatis.service.auth.AuthService;
import cn.springmvc.mybatis.service.workflow.ActivitiService;

@Service
public class ActivitiServiceImpl implements ActivitiService {

    @Autowired
    private LeaveBillMapper leaveBillMapper;

    @Resource
    private ProcessEngine processEngine;

    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private TaskService taskService;

    @Resource
    private FormService formService;

    @Resource
    private HistoryService historyService;

    @Autowired
    private AuthService authService;

    // ???????????????????????????
    @Override
    public List<LeaveBill> findLeaveBillList() {
        return leaveBillMapper.findAll();
    }

    // ???????????????ID?????????????????????
    @Override
    public LeaveBill findLeaveBillById(Long leaveBillId) {
        return leaveBillMapper.findById(leaveBillId);
    }

    /** ?????????????????? */
    @Override
    public void saveLeaveBill(LeaveBill leaveBill) {
        if (leaveBill != null && null != leaveBill.getId()) {
            leaveBill.setLeaveDate(Calendar.getInstance().getTime());
            leaveBillMapper.update(leaveBill);
        } else {
            // leaveBill.setId("1");
            leaveBillMapper.insert(leaveBill);
        }
    }

    /** ??????????????????ID????????????????????? */
    @Override
    public void deleteLeaveBillById(Long leaveBillId) {
        leaveBillMapper.delete(leaveBillId);
    }

    /** ???????????????????????????????????????act_re_deployment??? */
    @Override
    public List<Deployment> findDeploymentList() {
        List<Deployment> list = repositoryService.createDeploymentQuery()// ????????????????????????
            .orderByDeploymenTime().asc()//
            .list();
        return list;
    }

    /** ??????????????????????????????????????????act_re_procdef??? */
    @Override
    public List<ProcessDefinition> findProcessDefinitionList() {
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()// ????????????????????????
            .orderByProcessDefinitionVersion().asc()//
            .list();
        return list;
    }

    /** ?????????????????? */
    @Override
    public void saveNewDeploye(byte[] bytes, String filename) {
        try {
            // 2??????byte????????????????????????ZipInputStream???
            InputStream stream = new ByteArrayInputStream(bytes);
            ZipInputStream zipInputStream = new ZipInputStream(stream);
            repositoryService.createDeployment()// ??????????????????
                .name(filename)// ??????????????????
                .addZipInputStream(zipInputStream)//
                .deploy();// ????????????
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ??????????????????ID????????????????????? */
    @Override
    public void deleteProcessDefinitionByDeploymentId(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
    }

    /** ??????????????????ID???????????????????????????????????????????????? */
    @Override
    public InputStream findImageInputStream(String deploymentId, String imageName) {
        return repositoryService.getResourceAsStream(deploymentId, imageName);
    }

    /** ?????????????????????????????????????????????????????????????????????????????? */
    @Override
    public void saveStartProcess(WorkflowBean workflowBean, User user) {
        // 1??????????????????ID??????????????????ID???????????????????????????LeaveBill
        Long id = workflowBean.getId();
        LeaveBill leaveBill = leaveBillMapper.findById(id);
        // 2????????????????????????????????????0??????1???????????????-->????????????
        leaveBill.setState(1);
        leaveBillMapper.update(leaveBill);

        // 3?????????????????????????????????????????????key???????????????????????????????????????key???
        String key = leaveBill.getClass().getSimpleName();
        /**
         * 4??????Session??????????????????????????????????????????????????????????????????????????????????????? inputUser??????????????????????????? ???????????????????????????????????????
         */
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("inputUser", user.getId());// ??????????????????
        /**
         * 5??? (1)?????????????????????????????????????????????LeaveBill.id?????????????????????????????????????????????????????????????????????????????? <br/>
         * (2)?????????????????????????????????????????????BUSINESS_KEY???Activiti???????????????????????????????????????????????????????????????????????????
         */
        // ?????????LeaveBill.id?????????????????????????????????
        String objId = key + "." + id;
        variables.put("objId", objId);
        // 6????????????????????????key??????????????????????????????????????????????????????????????????????????????????????????????????????BUSINESS_KEY????????????????????????????????????????????????
        runtimeService.startProcessInstanceByKey(key, objId, variables);

        IdentityService identityService = processEngine.getIdentityService();//

        // ???????????????????????????????????????
        String manager_role = "manager_role";
        String boss_role = "boss_role";
        Role managerRole = authService.findRoleByRoleCode(manager_role);
        Role bossRole = authService.findRoleByRoleCode(boss_role);

        Group manager = identityService.createGroupQuery().groupId(managerRole.getCode()).singleResult();
        if (manager == null) {
            // ????????????
            identityService.saveGroup(new GroupEntity(managerRole.getCode()));// ????????????????????????==????????????????????????????????????????????????

            List<User> mgUsers = authService.findUserByRoleCode(manager_role);// ?????????????????????
            for (User u : mgUsers) {
                org.activiti.engine.identity.User aUser = identityService.createUserQuery().userId(u.getId()).singleResult();
                if (aUser == null) {
                    identityService.saveUser(new UserEntity(u.getId()));// ==?????????????????????????????????????????????
                    identityService.createMembership(u.getId(), managerRole.getCode());// ==???????????????????????????????????????????????????
                }
            }
        }

        Group boss = identityService.createGroupQuery().groupId(bossRole.getCode()).singleResult();
        if (boss == null) {
            identityService.saveGroup(new GroupEntity(bossRole.getCode()));// ?????????????????????==????????????????????????????????????????????????
            List<User> bUsers = authService.findUserByRoleCode(boss_role);// ??????????????????
            for (User u : bUsers) {
                org.activiti.engine.identity.User aUser = identityService.createUserQuery().userId(u.getId()).singleResult();
                if (aUser == null) {
                    identityService.saveUser(new UserEntity(u.getId()));// ==?????????????????????????????????????????????
                    identityService.createMembership(u.getId(), bossRole.getCode());// ==???????????????????????????????????????????????????
                }
            }
        }

    }

    /** 2????????????????????????????????????????????????????????????????????????????????????List<Task> */
    @Override
    public List<Task> findTaskListByName(String name) {
        List<Task> list = taskService.createTaskQuery()//
            .taskAssignee(name)// ????????????????????????
            .orderByTaskCreateTime().asc()//
            .list();
        return list;
    }

    /** ????????????ID???????????????????????????????????????Form key?????????????????? */
    @Override
    public String findTaskFormKeyByTaskId(String taskId) {
        TaskFormData formData = formService.getTaskFormData(taskId);
        // ??????Form key??????
        String url = formData.getFormKey();
        return url;
    }

    /** ??????????????????ID??????????????????ID?????????????????????????????? */
    @Override
    public LeaveBill findLeaveBillByTaskId(String taskId) {
        // 1???????????????ID?????????????????????Task
        Task task = taskService.createTaskQuery()//
            .taskId(taskId)// ????????????ID??????
            .singleResult();
        // 2?????????????????????Task??????????????????ID
        String processInstanceId = task.getProcessInstanceId();
        // 3?????????????????????ID??????????????????????????????????????????????????????????????????
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
            .processInstanceId(processInstanceId)// ??????????????????ID??????
            .singleResult();
        // 4?????????????????????????????????BUSINESS_KEY
        String buniness_key = pi.getBusinessKey();
        // 5?????????BUSINESS_KEY???????????????ID???????????????ID???????????????????????????LeaveBill.1???
        String id = "";
        if (StringUtils.isNotBlank(buniness_key)) {
            // ?????????????????????buniness_key???????????????2??????
            id = buniness_key.split("\\.")[1];
        }
        // ?????????????????????
        // ??????hql?????????from LeaveBill o where o.id=1
        LeaveBill leaveBill = leaveBillMapper.findById(Long.valueOf(id));
        return leaveBill;
    }

    /** ??????????????????ID?????????ProcessDefinitionEntiy???????????????????????????????????????????????????????????????????????????List<String>????????? */
    @Override
    public List<String> findOutComeListByTaskId(String taskId) {
        // ?????????????????????????????????
        List<String> list = new ArrayList<String>();
        // 1:????????????ID?????????????????????
        Task task = taskService.createTaskQuery()//
            .taskId(taskId)// ????????????ID??????
            .singleResult();
        // 2?????????????????????ID
        String processDefinitionId = task.getProcessDefinitionId();
        // 3?????????ProcessDefinitionEntiy??????
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        // ??????????????????Task??????????????????ID
        String processInstanceId = task.getProcessInstanceId();
        // ??????????????????ID??????????????????????????????????????????????????????????????????
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
            .processInstanceId(processInstanceId)// ??????????????????ID??????
            .singleResult();
        // ?????????????????????id
        String activityId = pi.getActivityId();
        // 4????????????????????????
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);
        // 5????????????????????????????????????????????????
        List<PvmTransition> pvmList = activityImpl.getOutgoingTransitions();
        if (pvmList != null && pvmList.size() > 0) {
            for (PvmTransition pvm : pvmList) {
                String name = (String) pvm.getProperty("name");
                if (StringUtils.isNotBlank(name)) {
                    list.add(name);
                } else {
                    list.add("????????????");
                }
            }
        }
        return list;
    }

    /** ?????????????????????????????????????????????ID?????????????????????ID??????????????? */
    @Override
    public List<Comment> findCommentByTaskId(String taskId) {
        List<Comment> list = new ArrayList<Comment>();
        // ?????????????????????ID??????????????????????????????????????????ID
        // ??????????????????ID???????????????????????????
        Task task = taskService.createTaskQuery()//
            .taskId(taskId)// ????????????ID??????
            .singleResult();
        // ??????????????????ID
        String processInstanceId = task.getProcessInstanceId();
        // //??????????????????ID???????????????????????????????????????????????????????????????ID
        // List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()//?????????????????????
        // .processInstanceId(processInstanceId)//??????????????????ID??????
        // .list();
        // //?????????????????????????????????ID
        // if(htiList!=null && htiList.size()>0){
        // for(HistoricTaskInstance hti:htiList){
        // //??????ID
        // String htaskId = hti.getId();
        // //??????????????????
        // List<Comment> taskList = taskService.getTaskComments(htaskId);//??????????????????????????????ID
        // list.addAll(taskList);
        // }
        // }
        list = taskService.getProcessInstanceComments(processInstanceId);
        return list;
    }

    /** ????????????????????????????????? */
    @Override
    public void saveSubmitTask(WorkflowBean workflowBean) {
        // ????????????ID
        String taskId = workflowBean.getTaskId();
        // ?????????????????????
        String outcome = workflowBean.getOutcome();
        // ????????????
        String message = workflowBean.getComment();
        // ???????????????ID
        Long id = workflowBean.getId();

        /**
         * 1???????????????????????????????????????????????????act_hi_comment????????????????????????????????????????????????????????????????????????
         */
        // ????????????ID????????????????????????????????????????????????ID
        Task task = taskService.createTaskQuery()//
            .taskId(taskId)// ????????????ID??????
            .singleResult();
        // ??????????????????ID
        String processInstanceId = task.getProcessInstanceId();
        /**
         * ???????????????????????????????????????Activiti????????????????????????</br>
         * String userId = Authentication.getAuthenticatedUserId();</br>
         * CommentEntity comment = new CommentEntity();</br>
         * comment.setUserId(userId);</br>
         * ???????????????Session??????????????????????????????????????????????????????????????????????????????act_hi_comment?????????User_ID???????????????????????????????????????????????????null</br>
         * ???????????????????????????????????????Authentication.setAuthenticatedUserId();??????????????????????????????</br>
         */
        // Authentication.setAuthenticatedUserId(SessionContext.get().getName());
        taskService.addComment(taskId, processInstanceId, message);
        /**
         * 2?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????</br>
         * ????????????????????????????????????????????????????????????????????????????????????</br>
         * ????????????????????????outcome</br>
         * ????????????????????????????????????</br>
         */
        Map<String, Object> variables = new HashMap<String, Object>();
        if (outcome != null && !outcome.equals("????????????")) {
            variables.put("outcome", outcome);
        }

        // 3???????????????ID??????????????????????????????????????????????????????
        taskService.complete(taskId, variables);

        // 4?????????????????????????????????????????????????????????????????????????????????-----??????????????????

        /**
         * 5???????????????????????????????????????????????????</br>
         * ??????????????????????????????????????????????????????1??????2????????????-->???????????????
         */
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()//
            .processInstanceId(processInstanceId)// ??????????????????ID??????
            .singleResult();
        // ???????????????
        if (pi == null) {
            // ??????????????????????????????1??????2????????????-->???????????????
            LeaveBill bill = leaveBillMapper.findById(Long.valueOf(id));
            bill.setState(2);
            leaveBillMapper.update(bill);
        }
    }

    /** 1???????????????ID????????????????????????????????????????????????????????????ID??????????????????????????? */
    @Override
    public ProcessDefinition findProcessDefinitionByTaskId(String taskId) {
        // ????????????ID?????????????????????
        Task task = taskService.createTaskQuery()//
            .taskId(taskId)// ????????????ID??????
            .singleResult();
        // ??????????????????ID
        String processDefinitionId = task.getProcessDefinitionId();
        // ???????????????????????????
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()// ??????????????????????????????????????????act_re_procdef
            .processDefinitionId(processDefinitionId)// ??????????????????ID??????
            .singleResult();
        return pd;
    }

    /**
     * ????????????????????????????????????????????????????????????x,y,width,height??????4???????????????Map<String,Object>???</br>
     * map?????????key???????????????x,y,width,height</br>
     * map?????????value???????????????????????????</br>
     */
    @Override
    public Map<String, Object> findCoordingByTask(String taskId) {
        // ????????????
        Map<String, Object> map = new HashMap<String, Object>();
        // ????????????ID?????????????????????
        Task task = taskService.createTaskQuery()//
            .taskId(taskId)// ????????????ID??????
            .singleResult();
        // ?????????????????????ID
        String processDefinitionId = task.getProcessDefinitionId();
        // ??????????????????????????????????????????.bpmn?????????????????????
        ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);
        // ????????????ID
        String processInstanceId = task.getProcessInstanceId();
        // ??????????????????ID???????????????????????????????????????????????????????????????????????????????????????
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()// ????????????????????????
            .processInstanceId(processInstanceId)// ??????????????????ID??????
            .singleResult();
        // ?????????????????????ID
        String activityId = pi.getActivityId();
        // ????????????????????????
        ActivityImpl activityImpl = processDefinitionEntity.findActivity(activityId);// ??????ID
        // ????????????
        map.put("x", activityImpl.getX());
        map.put("y", activityImpl.getY());
        map.put("width", activityImpl.getWidth());
        map.put("height", activityImpl.getHeight());
        return map;
    }

    /** ???????????????ID??????????????????????????? */
    @Override
    public List<Comment> findCommentByLeaveBillId(Long leaveBillId) {
        // ???????????????ID????????????????????????
        LeaveBill leaveBill = leaveBillMapper.findById(leaveBillId);
        // ?????????????????????
        String objectName = leaveBill.getClass().getSimpleName();
        // ????????????????????????????????????
        String objId = objectName + "." + leaveBillId;

        /** 1:??????????????????????????????????????????????????????????????????????????????????????????ID */
        // HistoricProcessInstance hpi = historyService.createHistoricProcessInstanceQuery()//??????????????????????????????
        // .processInstanceBusinessKey(objId)//??????BusinessKey????????????
        // .singleResult();
        // //????????????ID
        // String processInstanceId = hpi.getId();
        /** 2:?????????????????????????????????????????????????????????????????????????????????????????????ID */
        HistoricVariableInstance hvi = historyService.createHistoricVariableInstanceQuery()// ??????????????????????????????
            .variableValueEquals("objId", objId)// ??????????????????????????????????????????????????????
            .singleResult();
        // ????????????ID
        String processInstanceId = hvi.getProcessInstanceId();
        List<Comment> list = taskService.getProcessInstanceComments(processInstanceId);
        return list;
    }

    /** ??????????????????????????? */
    @Override
    public List<Task> findGroupTaskByUserId(String userId) {
        List<Task> list = processEngine.getTaskService()// ???????????????????????????????????????Service
            .createTaskQuery()// ????????????????????????
            /** ???????????????where????????? */
            .taskCandidateUser(userId)// ???????????????????????????
            /** ?????? */
            .orderByTaskCreateTime().asc()// ?????????????????????????????????
            /** ??????????????? */
            .list();// ????????????
        return list;
    }
}
