package com.example.IssueTrackingSystem.service.ProjectService;

import com.example.IssueTrackingSystem.apiPayload.code.status.ErrorStatus;
import com.example.IssueTrackingSystem.apiPayload.exception.handler.ProjectHandler;
import com.example.IssueTrackingSystem.converter.ProjectUserConverter;
import com.example.IssueTrackingSystem.converter.ProjectConverter;
import com.example.IssueTrackingSystem.domain.entity.Project;
import com.example.IssueTrackingSystem.domain.entity.User;
import com.example.IssueTrackingSystem.domain.entity.mapping.ProjectUser;
import com.example.IssueTrackingSystem.domain.enums.Admin;
import com.example.IssueTrackingSystem.domain.enums.UserRole;
import com.example.IssueTrackingSystem.repository.ProjectUserRepository;
import com.example.IssueTrackingSystem.repository.ProjectRepository;
import com.example.IssueTrackingSystem.repository.UserRepository;
import com.example.IssueTrackingSystem.web.dto.Project.ProjectRequestDTO;
import com.example.IssueTrackingSystem.web.dto.ProjectUser.ProjectUserRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectCommandServiceImpl implements ProjectCommandService{

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final ProjectUserRepository projectUserRepository;

    @Override
    public Project createProject(Long userId, ProjectRequestDTO.CreateProjectRequestDTO request){
        // 프론트에서 받은 json으로 Project entity 생성
        Project newProject = ProjectConverter.toProject(request);
        User getUser = userRepository.findById(userId).get();
        newProject.setUser(getUser);

        // admin만 프로젝트 생성 가능
        if(getUser.getAdmin() == Admin.FALSE){
            throw new ProjectHandler(ErrorStatus.PROJECT_CREATE_UNAUTHORIZED);
        }

        // Project entity db에 저장
        Project savedProject = projectRepository.save(newProject);

        // ProjectUser생성 및 DB에 저장
        ProjectUser newProjectUser = ProjectUser.builder()
                .user(getUser)
                .userName(getUser.getUserName())
                .userRole(UserRole.ADMIN)
                .project(savedProject)
                .build();
        projectUserRepository.save(newProjectUser);

        return savedProject;
    }

    @Override
    public Project updateProject(Long userId, Long projectId, ProjectRequestDTO.UpdateProjectDTO request) {
        // admin만 프로젝트 수정 가능
        User getUser = userRepository.findById(userId).get();
        if(getUser.getAdmin() == Admin.FALSE){
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
        if(getUser.getAdmin() == Admin.FALSE){
            throw new ProjectHandler(ErrorStatus.PROJECT_DELETE_UNAUTHORIZED);
        }

        Project deleteProject = projectRepository.findById(projectId).get();
        projectRepository.delete(deleteProject);
    }

    @Override
    public ProjectUser addUser(Long userId, ProjectUserRequestDTO.AddUserDTO request){
        ProjectUser newProjectUser = ProjectUserConverter.toProjectUser(request);
        User getUser = userRepository.findById(userId).get();
        Project getProject = projectRepository.findById(request.getProjectId()).get();
        User adminUser = userRepository.findById(request.getAdminId()).get();

        // 계정 추가를 시행하는 유저가 admin인지 확인
        if(adminUser.getAdmin() == Admin.FALSE){
            throw new ProjectHandler(ErrorStatus.PROJECT_ADDUSER_UNAUTHORIZED);
        }

        newProjectUser.setUser(getUser);
        newProjectUser.setProject(getProject);
        newProjectUser.setUserName(getUser.getUserName());

        ProjectUser savedProjectUser = projectUserRepository.save(newProjectUser);
        return savedProjectUser;
    }
    

    @Override
    public void deleteUserInProject(Long userId, Long projectId, Long adminId){  //ProjectRequestDTO.deleteUserInProjectDTO request
        User getUser = userRepository.findById(adminId).orElseThrow(() -> new RuntimeException("User not found"));

        // admin만 프로젝트 삭제 가능
        if(getUser.getAdmin() == Admin.FALSE){
            throw new ProjectHandler(ErrorStatus.PROJECT_DELETE_UNAUTHORIZED);
        }

        Project getProject = projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("Project not found"));
        User userToDelete = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        projectUserRepository.deleteByUserAndProject(userToDelete, getProject);
    }
}
