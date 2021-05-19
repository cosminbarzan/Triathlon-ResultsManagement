package rest.client;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import triathlon.model.Stage;
import triathlon.services.rest.ServiceException;

import java.util.concurrent.Callable;

public class StagesClient {
    public static final String URL = "http://localhost:8080/triathlon/stages";

    private RestTemplate restTemplate = new RestTemplate();

    private <T> T execute(Callable<T> callable) {
        try {
            return callable.call();
        } catch (ResourceAccessException | HttpClientErrorException e) { // server down, resource exception
            throw new ServiceException(e);
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }

    public Stage[] getAll() {
        return execute(() -> restTemplate.getForObject(URL, Stage[].class));
    }

    public Stage getById(Integer id) {
        return execute(() -> restTemplate.getForObject(String.format("%s/%s", URL, id), Stage.class));
    }

    public Stage create(Stage stage) {
        return execute(() -> restTemplate.postForObject(URL, stage, Stage.class));
    }

    public void update(Stage stage) {
        execute(() -> {
            restTemplate.put(String.format("%s/%s", URL, stage.getId()), stage);
            return null;
        });
    }

    public void delete(Integer id) {
        execute(() -> {
            restTemplate.delete(String.format("%s/%s", URL, id));
            return null;
        });
    }
}
