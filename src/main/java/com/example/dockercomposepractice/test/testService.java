package com.example.dockercomposepractice.test;

import com.example.dockercomposepractice.config.CacheKey;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.apache.catalina.LifecycleState;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional
public class testService {

    private final testEntityRepository testEntityRepository;

    @Transactional(readOnly = true)
    public List<testEntity>testEntityList(){
        List<testEntity>list = testEntityRepository.findAll();
        if(list.isEmpty()){
            throw new RuntimeException("empty");
        }
        return list;
    }

    @Cacheable(value = CacheKey.TEST_KEY)
    public testEntity testEntityByOne(Long id){
        Optional<testEntity>result = Optional.ofNullable(testEntityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("조회된 것이 없습니다.")));
        return result.get();
    }


    public Long createTester(testDto dto){
        testEntity test = testEntity
                .builder()
                .name(dto.getName())
                .password(dto.getPassword())
                .build();
        testEntityRepository.save(test);
        return test.getId();
    }

    @CacheEvict(value = CacheKey.TEST_KEY)
    public void deleteTester(Long id){
        testEntityRepository.deleteById(id);
    }


    public Object login(HttpSession httpSession, testDto testDto){
        testEntity testEntity = testEntityRepository.findByName(testDto.getName());

        if(!testDto.getPassword().equals(testEntity.getPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        httpSession.setAttribute("test",testEntity);

        return httpSession.getId();
    }

    public void logout(HttpSession httpSession){
        httpSession.removeAttribute("test");
    }
}
