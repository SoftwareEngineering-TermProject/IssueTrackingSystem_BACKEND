package com.example.IssueTrackingSystem.service.IssueService;

import com.example.IssueTrackingSystem.domain.entity.Issue;
import com.example.IssueTrackingSystem.domain.entity.Project;
import com.example.IssueTrackingSystem.repository.IssueRepository;
import com.example.IssueTrackingSystem.repository.ProjectRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class IssueQueryServiceImplTest {
    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private IssueRepository issueRepository;

    @InjectMocks
    private IssueQueryServiceImpl issueQueryServiceImpl;

    @Test
    @DisplayName("모든 이슈 조회 테스트")
    void findAllIssueBySearchTest(){
        // given
        Long projectId = 1L;

        String title = "test";
        String description = "description";
        Optional<String> search = Optional.empty();

        Project testProject = Project.builder()
                .projectId(projectId)
                .title(title)
                .description(description)
                .issueList(new ArrayList<>())
                .projectUserList(new ArrayList<>())
                .build();

        // 예상 이슈 리스트 생성
        int expectedAnswerListSize = 3;

        List<Issue> expectedIssueList = new ArrayList<>();
        for (int i = 1; i <= expectedAnswerListSize; i++) {
            expectedIssueList.add(Issue.builder()
                    .issueId((long) i)
                    .title("Test" + i)
                    .build());
        }

        // projectRepository.findById
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));

        // issueRepository.findAllByProjectOrderByCreatedAtDesc
        when(issueRepository.findAllByProjectOrderByCreatedAtDesc(testProject)).thenReturn(expectedIssueList);

        List<Issue> getIssueList = issueQueryServiceImpl.findAllBySearch(search, projectId);

        // then

        for (int i = 0; i < expectedAnswerListSize; i++) {
            assertEquals(expectedIssueList.get(i).getIssueId(), getIssueList.get(i).getIssueId());
            assertEquals(expectedIssueList.get(i).getTitle(), getIssueList.get(i).getTitle());
        }
    }
}
