package com.taskflow.taskflow_backend.service;

import java.util.List;

import com.taskflow.taskflow_backend.dto.request.CreatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.request.UpdatePersonalTaskRequest;
import com.taskflow.taskflow_backend.dto.response.PersonalTaskResponse;

public interface TaskService {
    // Personal task methods
    PersonalTaskResponse createPersonalTask(CreatePersonalTaskRequest request);
    PersonalTaskResponse updatePersonalTask(Long taskId, UpdatePersonalTaskRequest request);
    PersonalTaskResponse getPersonalTaskById(Long taskId);
    List<PersonalTaskResponse> getAllPersonalTask();
    void deletePersonalTaskById(Long taskId);
}
