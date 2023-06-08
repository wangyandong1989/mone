package com.xiaomi.mone.monitor.service.project.group;

import com.xiaomi.mone.app.api.model.HeraAppBaseInfoModel;
import com.xiaomi.mone.app.api.model.project.group.HeraProjectGroupDataRequest;
import com.xiaomi.mone.app.api.model.project.group.ProjectGroupTreeNode;
import com.xiaomi.mone.app.api.service.HeraAppService;
import com.xiaomi.mone.app.api.service.HeraProjectGroupServiceApi;
import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.monitor.service.model.project.group.ProjectGroupRequest;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gaoxihui
 * @date 2023/6/7 10:59 上午
 */
@Service
public class ProjectGroupService {

    @Reference(registry = "registryConfig", check = false, interfaceClass = HeraProjectGroupServiceApi.class, group = "${dubbo.group.heraapp}")
    HeraProjectGroupServiceApi projectGroupServiceApi;


    public Result<ProjectGroupTreeNode> getFullTree(Integer type){
        return projectGroupServiceApi.getFullTree(type);
    }

    public Result<ProjectGroupTreeNode> getTreeByUser(ProjectGroupRequest request){
        return projectGroupServiceApi.getTreeByUser(request.getUser(),request.getGroupType(),request.getProjectGroupName());
    }

    public Result<List<HeraAppBaseInfoModel>> searchGroupApps(ProjectGroupRequest request){
        return projectGroupServiceApi.searchGroupApps(request.getUser(),request.getGroupType(),request.getProjectGroupId(),request.getAppName(),request.getPage(),request.getPageSize());

    }

    public Result<Integer> create(HeraProjectGroupDataRequest request){
        return projectGroupServiceApi.create(request);
    }

    public Result<Integer> update(HeraProjectGroupDataRequest request){
        return projectGroupServiceApi.update(request);
    }

    public Result<Integer> delete(Integer id){
        return projectGroupServiceApi.delete(id);
    }
}
