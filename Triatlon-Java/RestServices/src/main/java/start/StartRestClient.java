package start;

import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import rest.client.StagesClient;
import triathlon.model.Stage;
import triathlon.services.rest.ServiceException;

public class StartRestClient {
    private final static StagesClient stagesClient = new StagesClient();

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        Stage stageT = new Stage("A stage");
        try{
            System.out.println("CREATE");
            show(()-> System.out.println(stagesClient.create(stageT)));

            System.out.println("GET ALL");
            show(()->{
                Stage[] res = stagesClient.getAll();
                for(Stage stage : res){
                    System.out.println(stage.getId() + ": " + stage.getName());
                }
            });

            Stage stage = new Stage("Update Stage");

            stage.setId(9);
            System.out.println("StageT id "+stageT.getId());

            System.out.println("UPDATE");
            stagesClient.update(stage);

            System.out.println("GET BY ID");
            show(()-> System.out.println(stagesClient.getById(stage.getId())));

        }catch(RestClientException ex){
            System.out.println("Exception ... " + ex.getMessage());
        }
    }

    private static void show(Runnable task) {
        try {
            task.run();
        } catch (ServiceException e) {
            //  LOG.error("Service exception", e);
            System.out.println("Service exception" + e);
        }
    }
}
