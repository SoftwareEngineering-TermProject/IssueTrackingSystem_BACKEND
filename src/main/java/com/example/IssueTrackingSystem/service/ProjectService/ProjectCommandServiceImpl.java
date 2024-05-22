package com.example.IssueTrackingSystem.service.ProjectService;

import com.example.IssueTrackingSystem.apiPayload.code.status.ErrorStatus;
import com.example.IssueTrackingSystem.apiPayload.exception.handler.IssueHandler;
import com.example.IssueTrackingSystem.apiPayload.exception.handler.ProjectHandler;
import com.example.IssueTrackingSystem.converter.ProjectConverter;
import com.example.IssueTrackingSystem.domain.entity.Project;
import com.example.IssueTrackingSystem.domain.entity.User;
import com.example.IssueTrackingSystem.repository.ProjectRepository;
import com.example.IssueTrackingSystem.repository.UserRepository;
import com.example.IssueTrackingSystem.web.dto.Project.ProjectRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.IssueTrackingSystem.domain.enums.UserRole.ADMIN;
import static com.example.IssueTrackingSystem.domain.enums.UserRole.TESTER;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    public Project createProject(Long userId, ProjectRequestDTO.CreateProjectRequestDTO request){
        // 프론트에서 받은 json으로 Project entity 생성
        Project newProject = ProjectConverter.toProject(request);
        User getUser = userRepository.findById(userId).get();
        newProject.setUser(getUser);

        // admin만 프로젝트 생성 가능
        if(getUser.getUserRole() != ADMIN){
            throw new ProjectHandler(ErrorStatus.PROJECT_CREATE_UNAUTHORIZED);
        }

        // Project entity db에 저장
        Project savedProject = projectRepository.save(newProject);

        return savedProject;
    }

    @Override
    public Project updateProject(Long userId, Long projectId, ProjectRequestDTO.UpdateProjectDTO request) {
        // admin만 프로젝트 수정 가능
        User getUser = userRepository.findById(userId).get();
        if(getUser.getUserRole() != ADMIN){
            throw new ProjectHandler(ErrorStatus.PROJECT_UPDATE_UNAUTHORIZED);
        }

        Project updateProject = projectRepository.findById(projectId).get();
        updateProject.update(request);

        return updateProject;
    }

    @Override
    public void deleteProject(Long userId, Long projectId) {
        // admin만 프로젝트 삭제 가능
        User getUser = userRepository.findById(userId).get();
        if(getUser.getUserRole() != ADMIN){
            throw new ProjectHandler(ErrorStatus.PROJECT_DELETE_UNAUTHORIZED);
        }

        Project deleteProject = projectRepository.findById(projectId).get();
        projectRepository.delete(deleteProject);
    }
}