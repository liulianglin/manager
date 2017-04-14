import java.io.FileInputStream;  
import java.util.List;  

import org.activiti.engine.HistoryService;  
import org.activiti.engine.ProcessEngine;  
import org.activiti.engine.ProcessEngineConfiguration;  
import org.activiti.engine.RepositoryService;  
import org.activiti.engine.RuntimeService;
//import org.activiti.engine.RuntimeService;  
import org.activiti.engine.TaskService;  
import org.activiti.engine.history.HistoricProcessInstance;  
import org.activiti.engine.runtime.ProcessInstance;  
import org.activiti.engine.task.Task;  
  
/** 
 * Activiti 5.10 demo 
 * @author BruceQin 
 *  
 */  
public class DemoProcessTest {  
    // diagrams实际路径  
    private static String realPath =   
    		"F:\\java_workspace\\ActivitiFirst\\src\\main\\resources\\diagrams";  
    public static void main(String[] args) throws Exception {  
        // 创建 Activiti流程引擎  
    	ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResource("activiti.cfg.xml").buildProcessEngine();
          
        // 获取流程存储服务组件
    	RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();  
  
        // 部署流程定义  
    	repositoryService
    		.createDeployment()
    		.addInputStream("DemoProcess.bpmn",new FileInputStream(realPath+"\\DemoProcess.bpmn"))
    		.deploy();
    	//.addInputStream("DemoProcess.png", new FileInputStream(realPath+<a href="file://\\DemoProcess.png">\\DemoProcess.png</a>))
          
        // 启动流程实例  
    	//ProcessInstance instance = runtimeService.startProcessInstanceById("DemoProcess");
    	ProcessInstance instance = runtimeService.startProcessInstanceByKey("DemoProcess"); 
    	String procId = instance.getId();
    	System.out.println("procId : "+procId);
          
        // 获得第一个任务  
    	TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey("firstTask").list();  
        
        System.out.println("===========");
        for (Task task : tasks) {  
            System.out.println("Following task is: taskID -" +task.getId()+" taskName -"+ task.getName());  
            // 认领任务  
            taskService.claim(task.getId(), "testUser");  
        }  
           
        // 查看testUser 现在是否能够获取到该任务  
        tasks = taskService.createTaskQuery().taskAssignee("testUser").list();  
        for (Task task : tasks) {  
            System.out.println("Task for testUser: " + task.getName());  
            // 完成任务  
            taskService.complete(task.getId());  
        }  
        System.out.println("Number of tasks for testUser: "  
                + taskService.createTaskQuery().taskAssignee("testUser").count());  
          
  
        // 获取并认领第二个任务  
        tasks = taskService.createTaskQuery().taskDefinitionKey("secondTask").list();  
        for (Task task : tasks) {  
            System.out.println("Following task is : taskID -" +task.getId()+" taskName -"+ task.getName());  
            taskService.claim(task.getId(), "testUser");  
        }  
          
        //完成第二个任务结束结束流程  
        for (Task task : tasks) {  
            taskService.complete(task.getId());  
        }  
          
        // 核实流程是否结束  
        HistoryService historyService = processEngine.getHistoryService();  
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procId).singleResult();  
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());  
    
    }  
}  