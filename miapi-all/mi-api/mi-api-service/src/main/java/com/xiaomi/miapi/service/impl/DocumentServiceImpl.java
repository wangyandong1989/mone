package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.pojo.Document;
import com.xiaomi.miapi.pojo.ProjectOperationLog;
import com.xiaomi.miapi.dto.DocumentDTO;
import com.xiaomi.miapi.mapper.DocumentMapper;
import com.xiaomi.miapi.mapper.ProjectOperationLogMapper;
import com.xiaomi.miapi.service.DocumentService;
import com.xiaomi.miapi.common.Consts;
import com.xiaomi.miapi.common.Result;
import com.xiaomi.miapi.common.exception.CommonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackForClassName = "java.lang.Exception")
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private ProjectOperationLogMapper projectOperationLogMapper;

    @Override
    public Result<Boolean> addDocument(Document document, String opUsername) {
        document.setCreateUserName(opUsername);
        if (this.documentMapper.addDocument(document) < 1) {
            return Result.fail(CommonError.UnknownError);
        } else {
            ProjectOperationLog projectOperationLog = new ProjectOperationLog();
            projectOperationLog.setOpProjectID(document.getProjectID());
            projectOperationLog.setOpUsername(opUsername);
            projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT);
            projectOperationLog.setOpTargetID(document.getDocumentID());
            projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_ADD);
            projectOperationLog.setOpDesc("添加项目文档:" + document.getTitle());

            this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
            return Result.success(true);
        }
    }

    @Override
    public Result<Boolean> editDocument(Document document,String opUsername) {
        int affectedRow = this.documentMapper.editDocument(document);
        if (affectedRow < 1) {
            return Result.fail(CommonError.UnknownError);
        } else {
            ProjectOperationLog projectOperationLog = new ProjectOperationLog();
            projectOperationLog.setOpProjectID(document.getProjectID());
            projectOperationLog.setOpUsername(opUsername);
            projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT);
            projectOperationLog.setOpTargetID(document.getDocumentID());
            projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
            projectOperationLog.setOpDesc("修改项目文档:" + document.getTitle());
            this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
            return Result.success(true);
        }
    }

    @Override
    public Result<List<DocumentDTO>> getAllDocumentList(int projectID) {
        List<DocumentDTO> documentList = this.documentMapper.getAllDocumentList(projectID);
        return Result.success(documentList);
    }

    @Override
    public Result<List<DocumentDTO>> searchDocument(int projectID, String tips,Integer type) {
        List<DocumentDTO> resultList;
        if (type == Consts.BY_DOCUMENT_TITLE){
            resultList = this.documentMapper.searchDocumentByTitle(projectID,tips);
        }else if (type == Consts.BY_DOCUMENT_CONTENT){
            resultList = this.documentMapper.searchDocument(projectID,tips);
        }else {
            resultList = this.documentMapper.searchDocument(projectID, tips);
        }

        return Result.success(resultList);
    }

    @Override
    public Result<Map<String, Object>> getDocument(int documentID) {

        Map<String, Object> document = documentMapper.getDocument(documentID);
        if (document != null && !document.isEmpty()) {
            document.put("creator",document.get("username"));
            return Result.success(document);
        } else {
            return Result.fail(CommonError.InvalidParamError);
        }
    }

    @Override
    public Result<Boolean> deleteBatchDocument(List<Integer> documentIDs, int projectID, int userID,String opUsername) {

        String documentTitles = this.documentMapper.getDocumentTitle(documentIDs);
        if (this.documentMapper.deleteDocuments(documentIDs, projectID) < 1) {
            return Result.fail(CommonError.UnknownError);
        } else {
            ProjectOperationLog projectOperationLog = new ProjectOperationLog();
            projectOperationLog.setOpProjectID(projectID);
            projectOperationLog.setOpUsername(opUsername);
            projectOperationLog.setOpTargetID(0);
            projectOperationLog.setOpTarget(ProjectOperationLog.OP_TARGET_PROJECT_DOCUMENT);
            projectOperationLog.setOpType(ProjectOperationLog.OP_TYPE_DELETE);
            projectOperationLog.setOpDesc("删除项目文档:" + documentTitles);

            this.projectOperationLogMapper.addProjectOperationLog(projectOperationLog);
            return Result.success(true);
        }
    }

}
