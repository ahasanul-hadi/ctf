package com.cirt.ctf.migration;

import com.cirt.ctf.challenge.AutoAnswerControllerService;
import com.cirt.ctf.challenge.AutoAnswerDTO;
import com.cirt.ctf.challenge.AutoAnswerEntity;
import com.cirt.ctf.challenge.ChallengeDTO;
import com.cirt.ctf.challenge.ChallengeService;
import com.cirt.ctf.enums.Role;
import com.cirt.ctf.hints.HintsDTO;
import com.cirt.ctf.team.TeamDTO;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.team.TeamService;
import com.cirt.ctf.user.UserDTO;
import com.cirt.ctf.user.UserService;
import com.cirt.ctf.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MigrationService {

    @Value("${document.upload.directory:/uploads}")
    private String UPLOAD_DIR;

    private final ResourceLoader resourceLoader;
    private final TeamService teamService;
    private final UserService userService;
    private final ChallengeService challengeService;
    private final AutoAnswerControllerService autoAnswerService;

    private static final String REGISTRATION_FILE_NAME = "team-info.xlsx";
    private static final String TEAMWISE_ANSWER_FILE_NAME = "team-answers.xlsx";

    public int loadUser() {
        String absPath=null;
        try{
            Path root = Paths.get(UPLOAD_DIR);
            absPath= root.resolve(REGISTRATION_FILE_NAME).toAbsolutePath().toString();
        }catch(Exception e){}

        int count=0;

        try {


            File file=null;

            try{
                file= new File(absPath);
            }catch (Exception e){}

            try{
                if(!file.exists()){
                    Resource resource = resourceLoader.getResource("classpath:team-info.xlsx");
                    file = resource.getFile();
                }

            }catch (Exception e){}



           // File file = resource.getFile();

            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();

            //skip header
            iterator.next();

            System.out.println("total rows:"+datatypeSheet.getLastRowNum());

            while (iterator.hasNext()) {

                Row currentRow = iterator.next();

                Integer team_id = (int) currentRow.getCell(0).getNumericCellValue();
                Integer user_id = (int) currentRow.getCell(1).getNumericCellValue();

                String team_name = currentRow.getCell(2).getStringCellValue();
                String org_name = currentRow.getCell(3).getStringCellValue();
                String fullName = currentRow.getCell(4).getStringCellValue();
                String email = currentRow.getCell(5).getStringCellValue();
                String password= currentRow.getCell(6).getStringCellValue();

                TeamDTO teamDTO= teamService.findById(Long.valueOf(team_id));

                //Team  Leader
                if(teamDTO==null){
                    teamDTO= new TeamDTO();
                    teamDTO.setId(Long.valueOf(team_id));
                    teamDTO.setTeamName(team_name);
                    teamDTO.setTeamOrganization(org_name);
                    teamDTO.setOrderID(Utils.getRandomPassword(8));
                    teamDTO.setPaymentEmail(Utils.getRandomPassword(8)+"@cirt.gov.bd");
                    TeamEntity entity= teamService.save(teamDTO);

                    UserDTO userDTO= UserDTO.builder()
                            .id(Long.valueOf(user_id))
                            .name(fullName)
                            .email(email)
                            .mobile("01xxxxxxx")
                            .team(entity)
                            .role(Role.TEAM_LEAD)
                            .password(password).build();

                    userService.saveUser(userDTO, null);
                }
                else {
                    TeamEntity entity= teamService.findEntityById(teamDTO.getId());
                    UserDTO userDTO= UserDTO.builder()
                            .id(Long.valueOf(user_id))
                            .name(fullName)
                            .email(email)
                            .team(entity)
                            .mobile("01xxxxxxx")
                            .role(Role.MEMBER)
                            .password(password).build();

                    userService.saveUser(userDTO, null);
                }


                System.out.println("row: "+currentRow.getRowNum()+" team_id:"+team_id+" team_name:"+" email:"+email+" password:"+password);
                count++;
                if( currentRow.getRowNum()==datatypeSheet.getLastRowNum()){
                    break;
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int loadChallenges() {
        String absPath=null;
        try{
            Path root = Paths.get(UPLOAD_DIR);
            absPath= root.resolve(TEAMWISE_ANSWER_FILE_NAME).toAbsolutePath().toString();
        }catch(Exception e){}

        try {
            File file=null;
            try{
                file= new File(absPath);
            }catch (Exception e){}

            try{
                if(!file.exists()){
                    Resource resource = resourceLoader.getResource("classpath:team-answers.xlsx");
                    file = resource.getFile();
                }

            }catch (Exception e){}

            FileInputStream excelFile = new FileInputStream(file);
            Workbook workbook = new XSSFWorkbook(excelFile);
            Sheet datatypeSheet = workbook.getSheetAt(0);
            Iterator<Row> iterator = datatypeSheet.iterator();
            List<AutoAnswerEntity> autoAnswerEntities = new ArrayList<AutoAnswerEntity>();
            String[] revColumnMap = new String[20];
            Set<Long> challengeIds = new HashSet<Long>();
            int columnCount = 11;
            //get header
            Row header = iterator.next();
            for(int i=0; i<columnCount; i++) {
                revColumnMap[i] = header.getCell(i).getStringCellValue();
            }
            while (iterator.hasNext()) {
                Row currentRow = iterator.next();
                ChallengeDTO challengeDTO = new ChallengeDTO();
                AutoAnswerEntity autoAnswerEntity = new AutoAnswerEntity();
                // in case end of data set
                if(currentRow.getCell(0) == null) {
                    break;
                }
                for(int i=0; i<columnCount; i++) {
                    String currentColumn = revColumnMap[i];
                    log.info(currentColumn);
                    switch (currentColumn) {
                        case "question_title":
                            challengeDTO.setTitle(currentRow.getCell(i).getStringCellValue().trim());
                            break;
                        case "category":
                            challengeDTO.setCategory(currentRow.getCell(i).getStringCellValue().trim());
                            break;
                        case "description":
                            if(currentRow.getCell(i) == null)  challengeDTO.setDescription("");
                            else challengeDTO.setDescription(currentRow.getCell(i).getStringCellValue().trim());
                            break;
                        case "deadline":
                            challengeDTO.setDeadline(currentRow.getCell(i).getStringCellValue().trim());
                            break;
                        case "visibility":
                            challengeDTO.setVisibility(currentRow.getCell(i).getStringCellValue().trim().toLowerCase());
                            break;
                        case "marking_type":
                            challengeDTO.setMarkingType(currentRow.getCell(i).getStringCellValue().trim());
                            break;
                        case "answer":
                            autoAnswerEntity.setAnswer(currentRow.getCell(i).getStringCellValue().trim());
                            break;
                        case "total_mark":
                            challengeDTO.setTotalMark((int) currentRow.getCell(i).getNumericCellValue());
                            break;
                        case "attempt_allowed":
                            if(challengeDTO.getMarkingType().equals("auto"))
                                challengeDTO.setAttempts((int) currentRow.getCell(i).getNumericCellValue());
                            else 
                                challengeDTO.setAttempts(1);
                            break;
                        case "challenge_id":
                            long challenge_id = (long) currentRow.getCell(i).getNumericCellValue();
                            challengeDTO.setId(challenge_id);
                            autoAnswerEntity.setChallengeId(challenge_id);
                            break;
                        case "team_id":
                            autoAnswerEntity.setTeamId((long) currentRow.getCell(i).getNumericCellValue());
                            break;
                        default:
                            break;
                    }
                }
                if(!challengeIds.contains(challengeDTO.getId())) {
                    challengeIds.add(challengeDTO.getId());
                    log.info("New Challenge "+challengeDTO.getTitle());
                    challengeDTO.setHint(new HintsDTO());
                    this.challengeService.saveChallengeFromExcel(challengeDTO);
                }
                if(challengeDTO.getMarkingType().equals("auto")) {
                    autoAnswerEntities.add(autoAnswerEntity);
                }    
            }
            autoAnswerService.addAllRecords(autoAnswerEntities);
            return autoAnswerEntities.size();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        
    }
}
