package com.kdatalab.bridge.adminpage.projectregistration.repository;

import com.kdatalab.bridge.mypage.model.RootEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ProjectRegistrationRepository extends JpaRepository<RootEntity, Integer> {


    @Query(value = "{call generate_tasks(?1,?2,?3)}", nativeQuery = true)
    void saveTbTaskDtl(Integer projectId, String regUser, Integer taskUnit);

    @Query(value = "{call save_tb_edu_mst(?1,?2,?3,?4,?5,?6,?7,?8,?9)}", nativeQuery = true)
    Integer saveProject(String projectType, String projectName, String projectContent, Integer workDateInMinutes, LocalDate projectStartDate,
                        LocalDate projectEndDate, Integer pointPerImage, Integer taskUnit, String regUser);

    @Query(value = "{call save_tb_att(?1,?2,?3,?4,?5,?6,?7)}", nativeQuery = true)
    void saveTbAtt(Integer projectId, Integer taskId, String fileName, String filePath, Long fileSize, String fileExt, String regUser);

    @Query(value = "select DTL_SEQ from TB_TASK_DTL where EDU_SEQ = ?1", nativeQuery = true)
    List<Integer> getTasksByProjectId(Integer projectId);

    @Query(value = "{call update_tb_edu_mst(?1,?2,?3,?4,?5,?6,?7)}", nativeQuery = true)
    void updateProject(Integer projectId, String projectName, String projectContent, Integer workDateInMinutes, LocalDate projectStartDate, LocalDate projectEndDate, String userId);

    @Query(value = "select count(*) from TB_EDU_MST where lower(SUBJECT) = ?1", nativeQuery = true)
    int existProjectName(String projectName);

    @Query(value = "select EDU_SEQ, EDU_TYPE, SUBJECT, CONTENT, TIME, START_DATE, END_DATE, POINT, TASK_UNIT " +
                     "from TB_EDU_MST where EDU_SEQ = ?1", nativeQuery = true)
    Map<String,Object> getProjectDetails(Integer projectId);

    @Query(value = "{call task_assigned(:tasks, :name)}", nativeQuery = true)
    int saveAssignedUsers(String tasks, String name);
    @Query(value = "select count(*) " +
                     "from TB_TASK_DTL " +
                    "where EDU_SEQ = ?1 " +
                      "and TASK_DTL_PROG > 0", nativeQuery = true)
    int getWorkingTaskCount(Integer projectId);

    @Query(value = "call delete_project(?1)", nativeQuery = true)
    void deleteProject(Integer projectId);
}
