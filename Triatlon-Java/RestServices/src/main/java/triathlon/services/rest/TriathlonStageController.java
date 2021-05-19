package triathlon.services.rest;


import org.apache.catalina.core.ApplicationContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import triathlon.model.Stage;
import triathlon.persistence.StageRepository;
import triathlon.persistence.repository.jdbc.triatlon.StageDBRepository;

@RestController
@RequestMapping("/triathlon/stages")
public class TriathlonStageController {
    private static final String template = "Hello, %s!";

//    ApplicationContext context = new ClassPathXmlApplicationContext("ConfigRepo.xml");
//    private StageRepository stageRepo = context.getBean(StageDBRepository.class);

    @Autowired
    StageRepository stageRepo;

    @RequestMapping("/greeting")
    public String greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return String.format(template, name);
    }

    @RequestMapping( method= RequestMethod.GET)
    public Iterable<Stage> getAll(){
        return stageRepo.findAll();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getById(@PathVariable Integer id){

        Stage stage = stageRepo.findOne(id);
        if (stage == null)
            return new ResponseEntity<String>("Stage not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Stage>(stage, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Stage create(@RequestBody Stage stage){
        stageRepo.save(stage);
        return stage;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Stage update(@RequestBody Stage stage) {
        System.out.println("Updating stage ...");
        stageRepo.update(stage);
        return stage;
    }

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<?> delete(@PathVariable Integer id){
        System.out.println("Deleting stage ... " + id);
        Stage stage = stageRepo.delete(id);
        if (stage == null)
            return new ResponseEntity<String>("Stage not found", HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<Stage>(stage, HttpStatus.OK);
    }
}
