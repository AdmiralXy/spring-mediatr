package io.github.admiralxy.mediator.demosb3;

import io.github.admiralxy.mediator.demosb3.request.CreateUserCommand;
import io.github.admiralxy.mediator.demosb3.request.GetUserQuery;
import io.github.admiralxy.mediator.demosb3.request.SendEmailToUserNotification;
import io.github.admiralxy.mediatr.api.Mediator;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@RequiredArgsConstructor
@SpringBootApplication
public class DemoSb3Application implements CommandLineRunner {

    private final Mediator mediator;

    public static void main(String[] args) {
        var context = SpringApplication.run(DemoSb3Application.class, args);
        SpringApplication.exit(context);
    }

    @Override
    public void run(String... args) {
        var user = mediator.send(new CreateUserCommand("John Doe"));
        System.out.printf("Created user: %s%n", user.getName());

        mediator.sendAsync(new GetUserQuery(user.getName())).thenAccept(u ->
                System.out.printf("User from first async: %s%n", u.getName()));
        mediator.sendAsync(new GetUserQuery(user.getName())).thenAccept(u ->
                System.out.printf("User from second async: %s%n", u.getName()));

        mediator.publishAsync(new SendEmailToUserNotification("example@example.com"));
    }
}
