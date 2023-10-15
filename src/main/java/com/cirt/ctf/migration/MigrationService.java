package com.cirt.ctf.migration;

import com.cirt.ctf.enums.Role;
import com.cirt.ctf.team.TeamDTO;
import com.cirt.ctf.team.TeamEntity;
import com.cirt.ctf.team.TeamService;
import com.cirt.ctf.user.UserDTO;
import com.cirt.ctf.user.UserService;
import com.cirt.ctf.util.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class MigrationService {

    private final ResourceLoader resourceLoader;
    private final TeamService teamService;
    private final UserService userService;

    private static final String FILE_NAME = "team-info.xlsx";

    public int loadUser() {

        int count=0;

        try {

            Resource resource = resourceLoader.getResource("classpath:team-info.xlsx");
            File file = resource.getFile();

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
}
