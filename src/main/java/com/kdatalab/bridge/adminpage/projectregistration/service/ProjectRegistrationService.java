package com.kdatalab.bridge.adminpage.projectregistration.service;

import com.kdatalab.bridge.adminpage.projectregistration.dto.AttachmentDto;
import com.kdatalab.bridge.adminpage.projectregistration.dto.ProjectRegistrationDto;
import com.kdatalab.bridge.adminpage.projectregistration.dto.TaskDto;
import com.kdatalab.bridge.adminpage.projectregistration.mapper.ProjectRegistrationMapper;
import com.kdatalab.bridge.adminpage.projectregistration.repository.ProjectRegistrationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectRegistrationService {

    private final ProjectRegistrationRepository projectRegistrationRepository;

    private final AWSService awsService;
    private final ProjectRegistrationMapper mapper;


    public void registerTask(Integer projectId, String regUser, Integer taskUnit) {
        projectRegistrationRepository.saveTbTaskDtl(projectId, regUser, taskUnit);
    }

    public void createProject(ProjectRegistrationDto dto) throws IOException {
        if(dto.getProjectId() == null) {
            // create project
            projectRegistrationRepository.saveProject(dto.getProjectType(), dto.getProjectName(), dto.getProjectContent(), dto.getWorkDateInMinutes(), dto.getProjectStartDate(),
                    dto.getProjectEndDate(), dto.getPointPerImage(), dto.getTaskUnit());
            registerTask(dto.getProjectId(), "yanghee", dto.getTaskUnit());
            // save images db and s3
            attachFileToTask(dto.getProjectId(), "yanghee", dto.getFiles());

        } else {
            //projectRegistrationRepository.updateTbProject(dto);
        }
    }

    public void attachFileToTask(Integer projectId, String regUser, List<MultipartFile> files) throws IOException {
        // list of tasks
        String projectName = "project3";
        List<Integer> taskIds = Arrays.asList(4,5,6);//,7,8);
        int taskUnit = 3;
        int length = files.size();//17
        int taskCount = length / taskUnit;//5
        int remainder = length % taskUnit; //2
        for (int i = 0; i < taskUnit; i++) {
            int taskId = taskIds.get(i);
            for(int j = 0; j < taskCount; j++) {
                AttachmentDto attachmentDto = new AttachmentDto();
                attachmentDto.setProjectId(projectId); //EDU_SEQ
                attachmentDto.setTaskId(taskId); //DTL_SEQ
                attachmentDto.setFileName(files.get(i+j).getOriginalFilename()); //NAME
                attachmentDto.setFilePath(generateTaskPath(projectName, taskId));//PATH
                attachmentDto.setFileExt(files.get(i+j).getContentType());//EXT
                attachmentDto.setFileSize(files.get(i+j).getSize());//SIZE
                attachmentDto.setRegUser(regUser);//REG_USER
                attachmentDto.setRegDate(LocalDateTime.now().toString());//REG_DATE
                attachmentDto.setModUser(regUser);//MOD_USER
                attachmentDto.setModDate(LocalDateTime.now().toString());//MOD_DATE
                System.out.println("attachmentDto = " + attachmentDto);
                //save to db image data
                saveImageData(attachmentDto);
                //save to s3 image
                awsService.uploadImageToAws(files.get(i+j),attachmentDto.getFilePath());
            }
        }
        for(int i = 0; i < remainder; i++) {
            int taskId = taskIds.get(i);
            AttachmentDto attachmentDto = new AttachmentDto();
            attachmentDto.setProjectId(projectId); //EDU_SEQ
            attachmentDto.setTaskId(taskId); //DTL_SEQ
            attachmentDto.setFileName(files.get(taskUnit*taskCount+i).getOriginalFilename()); //NAME
            attachmentDto.setFilePath(generateTaskPath(projectName, taskId));//PATH
            attachmentDto.setFileSize(files.get(taskUnit*taskCount+i).getSize());//SIZE
            attachmentDto.setFileExt(files.get(taskUnit*taskCount+i).getContentType());//EXT
            attachmentDto.setRegUser(regUser);//REG_USER
            attachmentDto.setRegDate(LocalDateTime.now().toString());//REG_DATE
            attachmentDto.setModUser(regUser);//MOD_USER
            attachmentDto.setModDate(LocalDateTime.now().toString());//MOD_DATE
            System.out.println("attachmentDto = " + attachmentDto);
            //save to db image data
            saveImageData(attachmentDto);
            //save to aws s3 image
            awsService.uploadImageToAws(files.get(taskUnit*taskCount+i),attachmentDto.getFilePath());
        }

    }

    public void saveImageData(AttachmentDto attachmentDto) {
        //save to db image data
        projectRegistrationRepository.saveTbAtt(attachmentDto.getProjectId(), attachmentDto.getTaskId(), attachmentDto.getFileName(),
                attachmentDto.getFilePath(), attachmentDto.getFileSize(), attachmentDto.getFileExt(), attachmentDto.getRegUser(),
                attachmentDto.getRegDate(), attachmentDto.getModUser(), attachmentDto.getModDate());
    }

    private String generateTaskPath(String projectName, Integer taskId) {
        return "module/" + projectName + "/" + "task" + taskId;
    }

    public List<TaskDto> getProjectWithTasks(Long projectId) {
        return mapper.selectTasksByProjectId(projectId);
    }
}
