package com.example.dockercomposepractice.test;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.Objects.isNull;

@Log4j2
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class testController {

    private final testService service;

    @GetMapping("/test")
    public ResponseEntity<?>test(){

        return ResponseEntity.ok("test!");
    }

    @GetMapping("/")
    public ResponseEntity<?>list(){
        List<testEntity>list = service.testEntityList();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?>getOne(@PathVariable("id")Long id){
        testEntity test = service.testEntityByOne(id);
        return ResponseEntity.ok().body(test);
    }

    @PostMapping("/")
    public ResponseEntity<?>createTest(@RequestBody testDto testDto){
        Long createResult = service.createTester(testDto);
        return ResponseEntity.ok().body(createResult);
    }

    @PostMapping("/login")
    public ResponseEntity<?>login(@RequestBody testDto loginDto, HttpSession session){
        return new ResponseEntity<>(service.login(session,loginDto), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<?>logout(HttpSession session){
        service.logout(session);
        session.invalidate();
        return new ResponseEntity<>("logout",HttpStatus.OK);
    }

    @GetMapping("/check-user")
    public ResponseEntity<?>memberCheck(HttpSession httpSession){
        String result;
        testEntity test = new testEntity();
        Object currentUser = httpSession.getAttribute("test");
        String id = httpSession.getId();
        log.info(currentUser);
        if(isNull(currentUser)){
            result = "로그인이 되어 있지 않음";
        }else{
            result = ((testEntity)currentUser).getName();
            test = ((testEntity)currentUser);
            log.info(test);
            log.info(id);
            log.info(httpSession.getAttribute("test"));
        }
        return new ResponseEntity<>(test,HttpStatus.OK);
    }

    @GetMapping("/test-session")
    public ResponseEntity<String>sessionTest(HttpSession session){
        session.setAttribute("member","sharing??");
        String sessionId = session.getId();
        return new ResponseEntity<>(sessionId,HttpStatus.OK);
    }
}
